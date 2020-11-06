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
# @Date     : 2020/6/3
# @version  : Python 3.8.3
# @File     : signature_test.py
from unittest import TestCase
from wallet.util import rsa


class Test(TestCase):
    # Test for callback signature validation.

    def test_verify_signature(self):
        # If your server receive a callback notification request from HMS wallet server, you should verify the
        # signature in the request header with Huawei's fixed signature public key. The signature is signed with
        # "SHA256WithRSA/PSS" algorithm. This test shows an example of verifying a signature.
        print("verifySignature begin.")
        receivedBody = {
            'eventId': '469283774166292993',
            'eventTime': '2020-10-09T03:41:55.694Z',
            'passNumber': 'passNumber1234',
            'passTypeIdentifier': 'hwpass.com.xxx',
            'eventType': 'DELETE_CARD',
            'sceneType': 'THIRD_PARTY_DELETE_CARD',
            'noticeToken': '1e4dda10e4590dcd66d1c14bfe1505424091f693996d2db885e54ad040723d7c',
            'pushToken': 'asdfghjkl'
        }
        # Convert the parameters into a string in certain format.
        content = to_sign_string(receivedBody)
        # Assume this is the HMSSign you received in the request header.
        received_hms_sign = "g6Ylid2v13ibrGCDITYkms7rOxM9Qmpn2nTQy+MDneCvs8n2AznhdH1BOdZxAFEeNvIqaBejupJJNnHweDixxwQub34pt7Kv0wuW3LI0gtut5jsjEJuF9kfPj/f6W6ZfUgZB8R9j6jGMzqWoa7IRkXpIxpdJgral8aE+QwMG51hrzH8j/7EbPxpQgFyxuxiZimaeKDbgJ2yWIDtnaEVs+6NxLMhz+Vgo0vxEiyo+TEdcpkl0ahMA8XCXGs6lqlbl+G8imlU4+pMvM+IL9ygCbDWgwj6pmfrkDnD/tYVqElE9SIZ79+ShWLNwUgtWFfzo1ckMRWGSdMfwVd+f6boVIQ=="
        sign_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1+b2/q6KEJfvI65xJLXhPMT8YRUO618zsgaW4pNGZ+r/mwfFC1EOZbcBp7sV0IaxSWeMy0WNyJPSh/JltuiC1R93hfA0Kh3DlaRWaDgJz9VC1b+aPjUOx+uqndOEFiZcKGGnM60YPXfyo7xCDH76/WsWR0G4Ov6MoYQ76RAUT0t+G0oumYGgdLYwx5hJ1ywDKPXszj7A/mKHtWJKiylPIhUK2mLwKR8Y/+3dLNuNomvb7miVgeBFiriwGS1FolQMu433zEugAqRgsiasZAKfVK1BChPmiC812IMS1UPhz1wwpXzzkjQ1YQUGjnbHpooKobeCyctKKgF27F84egpzsQIDAQAB"
        jwe_sign_public_key_pem = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----\n" % sign_public_key
        rsa.verify_by_public_key(content, jwe_sign_public_key_pem, received_hms_sign)


def to_sign_string(received_body):
    sortedKeys = sorted(received_body.keys())
    sign_string = ''
    for k in sortedKeys:
        if len(sign_string) != 0:
            sign_string = sign_string + '&'
        sign_string = sign_string + k + '=' + received_body[k]
    return sign_string
