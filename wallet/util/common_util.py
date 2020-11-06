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
# @File     : common_util
import os
import json
from config.config_path import get_config_path


def read_json_file(file_name):
    conf_path = get_config_path()
    hms_pass_path = os.path.join(conf_path, 'hmspass')
    file_path = os.path.join(hms_pass_path, file_name)
    try:
        with open(file_path, 'r', encoding='UTF-8') as file_handler:
            return file_handler.read()
    except FileNotFoundError:
        print(file_path + ' not found')


def add_issuer(json_data, app_id):
    json_to_python = json.loads(json_data)
    json_to_python["iss"] = app_id
    new_json_data = json.dumps(json_to_python)
    return new_json_data


def str_to_dict(str_object: str):
    dic = json.loads(str_object)
    return dic


def dict_to_obj(dictObject: dict, obj):
    for k, v in dictObject.items():
        obj.__dict__[k] = v
    return obj


def dict_to_str(dict_object: dict, ensure_ascii=False):
    return json.dumps(dict_object, ensure_ascii=ensure_ascii)


def isNoneOrEmptyString(stringAttr):
    # 因为参数默认是None 所以这种情况下也认为是空的。
    if stringAttr is None or len(stringAttr) == 0:
        return True
    return False


def isNoneObject(objectAttr):
    if objectAttr is None:
        return True
    return False
