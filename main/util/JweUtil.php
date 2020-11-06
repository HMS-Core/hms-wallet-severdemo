<?php
/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

require_once '../../vendor/autoload.php';

/**
 * JWE utility class.
 *
 * @since 2020-05-25
 */
class JweUtil
{
    /**
     * Generate a JSON Web Encryption (JWE).
     *
     * @param string $jwePrivateKey private key used to sign JWE content.
     * @param string $payload information plain text. It can be a list of instance IDs or an instance.
     * @return string the JWE string
     */
    public static function generateJwe(string $jwePrivateKey, string $payload)
    {
        // Part 1: JWE Header.
        // This header contains information about encryption and compression algorithms. It's a constant String.
        echo "Part 1:" . "\n";
        $jweHeader = static::getHeader();
        $jweHeaderEncode = static::getEncodeHeader($jweHeader);
        echo "Encoded header: " . $jweHeaderEncode . "\n";

        // Part 2: JWE Encrypted Key
        // Generate a 16-byte session key to encrypt payload data. Then convert it to a Hex String.
        echo "Part 2:" . "\n";
        try {
            $sessionKey = bin2hex(random_bytes(16));
        } catch (Exception $e) {
            throw new BadFunctionCallException("Get random bytes failed. Error: " . $e->getMessage() . "\n");
        }
        // Encrypt the session key Hex String with Huawei's fixed sessionKeyPublicKey using
        // RSA/NONE/OAEPwithSHA-256andMGF1Padding algorithm, and then do base64 encoding to it.
        echo "sessionKey: " . $sessionKey . "\n";
        $sessionKeyPublicKey = 'MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE=';
        $encryptedKeyEncode = static::getEncryptedKey($sessionKey, $sessionKeyPublicKey);
        echo "Encrypted sessionKey: " . $encryptedKeyEncode . "\n";

        // Part 3: JWE IV
        // Generate a 12-byte iv. Then convert it to a Hex String, and then do base64 encoding to the Hex String.
        echo "Part 3:" . "\n";
        try {
            $ivHexStr = strtoupper(bin2hex(random_bytes(12)));
        } catch (Exception $e) {
            throw new BadFunctionCallException("Get random bytes failed. Error: " . $e->getMessage() . "\n");
        }
        $ivEncode = static::urlSafeBase64Encode($ivHexStr);
        echo "Encoded iv: " . $ivEncode . "\n";

        // Part 4: JWE Cipher Text
        // Encrypt the payload with sessionKey and iv using AES/GCM/NoPadding algorithm. Encode the cipher text into a
        // Hex String. Then do gzip compression and base64 encoding to the Hex String.
        echo "Part 4:" . "\n";
        echo "payload: " . $payload . "\n";
        $cipherTextEncode = static::getCipherText($payload, $sessionKey, $ivHexStr);
        echo "Compressed and encoded cipher text: " . $cipherTextEncode . "\n";

        // Part 5: JWE Signature
        // Use your own private key to sign the content with SHA256withRSA, then do base64 encoding to it.
        // HMS wallet server will use the public key you provided on AGC to verify signatures.
        echo "Part 5:" . "\n";
        $signature = static::getSignature($jwePrivateKey, $sessionKey, $payload, $jweHeaderEncode, $ivEncode);
        echo "signature: " . $signature . "\n\n";

        // Combine all five parts together to form a valid JWE.
        return $jweHeaderEncode . "." . $encryptedKeyEncode . "." . $ivEncode . "." . $cipherTextEncode . "." .
            $signature;
    }

    /**
     * Generate a JWE header.
     *
     * @return array
     */
    private static function getHeader()
    {
        $jweHeader = [
            'alg' => 'RSA-OAEP',
            'enc' => 'A128GCM',
            'kid' => '1',
            'zip' => 'gzip'
        ];
        return $jweHeader;
    }

    /**
     * Encode a JWE header into a base64 string.
     *
     * @param array $jweHeader
     * @return string
     */
    private static function getEncodeHeader(array $jweHeader)
    {
        $headerStr = 'alg=' . $jweHeader['alg'] . ', enc=' . $jweHeader['enc'] . ', kid=' . $jweHeader['kid'] .
            ', zip=' . $jweHeader['zip'];
        echo "Header before encoding: " . $headerStr . "\n";
        // Do base64 encoding.
        return static::urlSafeBase64Encode($headerStr);
    }

    /**
     * Encrypt a sessionKey using RSA/NONE/OAEPwithSHA-256andMGF1Padding algorithm.
     *
     * @param $sessionKey
     * @param $sessionKeyPublicKey
     * @return string
     */
    private static function getEncryptedKey($sessionKey, $sessionKeyPublicKey)
    {
        // Encrypt the sessionKey.
        $rsaObj = new Crypt_RSA();
        $rsaObj->setEncryptionMode(CRYPT_RSA_ENCRYPTION_OAEP);
        $rsaObj->setMGFHash('sha256');
        $rsaObj->setHash('sha256');
        $rsaObj->loadKey($sessionKeyPublicKey);
        $cipher = $rsaObj->encrypt($sessionKey);
        // Do base64.
        $encode = base64_encode($cipher);
        return static::urlSafeBase64Encode($encode);
    }

    /**
     * Encrypt and compress payload.
     *
     * @param $payload
     * @param $sessionKey
     * @param $ivHex
     * @return string
     */
    private static function getCipherText($payload, $sessionKey, $ivHex)
    {
        $payloadEncrypt = static::encryptByGcm($payload, $sessionKey, $ivHex);
        echo "Encrypted payload Hex String: " . $payloadEncrypt . "\n";
        $cipherTextEncode = static::compress($payloadEncrypt);
        return $cipherTextEncode;
    }

    /**
     * AES-GCM encryption
     *
     * @param $plainData
     * @param $key
     * @param $ivHex
     * @return string
     */
    private static function encryptByGcm($plainData, $key, $ivHex)
    {
        $method = 'aes-256-gcm';
        $tag = null;
        $cipherText = openssl_encrypt(
            $plainData,
            $method,
            $key,
            OPENSSL_ZERO_PADDING,
            hex2bin($ivHex),
            $tag
        );
        return strtoupper(bin2hex(base64_decode($cipherText) . $tag));
    }

    /**
     * Gzip compression.
     *
     * @param $dataStr
     * @return string
     */
    private static function compress($dataStr)
    {
        $compressed = gzencode($dataStr);
        return static::urlSafeBase64Encode($compressed);
    }

    /**
     * Create signature.
     *
     * @param $jwePrivateKey
     * @param $sessionKey
     * @param $payload
     * @param $jweHeaderEncode
     * @param $ivEncode
     * @return string
     */
    private static function getSignature($jwePrivateKey, $sessionKey, $payload, $jweHeaderEncode, $ivEncode)
    {
        $content = $jweHeaderEncode . "." . $sessionKey . "." . $ivEncode . "." . $payload;
        echo "Content to be signed: " . $content . "\n";

        $rsa = new Crypt_RSA();
        if ($rsa->loadKey($jwePrivateKey) != TRUE) {
            throw new InvalidArgumentException("Load private key failed.");
        }
        $rsa->setHash("sha256");
        $rsa->setMGFHash("sha256");
        $rsa->setSignatureMode(CRYPT_RSA_SIGNATURE_PSS);
        $sign = $rsa->sign($content);

        return base64_encode($sign);
    }

    private static function urlSafeBase64Encode($data)
    {
        $originalBase64 = base64_encode($data);
        $stripEqual = null;
        if (strlen($data) % 3 == 1) {
            $stripEqual = substr($originalBase64, 0, -2);
        } elseif (strlen($data) % 3 == 2) {
            $stripEqual = substr($originalBase64, 0, -1);
        } else {
            $stripEqual = $originalBase64;
        }
        $urlSafe = str_replace('/', '_', $stripEqual);
        $urlSafe = str_replace('+', '-', $urlSafe);
        return $urlSafe;
    }
}