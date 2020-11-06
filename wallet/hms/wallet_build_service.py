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
# @File     : wallet_build_service
import json
import requests
from wallet.hms.dto.batch_get_hw_wallet_result import BatchGetHwWalletResult
from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.dto.page_info import PageInfo
from wallet.util.common_util import dict_to_obj, isNoneOrEmptyString, isNoneObject, str_to_dict
from wallet.util.config_helper import ConfigHelper
from wallet.util.open_gateway_client import get_token

CONFIG_HELPER = ConfigHelper()


def postHwWalletObjectToWalletServer(url, hwWalletObject):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    wallet_server_url = baseUrl + url
    # Send the http request and get response.
    rsp = requests.post(wallet_server_url,
                        data=json.dumps(hwWalletObject, default=lambda obj: obj.__dict__, sort_keys=True, indent=4),
                        headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def getHwWalletObjectById(url, resourceId):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    walletServerUrl = baseUrl + url + resourceId
    # Send the http request and get response.
    rsp = requests.get(walletServerUrl, headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    # Return the HwWalletObject with the corresponding resourceId.
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def getModels(url, pageSize):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    walletServerUrl = baseUrl + url
    if pageSize is None:
        response = requests.get(walletServerUrl, headers=headers)
        batchGetHwWalletResult: BatchGetHwWalletResult = dict_to_obj(response.text, BatchGetHwWalletResult())
        if isNoneObject(batchGetHwWalletResult):
            print("listWalletModel failed. batchGetHwWalletResult is null.")
            return None
        else:
            return batchGetHwWalletResult.data
    else:
        walletModelList = []
        tempUrl = walletServerUrl + "?pageSize=" + str(pageSize)
        session = ""
        while True:
            finalUrl = tempUrl
            if not isNoneOrEmptyString(session):
                finalUrl = finalUrl + "&session=" + session
            response = requests.get(finalUrl, headers=headers)
            batchGetHwWalletResult: BatchGetHwWalletResult = dict_to_obj(str_to_dict(response.text),
                                                                         BatchGetHwWalletResult())
            if isNoneObject(batchGetHwWalletResult):
                print("listWalletModel failed. batchGetHwWalletResult is null.")
                return None
            data = batchGetHwWalletResult.get_data()
            if data is None:
                return walletModelList
            walletModelList.extend(data)
            pageInfo: PageInfo = dict_to_obj(batchGetHwWalletResult.get_pageInfo(), PageInfo())
            if isNoneObject(pageInfo):
                print("listWalletModel failed. pageInfo is null.")
                return None
            next_session = pageInfo.get_nextSession()
            if isNoneOrEmptyString(next_session):
                return walletModelList
            session = next_session


def getInstances(url, modelId, pageSize):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl') + url
    if pageSize is None:
        response = requests.get(url + "?modelId=" + modelId, headers=headers)
        batchGetHwWalletResult: BatchGetHwWalletResult = dict_to_obj(str_to_dict(response.text),
                                                                     BatchGetHwWalletResult())
        if isNoneObject(batchGetHwWalletResult):
            print("listWalletInstance failed. batchGetHwWalletResult is null.")
            return None
        else:
            return batchGetHwWalletResult.data
    else:
        walletInstanceList = []
        tempUrl = baseUrl + "?modelId=" + modelId + "&pageSize=" + str(pageSize)
        session = ""
        while True:
            finalUrl = tempUrl
            if not isNoneOrEmptyString(session):
                finalUrl = finalUrl + "&session=" + session
            response = requests.get(finalUrl, headers=headers)
            batchGetHwWalletResult: BatchGetHwWalletResult = dict_to_obj(str_to_dict(response.text),
                                                                         BatchGetHwWalletResult())
            if isNoneObject(batchGetHwWalletResult):
                print("listWalletInstance failed. batchGetHwWalletResult is null.")
                return None
            data = batchGetHwWalletResult.get_data()
            if data is None:
                return walletInstanceList
            walletInstanceList.extend(data)
            pageInfo: PageInfo = dict_to_obj(batchGetHwWalletResult.get_pageInfo(), PageInfo())
            if isNoneObject(pageInfo):
                print("listWalletInstance failed. pageInfo is null.")
                return None
            next_session = pageInfo.get_nextSession()
            if isNoneOrEmptyString(next_session):
                return walletInstanceList
            session = next_session


def fullUpdateHwWalletObject(url, resourceId, hwWalletObject):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    walletServerUrl = baseUrl + url + resourceId
    # Send the http request and get response.
    rsp = requests.put(walletServerUrl,
                       data=json.dumps(hwWalletObject, default=lambda obj: obj.__dict__, sort_keys=True, indent=4),
                       headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    # Return the updated wallet object
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def partialUpdateHwWalletObject(url, resourceId, hwWalletObject):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    walletServerUrl = baseUrl + url + resourceId
    # Send the http request and get response.
    rsp = requests.patch(walletServerUrl,
                         data=json.dumps(hwWalletObject, default=lambda obj: obj.__dict__, sort_keys=True, indent=4),
                         headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    # Return the updated wallet model.
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def addMessageToHwWalletObject(url, resourceId, body):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    wallet_server_url = baseUrl + url.replace("addMessage", "") + resourceId + "/addMessage"
    rsp = requests.post(wallet_server_url,
                        data=json.dumps(body, default=lambda obj: obj.__dict__, sort_keys=True, indent=4),
                        headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def updateLinkedOffersToLoyaltyInstance(url, resourceId, body):
    # Construct the http header.
    headers = construct_http_headers()
    # Construct the http URL.
    baseUrl = CONFIG_HELPER.get_value('walletServerBaseUrl')
    wallet_server_url = baseUrl + url.replace("linkedoffers", resourceId) + "/linkedoffers"
    rsp = requests.patch(wallet_server_url,
                         data=json.dumps(body, default=lambda obj: obj.__dict__, sort_keys=True, indent=4),
                         headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    return dict_to_obj(str_to_dict(rsp.text), HwWalletObject())


def postToWalletServer(url_segment, body):
    headers = construct_http_headers()
    base_url = CONFIG_HELPER.get_value('walletServerBaseUrl')
    wallet_server_url = base_url + url_segment
    rsp = requests.post(wallet_server_url, data=body, headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    return rsp.text


def delete_from_wallet_server(url_segment):
    headers = construct_http_headers()
    base_url = CONFIG_HELPER.get_value('walletServerBaseUrl')
    wallet_server_url = base_url + url_segment
    rsp = requests.delete(wallet_server_url, headers=headers)
    if rsp.status_code != requests.codes.ok:
        rsp.raise_for_status()
    return rsp.text

def construct_http_headers():
    client_id = CONFIG_HELPER.get_value('gw.appid')
    client_secret = CONFIG_HELPER.get_value('gw.appid.secret')
    authorization = get_token(client_id, client_secret)
    return {'Content-Type': 'application/json;charset=utf-8', 'Authorization': 'Bearer ' + authorization,
            'Accept': 'application/json;charset=utf-8'}
