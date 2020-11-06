# Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -*- coding: utf-8 -*-
# @Date     : 2020/06/12 17:43
# @Software : PyCharm
# @version  ：Python 3.6.8
# @File     : jwe_util.py

from wallet.util.jwe_header import JweHeader
from wallet.util import aes_util
from wallet.util import rsa
import base64
import secrets


def get_header():
    jwe_header = JweHeader()
    jwe_header.set_alg("RSA-OAEP")
    jwe_header.set_enc("A128GCM")
    jwe_header.set_kid("1")
    jwe_header.set_zip("gzip")
    return jwe_header


def get_encode_header(jwe_header):
    header_data = "alg=%s, enc=%s, kid=%s, zip=%s" % (
        jwe_header.get_alg(), jwe_header.get_enc(), jwe_header.get_kid(), jwe_header.get_zip())

    header_str_byte = bytes(header_data, encoding='utf-8')
    jwe_header_encode = base64.urlsafe_b64encode(header_str_byte)
    return jwe_header_encode.decode('utf-8')


def get_encrypted_key(session_key, session_key_public_key):
    encrypt_data = rsa.encrypt_by_rsa(session_key, session_key_public_key)
    encrypt_data_encode = base64.urlsafe_b64encode(encrypt_data)
    print("Encrypted sessionKey: ", encrypt_data_encode)
    return encrypt_data_encode.decode('utf-8')


def get_cipher_text(pay_load, session_key, iv_hex):
    payload_encrypt = aes_util.encrypt_by_gcm(pay_load, session_key, iv_hex)
    print("Encrypted payload Hex String: ", payload_encrypt)
    payload_encrypt_compress_byte = aes_util.compress_data(payload_encrypt)
    cipher_text_encode = base64.urlsafe_b64encode(payload_encrypt_compress_byte)
    return cipher_text_encode.decode('utf-8')


def get_signature(jwe_private_key, session_key, pay_load, jwe_header_encode, iv_hex_encode):
    sign_content = jwe_header_encode + "." + session_key + "." + iv_hex_encode + "." + pay_load
    print("Content to be signed: ", sign_content)
    sign_data_byte = rsa.sign_by_private_key(sign_content, jwe_private_key)
    sign_data = sign_data_byte.decode('utf-8')
    return sign_data


def generate_jwe(jwe_private_key, pay_load):
    # Part 1: JWE Header.
    # This header contains information about encryption and compression algorithms. It's a constant String.

    print("Part 1:")
    jwe_header = get_header()
    jwe_header_encode = get_encode_header(jwe_header)
    print("Encoded header: ", jwe_header_encode)

    # Part 2: JWE Encrypted Key
    # Generate a 16-byte session key to encrypt payload data. Then convert it to a Hex String.
    print("Part 2:")
    session_key = secrets.token_hex(16)
    print("session key: ", session_key)
    session_key_public_key = "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE="

    session_key_public_key_pem = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----\n" % session_key_public_key

    encrypted_key_encode = get_encrypted_key(session_key, session_key_public_key_pem)
    print("Encrypted sessionKey: ", encrypted_key_encode)

    # Part 3: JWE IV
    # Generate a 12-byte iv. Then convert it to a Hex String, and then do base64 encoding to the Hex String.
    print("Part 3:")
    iv_hex = secrets.token_hex(12)
    iv_hex_byte = bytes(iv_hex, encoding='utf-8')
    iv_hex_encode_byte = base64.urlsafe_b64encode(iv_hex_byte)
    iv_hex_encode = iv_hex_encode_byte.decode('utf-8')
    print("Encoded iv: ", iv_hex_encode)

    # Part 4: JWE Cipher Text
    # Encrypt the payload with sessionKey and iv using AES/GCM/NoPadding algorithm. Encode the cipher text into a
    # Hex String. Then do gzip compression and base64 encoding to the Hex String.
    print("Part 4:")
    print("pay_load: " + pay_load)
    cipher_text_encode = get_cipher_text(pay_load, session_key, iv_hex)
    print("Compressed and encoded cipher text: ", cipher_text_encode)

    # Part 5: JWE Signature
    # Use your own private key to sign the content with SHA256withRSA, then do base64 encoding to it.
    # HMS wallet server will use the public key you provided on AGC to verify signatures.
    print("Part 5:")
    signature = get_signature(jwe_private_key, session_key, pay_load, jwe_header_encode, iv_hex_encode)
    print("signature: ", signature)

    # Combine all five parts together to form a valid JWE.
    jwe_data = "%s.%s.%s.%s.%s" % (
        jwe_header_encode, encrypted_key_encode, iv_hex_encode, cipher_text_encode, signature)
    return jwe_data
