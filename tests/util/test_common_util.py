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
# @Date     : 2020/6/4
# @version  : Python 3.8.3
# @File     : test_common_util
import json
from unittest import TestCase
from wallet.util.common_util import read_json_file


class Test(TestCase):
    def test_read_json_file(self):
        file_contents = read_json_file('NfccardPersonalize.json')
        json_contents = json.loads(file_contents)
        self.assertEqual('40001149', json_contents['serialNumber'])
        applet_personalize_fields_text = json_contents['appletPersonalizeFields']
        json_applet_personalize_fields = json.loads(applet_personalize_fields_text)
        self.assertEqual('value1', json_applet_personalize_fields['key1'])
