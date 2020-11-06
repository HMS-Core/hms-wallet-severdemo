package api

import (
	"bytes"
	"compress/gzip"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
	"encoding/hex"
	"io"
	"log"
)

// GenerateJwe generate jwe
func GenerateJwe(jwePrivateKey, payload string) string {
	// Part 1: JWE Header.
	// This header contains information about encryption and compression algorithms. It's a constant String.
	jweHeaderEncode := getEncodeHeader()
	log.Printf("Encoded header: %s", jweHeaderEncode)

	// Part 2: JWE Encrypted Key
	// Generate a 16-byte session key to encrypt payload data. Then convert it to a Hex String.
	sessionKey := hex.EncodeToString(generateSecureRandomFactor(16))

	// Encrypt the session key Hex String with Huawei's fixed sessionKeyPublicKey using
	// RSA/NONE/OAEPwithSHA-256andMGF1Padding algorithm, and then do base64 encoding to it.
	sessionKeyPublicKey := `
-----BEGIN PUBLIC KEY-----
MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE=
-----END PUBLIC KEY-----
`
	encryptedKey := getEncryptedKey(sessionKey, sessionKeyPublicKey)
	encryptedKeyEncode := base64.URLEncoding.EncodeToString([]byte(encryptedKey))

	// Part 3: JWE IV
	// Generate a 12-byte iv. Then convert it to a Hex String, and then do base64 encoding to the Hex String.
	iv := generateSecureRandomFactor(12)
	ivHexStr := hex.EncodeToString(iv)
	ivEncode := base64.URLEncoding.EncodeToString([]byte(ivHexStr))

	// Part 4: JWE Cipher Text
	// Encrypt the payload with sessionKey and iv using AES/GCM/NoPadding algorithm. Encode the cipher text into a
	// Hex String. Then do gzip compression and base64 encoding to the Hex String.
	cipherText := getCipherText(payload, sessionKey, iv)
	cipherTextEncode := base64.URLEncoding.EncodeToString(cipherText)

	// Part 5: JWE Signature
	// Use your own private key to sign the content with SHA256withRSA, then do base64 encoding to it.
	// HMS wallet server will use the public key you provided on AGC to verify signatures.
	signature := getSignature(jwePrivateKey, sessionKey, payload, jweHeaderEncode, ivEncode)

	// Combine all five parts together to form a valid JWE.
	var buffer bytes.Buffer
	buffer.WriteString(jweHeaderEncode)
	buffer.WriteString(".")
	buffer.WriteString(encryptedKeyEncode)
	buffer.WriteString(".")
	buffer.WriteString(ivEncode)
	buffer.WriteString(".")
	buffer.WriteString(cipherTextEncode)
	buffer.WriteString(".")
	buffer.WriteString(signature)
	return buffer.String()
}

func getSignature(jweSignPrivateKey string, sessionKey string, payLoadJson string, jweHeaderEncode string, ivEncode string) string {
	var buffer bytes.Buffer
	buffer.WriteString(jweHeaderEncode)
	buffer.WriteString(".")
	buffer.WriteString(sessionKey)
	buffer.WriteString(".")
	buffer.WriteString(ivEncode)
	buffer.WriteString(".")
	buffer.WriteString(payLoadJson)

	return SignByPss(buffer.Bytes(), []byte(jweSignPrivateKey))
}

func getCipherText(payload string, sessionKey string, iv []byte) []byte {
	block, err := aes.NewCipher([]byte(sessionKey))
	if err != nil {
		panic(err.Error())
	}

	aesgcm, err := cipher.NewGCM(block)
	if err != nil {
		panic(err.Error())
	}

	ciphertext := aesgcm.Seal(nil, iv, []byte(payload), nil)

	var b bytes.Buffer
	w := gzip.NewWriter(&b)
	defer w.Close()
	w.Write([]byte(hex.EncodeToString(ciphertext)))
	w.Flush()

	return b.Bytes()
}

func getEncryptedKey(sessionKey, sessionKeyPublicKey string) string {
	encryptedSessionKey := Encrypt([]byte(sessionKey), []byte(sessionKeyPublicKey))
	return base64.StdEncoding.EncodeToString(encryptedSessionKey)
}

func generateSecureRandomFactor(size int) []byte {
	b := make([]byte, size)
	_, err := io.ReadFull(rand.Reader, b)
	if err != nil {
		log.Fatal("Get rand failed.")
		return nil
	}
	return b
}

func getHeader() map[string]string {
	jweHeader := make(map[string]string, 4)
	jweHeader["alg"] = "RSA-OAEP"
	jweHeader["enc"] = "A128GCM"
	jweHeader["kid"] = "1"
	jweHeader["zip"] = "gzip"
	return jweHeader
}

func getEncodeHeader() string {
	jweHeader := getHeader()
	var buffer bytes.Buffer
	buffer.WriteString("alg=")
	buffer.WriteString(jweHeader["alg"])
	buffer.WriteString(", enc=")
	buffer.WriteString(jweHeader["enc"])
	buffer.WriteString(", kid=")
	buffer.WriteString(jweHeader["kid"])
	buffer.WriteString(", zip=")
	buffer.WriteString(jweHeader["zip"])

	return base64.URLEncoding.EncodeToString(buffer.Bytes())
}
