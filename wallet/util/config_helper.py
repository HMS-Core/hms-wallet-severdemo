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
# @File     : config_helper
import threading
import os
from config.config_path import get_config_path


class ConfigHelper:
    """
    单例模式实现的配置管理类
    """
    _instance_lock = threading.Lock()

    def __init__(self):
        self.params = {}
        self.load()

    def __new__(cls, *args, **kwargs):
        if not hasattr(ConfigHelper, "_instance"):
            with ConfigHelper._instance_lock:
                if not hasattr(ConfigHelper, "_instance"):
                    ConfigHelper._instance = object.__new__(cls)
        return ConfigHelper._instance

    def load(self):
        try:
            conf_path = get_config_path()
            release_path = os.path.join(conf_path, 'release.config.properties')
            with open(release_path, 'r') as system_config:
                for line in system_config:
                    if line.startswith('#'):
                        continue
                    if line.find('=') > 0:
                        config = line.replace('\n', '').split('=')
                        self.params[config[0]] = config[1]
        except FileNotFoundError:
            print(release_path + ' not found')

    def get_value(self, key):
        return self.params.get(key)
