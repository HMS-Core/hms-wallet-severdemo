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
# @File     : aes_util.py

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import (
    Cipher, algorithms, modes
)
import gzip


# GCM encrypt
# plain_data: the data to be encrypted, it is string format.
# secret_key: encryption secret key, it is String type.
# iv_hex: encryption random iv.
# return: GCM encrypted data
def encrypt_by_gcm(plain_data, secret_key, iv_hex):
    secret_key_byte = bytes(secret_key, encoding='utf-8')
    iv_hex_byte = bytes.fromhex(iv_hex)

    encrypt_object = Cipher(
        algorithms.AES(secret_key_byte),
        modes.GCM(iv_hex_byte),
        backend=default_backend()
    ).encryptor()
    payload_byte = bytes(plain_data, encoding='utf-8')
    cipher_data = encrypt_object.update(
        payload_byte) + encrypt_object.finalize() + encrypt_object.tag  # 将tag直接追加在最后，即可和java解密代码兼容
    return cipher_data.hex().upper()


# gzip Compress
# original_data: Data to be compressed
# return: Compressed data
def compress_data(original_data):
    encrypt_byte = bytes(original_data, encoding='utf-8')
    encrypt_data_byte = gzip.compress(encrypt_byte)
    return encrypt_data_byte
