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
# @File     : offer_instance_test.py
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
    Offer instance tests.
    """

    def test_addOfferInstance(self):
        """
        Add a offer instance to HMS wallet server.
        Run the "test_createOfferModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/offer/instance
        """
        print("addOfferInstance begin")
        # Read an example loyalty instance from a JSON file.
        objectJson = read_json_file("OfferInstance.json")
        to_dict = str_to_dict(objectJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("hwWallet object is error")
        urlSegment = "offer/instance"
        # Post the new offer instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted offer instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("addOfferInstance end")

    def test_getOfferInstance(self):
        """
        Get a offer instance by its instance ID.
        Run the "test_addOfferInstance" test before running this test.
        GET http://xxx/hmspass/v1/offer/instance/{instanceId}
        """
        print("getOfferInstance begin.")
        #  ID of the offer instance you want to get.
        instanceId = "OfferPass50001"
        # This is used to construct http URL.
        urlSegment = "offer/instance/"
        # Get the loyalty instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding offer instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                          sort_keys=True,
                                                          indent=4))
        print("getOfferInstance end.")

    def test_getOfferInstanceList(self):
        """
        Get some offer instance belongs to a specific loyalty model.
        Run the "test_addOfferInstance" test before running this test.
        GET http://xxx/hmspass/v1/offer/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getOfferInstanceList begin.")

        # Model ID of offer instances you want to get.
        modelId = "OfferTestModel"

        #  Get a list of offer instances.
        urlSegment = "offer/instance"
        responseInstances = getInstances(urlSegment, modelId, 5)
        print("Offer instances list: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("getOfferInstanceList end.")

    def test_fullUpdateOfferInstance(self):
        """
        Overwrite an offer instance.
        Run the "test_addOfferInstance" test before running this test.
        PUT http://xxx/hmspass/v1/offer/instance/{instanceId}
        """
        print("fullUpdateOfferInstance begin.")

        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        instanceJson = read_json_file("FullUpdateOfferInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the offer instance.
        urlSegment = "offer/instance/"
        responseInstance = fullUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated offer instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("fullUpdateOfferInstance end.")

    def test_partialUpdateOfferInstance(self):
        """
        Update an offer instance.
        Run the "test_addOfferInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/offer/instance/{instanceId}
        """
        print("partialUpdateOfferInstance begin.")
        # ID of the offer instance you want to update.
        instanceId = "OfferPass50001"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        instanceJson = read_json_file("PartialUpdateOfferInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the offer instance.
        urlSegment = "offer/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated offer instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("partialUpdateOfferInstance end.")

    def test_addMessageToOfferInstance(self):
        """
        Add messages to an offer instance.
        Run the "test_addOfferInstance" test before running this test.
        POST http://xxx/hmspass/v1/offer/instance/{instanceId}/addMessage
        """
        print("addMessageToOfferInstance begin.")
        # ID of the offer instance to be updated.
        instanceId = "OfferPass50001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the offer instance.
        urlSegment = "offer/instance/addMessage"
        to_dict = str_to_dict(messagesJson)
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, to_dict)
        print("Updated offer instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("addMessageToOfferInstance end.")

