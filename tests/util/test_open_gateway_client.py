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
# @File     : test_open_gateway_client

from unittest import TestCase

from wallet.util.open_gateway_client import get_token


class Test(TestCase):
    def test_get_token(self):
        token = get_token('60993', 'test60993bba8df90acb8eb1s0a2081f')
        print(token)