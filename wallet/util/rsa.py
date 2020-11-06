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
# @File     : rsa.py

from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.backends import default_backend
import base64


# RSA encrypt data by RSA public key
# plain_data: encrypted data, it is String type.
# public_key_data: public key
# return: the encrypted string.

def encrypt_by_rsa(plain_data, public_key_data):
    plain_data_byte = bytes(plain_data, encoding='utf-8')
    public_key_byte = bytes(public_key_data, encoding='utf-8')
    public_key_pem = serialization.load_pem_public_key(
        public_key_byte,
        backend=default_backend()
    )
    cipher_text = public_key_pem.encrypt(plain_data_byte,
                                         padding.OAEP(
                                             mgf=padding.MGF1(algorithm=hashes.SHA256()),
                                             algorithm=hashes.SHA256(),
                                             label=None
                                         )
                                         )

    cipher_text_encode = base64.b64encode(cipher_text)
    return cipher_text_encode


# sign by RSA private key
# sign_content_byte: sign data content, it is String type.
# private_key_byte: private key to sign the data.
# return: sign data.
def sign_by_private_key(sign_content, private_key):
    sign_content_byte = bytes(sign_content, encoding='utf-8')
    private_key_byte = bytes(private_key, encoding='utf-8')
    private_key_pem = serialization.load_pem_private_key(private_key_byte,
                                                         password=None,
                                                         backend=default_backend()
                                                         )
    sign_data = private_key_pem.sign(sign_content_byte,
                                     padding.PSS(
                                         mgf=padding.MGF1(hashes.SHA256()),
                                         salt_length=32
                                     ),
                                     hashes.SHA256()
                                     )

    sign_data_encode = base64.b64encode(sign_data)
    return sign_data_encode


# verify by RSA public key
# sign_content: sign data content, it is String type.
# public_key: public key to sign the data.
# sign: sign data.
def verify_by_public_key(sign_content, public_key, sign):
    sign_content_byte = bytes(sign_content, encoding='utf-8')
    sign_byte = bytes(sign, encoding='utf-8')

    public_key_byte = bytes(public_key, encoding='utf-8')
    public_key_pem = serialization.load_pem_public_key(
        public_key_byte,
        backend=default_backend()
    )

    public_key_pem.verify(
        base64.b64decode(sign_byte),
        sign_content_byte,
        padding.PSS(
            mgf=padding.MGF1(hashes.SHA256()),
            salt_length=32
        ),
        hashes.SHA256()
    )
