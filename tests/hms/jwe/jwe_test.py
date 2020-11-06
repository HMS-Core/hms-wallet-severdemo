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
# @File     : jwe_test.py
from unittest import TestCase
from urllib import parse
from wallet.util import common_util, jwe_util


class JweTest(TestCase):

    """
        Generate a thin JWE. This JWEs contains only instanceId information. It's used to bind an existing instance
        to a user. You should generate a thin JWE with an instanceId that has already been added to HMS wallet server.
    """
    def test_generate_thin_jwe_to_bind_user(self):
        print("generateThinJWEToBindUser begin.\n")

        # This is the app ID registered on Huawei AGC website.
        app_id = "300011981"

        # Bind existing pass instances to users. Construct a list of event ticket-instance IDs to be bound.
        instance_id_list_json = "{\"instanceIds\": [\"Replace with the instance ID to be bond. For example: EventTicketPass10001\"]}"
        common_util.add_issuer(instance_id_list_json, app_id)
        pay_load = common_util.add_issuer(instance_id_list_json, app_id)

        # You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        jwe_sign_privateKey = "Replace with your private key."

        jwe_sign_privateKey_pem = "-----BEGIN RSA PRIVATE KEY-----\n%s\n-----END RSA PRIVATE KEY-----\n" % (
            jwe_sign_privateKey)
        # Generate a thin JWE.
        jwe_data = jwe_util.generate_jwe(jwe_sign_privateKey_pem, pay_load)
        print("jwe data: ", jwe_data)

        """
            Replace {walletkit_website_url} with one of the following strings according to your account location.
            walletpass-drcn.cloud.huawei.com for China
            walletpass-drru.cloud.huawei.com for Russia
            walletpass-dra.cloud.huawei.com for Asia, Africa, and Latin America
            walletpass-dre.cloud.huawei.com for Europe
        """
        print("JWE link to be viewed in a browser: https://{walletkit_website_url}/walletkit/consumer/pass/save?content=" + parse.quote(jwe_data, safe=""))
        print("generateThinJWEToBindUser end.\n")


    """
        Generate a JWE. This JWE contains full instance information. It's used to add a new instance to HMS wallet serve
        and bind it to a user. You should generate a JWE with an instance that has not been added to HMS wallet server.
    """
    def test_generate_jwe_to_add_instance_and_bind_user(self):
        print("generateJWEToAddPassAndBindUser begin.\n")

        # This is the app ID registered on Huawei AGC website.
        app_id = "300011981"

        # Read a new pass instance.
        json_content = common_util.read_json_file("Replace with the instance JSON file to be created. For example: EventTicketInstance.json")
        pay_load = common_util.add_issuer(json_content, app_id)

        # You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        jwe_sign_privateKey = "Replace with your private key."

        jwe_sign_privateKey_pem = "-----BEGIN RSA PRIVATE KEY-----\n%s\n-----END RSA PRIVATE KEY-----\n" % (
            jwe_sign_privateKey)
        # Generate a thin JWE.
        jwe_data = jwe_util.generate_jwe(jwe_sign_privateKey_pem, pay_load)
        print("jwe data: ", jwe_data)

        """
            Replace {walletkit_website_url} with one of the following strings according to your account location.
            walletpass-drcn.cloud.huawei.com for China
            walletpass-drru.cloud.huawei.com for Russia
            walletpass-dra.cloud.huawei.com for Asia, Africa, and Latin America
            walletpass-dre.cloud.huawei.com for Europe
        """
        print("JWE link to be viewed in a browser: https://{walletkit_website_url}/walletkit/consumer/pass/save?content=" + parse.quote(jwe_data, safe=""))
        print("generateJWEToAddPassAndBindUser end.\n")

