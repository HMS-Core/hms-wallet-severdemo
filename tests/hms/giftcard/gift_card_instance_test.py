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
# @File     : gift_card_instance_test.py
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, \
    addMessageToHwWalletObject, getHwWalletObjectById, getInstances, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject

from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletInstance


class Test(TestCase):
    """
    Gift card instance tests.
    """

    def test_addGiftCardInstance(self):
        """
        Add a gift card instance to HMS wallet server.
        Run the "test_createGiftCardModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/giftcard/instance
        """
        print("addGiftCardInstance begin")
        # Read a gift card instance from a JSON file.
        objectJson = read_json_file("GiftCardInstance.json")
        to_dict = str_to_dict(objectJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("Invalid instance parameters.")
        urlSegment = "giftcard/instance"
        # Post the new gift card instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted gift card instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                         sort_keys=True, indent=4))
        print("addGiftCardInstance end")

    def test_getGiftCardInstance(self):
        """
        Get a gift card instance by its instance ID.
        Run the "test_addGiftCardInstance" test before running this test.
        GET http://xxx/hmspass/v1/giftcard/instance/{instanceId}
        """
        print("getGiftCardInstance begin.")
        # ID of the gift card instance you want to get.
        instanceId = "GiftCardPass30001"
        # This is used to construct http URL.
        urlSegment = "giftcard/instance/"
        # Get the gift card instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding gift card instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                              sort_keys=True,
                                                              indent=4))
        print("getGiftCardInstance end.")

    def test_getGiftCardInstanceList(self):
        """
        Get gift card instance belonging to a specific gift card model.
        Run the "test_addGiftCardInstance" test before running this test.
        GET http://xxx/hmspass/v1/giftcard/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getGiftCardInstanceList begin.")

        # Model ID of gift card instances you want to get.
        modelId = "GiftCardTestModel"

        # Get a list of gift card instances.
        urlSegment = "giftcard/instance"
        responseInstances = getInstances(urlSegment, modelId, 5)
        print("Gift card instances list: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("getGiftCardInstanceList end.")

    def test_fullUpdateGiftCardInstance(self):
        """
        Overwrite a gift card instance.
        Run the "test_addGiftCardInstance" test before running this test.
        PUT http://xxx/hmspass/v1/giftcard/instance/{instanceId}
        """
        print("fullUpdateGiftCardInstance begin.")

        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        instanceJson = read_json_file("FullUpdateGiftCardInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the gift card instance.
        urlSegment = "giftcard/instance/"
        responseInstance = fullUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated gift card instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("fullUpdateGiftCardInstance end.")

    def test_partialUpdateGiftCardInstance(self):
        """
        Update a gift card instance.
        Run the "test_addGiftCardInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/giftcard/instance/{instanceId}
        """
        print("partialUpdateGiftCardInstance begin.")
        # ID of the gift card instance you want to update.
        instanceId = "GiftCardPass30001"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        instanceJson = read_json_file("PartialUpdateGiftCardInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the gift card instance.
        urlSegment = "giftcard/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated gift card instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("partialUpdateGiftCardInstance end.")

    def test_addMessageToGiftCardInstance(self):
        """
        Add messages to a gift card instance.
        Run the "test_addGiftCardInstance" test before running this test.
        POST http://xxx/hmspass/v1/giftcard/instance/{instanceId}/addMessage
        """
        print("addMessageToGiftCardInstance begin.")
        # ID of the gift card instance you want to update.
        instanceId = "GiftCardPass30001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the gift card instance.
        urlSegment = "giftcard/instance/addMessage"
        to_dict = str_to_dict(messagesJson)
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, to_dict)
        print("Updated gift card instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("addMessageToGiftCardInstance end.")


