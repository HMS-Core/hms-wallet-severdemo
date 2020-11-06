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
# @File     : open_gateway_client
import requests
import json
from wallet.util.config_helper import ConfigHelper


def get_token(client_id, client_secret):
    """
       功能描述：获取access token
       返回值： access token
    """
    token_url = ConfigHelper().get_value('gw.tokenUrl')
    headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }
    parms = {
        'grant_type': 'client_credentials',
        'client_id': client_id,
        "client_secret": client_secret
    }
    rsp = requests.post(token_url, data=parms, headers=headers)
    json_result = json.loads(rsp.text)
    return json_result.get('access_token')
