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
# @File     : JweHeader.py


class JweHeader(object):
    def __init__(self):
        self.alg = ""
        self.enc = ""
        self.kid = ""
        self.zip = ""

    def set_alg(self, alg):
        self.alg = alg

    def get_alg(self):
        return self.alg

    def set_enc(self, enc):
        self.enc = enc

    def get_enc(self):
        return self.enc

    def set_kid(self, kid):
        self.kid = kid

    def get_kid(self):
        return self.kid

    def set_zip(self, zip):
        self.zip = zip

    def get_zip(self):
        return self.zip
