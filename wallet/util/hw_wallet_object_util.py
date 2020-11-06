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
# @Date      :2020/6/9
# @Version   :Python 3.8.3
# @File      :hw_wallet_object_util
import datetime
from wallet.hms.dto.fields import Fields
from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.dto.status import Status
from wallet.util.common_util import isNoneOrEmptyString, dict_to_obj


def getLocalTimeByUtcString(utcTimeString):
    try:
        UTC_FORMAT = "%Y-%m-%dT%H:%M:%S.%fZ"
        utcTime = datetime.datetime.strptime(utcTimeString, UTC_FORMAT)
        localtime = utcTime + datetime.timedelta(hours=8)
    except Exception:
        print("Invalid time format: " + utcTimeString)
        raise Exception
    return localtime


STRING_STATE_ACTIVE = "active"
STRING_STATE_INACTIVE = "inactive"
STRING_STATE_COMPLETED = "completed"
STRING_STATE_EXPIRED = "expired"
STATE_TYPE_LIST = [STRING_STATE_ACTIVE, STRING_STATE_INACTIVE, STRING_STATE_COMPLETED, STRING_STATE_EXPIRED]


def validateWalletModel(walletModel: HwWalletObject):
    if walletModel is None:
        print("walletClass is None")
        return False
    if isNoneOrEmptyString(walletModel.get_passTypeIdentifier()):
        print("passTypeIdentifier is None or empty")
        return False
    if isNoneOrEmptyString(walletModel.get_passStyleIdentifier()):
        print("passStyleIdentifier is None or empty")
        return False
    if isNoneOrEmptyString(walletModel.get_organizationName()):
        print("organizationName is None or empty")
        return False
    if isNoneOrEmptyString(walletModel.get_passVersion()):
        print("passVersion is None or empty")
        return False
    return True


def validateWalletInstance(walletObject: HwWalletObject):
    if walletObject is None:
        print("walletObject is null")
        return False
    if isNoneOrEmptyString(walletObject.get_passTypeIdentifier()):
        print("passTypeIdentifier is None or empty")
        return False
    if isNoneOrEmptyString(walletObject.get_passStyleIdentifier()):
        print("passStyleIdentifier is None or empty")
        return False
    if isNoneOrEmptyString(walletObject.get_organizationPassId()):
        print("organizationPassId is None or empty")
        return False
    if isNoneOrEmptyString(walletObject.get_serialNumber()):
        print("serialNumber is None or empty")
        return False
    return validateStatusDate(walletObject)


def validateStatusDate(walletObject: HwWalletObject):
    fields_dict = walletObject.get_fields()
    if fields_dict is None:
        return True
    fields: Fields = dict_to_obj(fields_dict, Fields())
    status_dict = fields.get_status()
    if status_dict is None:
        return True
    status: Status = dict_to_obj(status_dict, Status())

    state = status.get_state()
    if not isNoneOrEmptyString(state):
        if state.lower() not in STATE_TYPE_LIST:
            print("status is abnormal.")
            return False
        if state.lower() == STRING_STATE_EXPIRED:
            print("Instance state has been expired.")
            return False

    effectTime = status.effectTime
    expireTime = status.expireTime
    is_effectTime_empty = isNoneOrEmptyString(effectTime)
    is_expireTime_empty = isNoneOrEmptyString(expireTime)

    if (is_effectTime_empty and not is_expireTime_empty) or (not is_effectTime_empty and is_expireTime_empty):
        print("Instance effectTime and expireTime must be consistent.")
        return False
    if is_effectTime_empty:
        return True

    try:
        statusEffectTime = getLocalTimeByUtcString(effectTime)
        statusExpireTime = getLocalTimeByUtcString(expireTime)
    except Exception:
        print(Exception.__context__)
        return False
    if statusExpireTime < datetime.datetime.now() or statusExpireTime < statusEffectTime:
        print("Instance status has expired.")
        return False
    return True
