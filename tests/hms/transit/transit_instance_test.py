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
# @File     : transit_instance_test.py
import json
from unittest import TestCase
from urllib import parse

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, \
    addMessageToHwWalletObject, getHwWalletObjectById, getInstances, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject

from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletInstance


class Test(TestCase):
    """
    Transit instance tests.
    """

    def test_addTransitInstance(self):
        """
        Add a transit instance to HMS wallet server.
        Run the "test_createTransitModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/transit/instance
        """
        print("addTransitInstance begin")
        # Read a transit instance from a JSON file.
        objectJson = read_json_file("TransitInstance.json")
        to_dict = str_to_dict(objectJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("hwWallet object is error")
        urlSegment = "transit/instance"
        # Post the new transit instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted transit instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("addTransitInstance end")

    def test_getTransitInstance(self):
        """
        Get a transit instance by its instance ID.
        Run the "test_addTransitInstance" test before running this test.
        GET http://xxx/hmspass/v1/transit/instance/{instanceId}
        """
        print("getTransitInstance begin.")
        # ID of the transit instance you want to get.
        instanceId = "TransitPass60001"
        # This is used to construct http URL.
        urlSegment = "transit/instance/"
        # Get the transit instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding transit instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                            sort_keys=True,
                                                            indent=4))
        print("getTransitInstance end.")

    def test_getTransitInstanceList(self):
        """
        Get transit instance belonging to a specific transit model.
        Run the "test_addTransitInstance" test before running this test.
        GET http://xxx/hmspass/v1/transit/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getTransitInstanceList begin.")

        # Model ID of offer instances you want to get.
        modelId = "TransitTestModel"

        # Get a list of transit instances.
        urlSegment = "transit/instance"
        responseInstances = getInstances(urlSegment, modelId, 5)
        print("Transit instances list: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("getTransitInstanceList end.")

    def test_fullUpdateTransitInstance(self):
        """
        Overwrite a transit instance.
        Run the "test_addTransitInstance" test before running this test.
        PUT http://xxx/hmspass/v1/transit/instance/{instanceId}
        """
        print("fullUpdateTransitInstance begin.")

        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        instanceJson = read_json_file("FullUpdateTransitInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the transit instance.
        urlSegment = "transit/instance/"
        responseInstance = fullUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated transit instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("fullUpdateTransitInstance end.")

    def test_partialUpdateTransitInstance(self):
        """
        Update a transit instance.
        Run the "test_addTransitInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/transit/instance/{instanceId}
        """
        print("partialUpdateTransitInstance begin.")
        # ID of the transit instance to be updated.
        instanceId = "TransitPass60001"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        instanceJson = read_json_file("PartialUpdateTransitInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the transit instance.
        urlSegment = "transit/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated transit instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("partialUpdateTransitInstance end.")

    def test_addMessageToTransitInstance(self):
        """
        Add messages to a transit instance.
        Run the "test_addTransitInstance" test before running this test.
        POST http://xxx/hmspass/v1/transit/instance/{instanceId}/addMessage
        """
        print("addMessageToTransitInstance begin.")
        # ID of the transit instance to be updated.
        instanceId = "TransitPass60001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the transit instance.
        urlSegment = "transit/instance/addMessage"
        to_dict = str_to_dict(messagesJson)
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, to_dict)
        print("Updated transit instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("addMessageToTransitInstance end.")

