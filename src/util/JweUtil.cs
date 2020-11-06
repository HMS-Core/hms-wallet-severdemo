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

using System;
using System.IO;
using System.IO.Compression;
using System.Security.Cryptography;
using System.Text;
using Newtonsoft.Json.Linq;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Digests;
using Org.BouncyCastle.Crypto.Encodings;
using Org.BouncyCastle.Crypto.Engines;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Org.BouncyCastle.Security;

namespace hmswallet {
    public class JweUtil {
        /**
         * Generate a JSON Web Encryption (JWE).
         *
         * @param jwePrivateKey private key used to sign JWE content.
         * @param payload information plain text. It can be a list of instance IDs or an instance.
         * @return return a map containing a content string and a signature string.
         */
        public static string GenerateJwe (string jwePrivateKey, string payload) {
            // Part 1: JWE Header.
            // This header contains information about encryption and compression algorithms. It's a constant String.
            Console.WriteLine ("Part 1: ");
            JObject jweHeader = GetHeader ();
            string jweHeaderEncode = GetEncodeHeader (jweHeader);
            Console.WriteLine ("Encoded header: " + jweHeaderEncode);

            // Part 2: JWE Encrypted Key
            // Generate a 16-byte session key to encrypt payload data. Then convert it to a Hex String.
            Console.WriteLine ("Part 2:");
            string sessionKey = GenerateSecureRandomFactor (16);
            // Encrypt the session key Hex String with Huawei's fixed sessionKeyPublicKey using
            // RSA/NONE/OAEPwithSHA-256andMGF1Padding algorithm, and then do base64 encoding to it.
            Console.WriteLine ("sessionKey: " + sessionKey);
            StringBuilder sb = new StringBuilder ()
                .Append ("-----BEGIN PUBLIC KEY-----")
                .Append (System.Environment.NewLine)
                .Append ("MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE=")
                .Append (System.Environment.NewLine)
                .Append ("-----END PUBLIC KEY-----");
            string sessionKeyPublicKey = sb.ToString ();
            string encryptedKeyEncode = GetEncryptedKey (sessionKey, sessionKeyPublicKey);
            Console.WriteLine ("Encrypted sessionKey: " + encryptedKeyEncode);

            // Part 3: JWE IV
            // Generate a 12-byte iv. Then convert it to a Hex String, and then do base64 encoding to the Hex String.
            Console.WriteLine ("Part 3:");
            byte[] iv = GetRandomByte (12);;
            string ivHexStr = BitConverter.ToString (iv).Replace ("-", string.Empty);
            string ivEncode = UrlSafeBase64Encode (Encoding.UTF8.GetBytes (ivHexStr));
            Console.WriteLine ("Encoded iv: " + ivEncode);

            // Part 4: JWE Cipher Text
            // Encrypt the payload with sessionKey and iv using AES/GCM/NoPadding algorithm. Encode the cipher text into a
            // Hex String. Then do gzip compression and base64 encoding to the Hex String.
            Console.WriteLine ("Part 4:");
            Console.WriteLine ("payload: " + payload);
            string cipherTextEncode = GetCipherText (payload, sessionKey, iv);
            Console.WriteLine ("Compressed and encoded cipher text: " + cipherTextEncode);

            // Part 5: JWE Signature
            // Use your own private key to sign the content with SHA256withRSA, then do base64 encoding to it.
            // HMS wallet server will use the public key you provided on AGC to verify signatures.
            Console.WriteLine ("Part 5:");
            string signature = GetSignature (jwePrivateKey, sessionKey, payload, jweHeaderEncode, ivEncode);
            Console.WriteLine ("signature: " + signature + "\n");

            // Combine all five parts together to form a valid JWE.
            StringBuilder jwe = new StringBuilder ()
                .Append (jweHeaderEncode)
                .Append (".")
                .Append (encryptedKeyEncode)
                .Append (".")
                .Append (ivEncode)
                .Append (".")
                .Append (cipherTextEncode)
                .Append (".")
                .Append (signature);
            return jwe.ToString ();
        }

        private static string GenerateSecureRandomFactor (int size) {
            byte[] factor = GetRandomByte (size);
            return BitConverter.ToString (factor).Replace ("-", string.Empty);
        }

        private static JObject GetHeader () {
            JObject jweHeader = new JObject ();
            jweHeader.Add ("alg", "RSA-OAEP");
            jweHeader.Add ("enc", "A128GCM");
            jweHeader.Add ("kid", "1");
            jweHeader.Add ("zip", "gzip");
            return jweHeader;
        }

        private static string GetEncodeHeader (JObject jweHeader) {
            string headerStr = String.Format ("alg={0}, enc={1}, kid={2}, zip={3}",
                jweHeader["alg"], jweHeader["enc"], jweHeader["kid"], jweHeader["zip"]);
            Console.WriteLine ("Header before encoding: " + headerStr);
            // Do base64 encoding.
            return UrlSafeBase64Encode (Encoding.UTF8.GetBytes (headerStr));
        }

        private static string UrlSafeBase64Encode (byte[] data) {
            char[] padding = { '=' };
            return Convert.ToBase64String (data).TrimEnd (padding).Replace ('+', '-').Replace ('/', '_');
        }

        private static string GetEncryptedKey (string sessionKey, string sessionKeyPublicKey) {
            byte[] plain = Encoding.UTF8.GetBytes (sessionKey);
            PemReader pemReader = new PemReader (new StringReader (sessionKeyPublicKey));
            RsaKeyParameters rsaKey = (RsaKeyParameters) pemReader.ReadObject ();
            OaepEncoding encrypter = new OaepEncoding (new RsaEngine (), new Sha256Digest (), new Sha256Digest (), null);
            encrypter.Init (true, rsaKey);
            byte[] cipher = encrypter.ProcessBlock (plain, 0, plain.Length);
            string encoded = Convert.ToBase64String (cipher);
            return UrlSafeBase64Encode (Encoding.UTF8.GetBytes (encoded));
        }

        public static byte[] GetRandomByte (int size) {
            RNGCryptoServiceProvider Gen = new RNGCryptoServiceProvider ();
            byte[] ivByte = new byte[size];
            Gen.GetBytes (ivByte);
            return ivByte;
        }

        private static string GetCipherText (string payload, string sessionKey, byte[] iv) {
            string payloadEncrypt = EncryptByGcm (payload, sessionKey, iv);
            Console.WriteLine ("Encrypted payload Hex String: " + payloadEncrypt);
            byte[] payLoadEncryptCompressByte = Compress (Encoding.UTF8.GetBytes (payloadEncrypt));
            string cipherTextEncode = UrlSafeBase64Encode (payLoadEncryptCompressByte);
            return cipherTextEncode;
        }

        private static string EncryptByGcm (string data, string privateKey, byte[] iv) {
            byte[] keyBytes = Encoding.UTF8.GetBytes (privateKey);
            byte[] plain = Encoding.UTF8.GetBytes (data);
            byte[] tag = new byte[16];
            byte[] cipher = new byte[plain.Length];
            AesGcm aesGcm = new AesGcm (keyBytes);
            aesGcm.Encrypt (iv, plain, cipher, tag);
            return (BitConverter.ToString (cipher) + BitConverter.ToString (tag)).Replace ("-", string.Empty);
        }

        private static byte[] Compress (byte[] data) {
            MemoryStream result = new MemoryStream ();
            //Byte[] lengthBytes = BitConverter.GetBytes(data.Length);
            //result.Write(lengthBytes, 0, 4);

            GZipStream compressionStream = new GZipStream (result, CompressionMode.Compress);
            compressionStream.Write (data, 0, data.Length);
            compressionStream.Close ();

            return result.ToArray ();
        }

        private static string GetSignature (string jweSignPrivateKey, string sessionKey, string payLoadJson, string jweHeaderEncode, string ivEncode) {
            string signContent = jweHeaderEncode + "." + sessionKey + "." + ivEncode + "." + payLoadJson;
            Console.WriteLine ("Content to be signed: " + signContent);
            StringBuilder sb = new StringBuilder ()
                .Append ("-----BEGIN PRIVATE KEY-----")
                .Append (System.Environment.NewLine)
                .Append (jweSignPrivateKey)
                .Append (System.Environment.NewLine)
                .Append ("-----END PRIVATE KEY-----");
            string privateKey = sb.ToString ();
            return RsaSignWithSha256 (signContent, privateKey);
        }

        private static string RsaSignWithSha256 (string data, string privateKey) {
            byte[] plain = Encoding.UTF8.GetBytes (data);
            PemReader pemReader = new PemReader (new StringReader (privateKey));
            RsaKeyParameters rsaKey = (RsaKeyParameters) pemReader.ReadObject ();
            ISigner signer = SignerUtilities.GetSigner ("SHA256withRSA/PSS");
            signer.Init (true, rsaKey);
            signer.BlockUpdate (plain, 0, plain.Length);
            byte[] signBytes = signer.GenerateSignature ();
            return Convert.ToBase64String (signBytes);
        }
    }
}