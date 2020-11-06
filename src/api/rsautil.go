package api

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"log"
	"os"
)

// SignByPss sign by pss
func SignByPss(content, privateKey []byte) string {
	blockPri, _ := pem.Decode(privateKey)
	if blockPri == nil {
		fmt.Println("err1, invalid privateKey or invalid format")
		return ""
	}

	prkI, err := x509.ParsePKCS8PrivateKey(blockPri.Bytes)
	if err != nil {
		fmt.Println(err)
		fmt.Println("err2")
		return ""
	}
	priKey := prkI.(*rsa.PrivateKey)

	// content - Signature
	var opts rsa.PSSOptions
	opts.SaltLength = rsa.PSSSaltLengthAuto // for simple example
	newhash := crypto.SHA256
	pssh := newhash.New()
	pssh.Write(content)
	hashed := pssh.Sum(nil)

	signature, err := rsa.SignPSS(rand.Reader, priKey, newhash, hashed, &opts)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
	return base64.StdEncoding.EncodeToString(signature)
}

// VerifySignPss verify sign by pss
func VerifySignPss(content, signature, publicKey []byte) bool {
	block, _ := pem.Decode(publicKey)
	ifc, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		log.Fatalf("Error parsing PKCS1 Public Key: %s", err)
		return false
	}
	pubKey, ok := ifc.(*rsa.PublicKey)
	if !ok {
		log.Printf("Error converting to Public Key: %v", err)
		return false
	}

	// Verify Signature
	var opts rsa.PSSOptions
	opts.SaltLength = rsa.PSSSaltLengthAuto // for simple example
	newhash := crypto.SHA256
	pssh := newhash.New()
	pssh.Write(content)
	hashed := pssh.Sum(nil)

	err = rsa.VerifyPSS(pubKey, newhash, hashed, signature, &opts)
	if err != nil {
		fmt.Println("Verify Signature failed")
		return false
	} else {
		fmt.Println("Verify Signature successful")
		return true
	}
}

// Encrypt encrypts the given message with RSA-OAEP
func Encrypt(content, publicKey []byte) []byte {
	block, _ := pem.Decode(publicKey)
	ifc, err := x509.ParsePKIXPublicKey(block.Bytes)
	if err != nil {
		log.Fatalf("Error parsing PKCS1 Public Key: %s", err)
		return nil
	}
	pubKey, ok := ifc.(*rsa.PublicKey)
	if !ok {
		log.Printf("Error converting to Public Key: %v", err)
		return nil
	}

	hash := sha256.New()
	res, err := rsa.EncryptOAEP(hash, rand.Reader, pubKey, content, nil)
	if err != nil {
		log.Println("Encrypt error.", err)
		return nil
	}
	return res
}