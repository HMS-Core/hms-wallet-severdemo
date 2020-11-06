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
# @File     : loyalty_instance_test.py
import json
from unittest import TestCase


from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, \
    addMessageToHwWalletObject, updateLinkedOffersToLoyaltyInstance, getHwWalletObjectById, getInstances, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject

from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletInstance


class Test(TestCase):
    """
    Loyalty instance tests.
    """

    def test_addLoyaltyInstance(self):
        """
        Add a loyalty instance to HMS wallet server.
        Run the "test_createLoyaltyModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/loyalty/instance
        """
        print("addLoyaltyInstance begin")
        # Read a loyalty instance from a JSON file.
        objectJson = read_json_file("LoyaltyInstance.json")
        to_dict = str_to_dict(objectJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("hwWallet object is error")
        urlSegment = "loyalty/instance"
        # Post the new loyalty instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("addLoyaltyInstance end")

    def test_getLoyaltyInstance(self):
        """
        Get a loyalty instance by its instance ID.
        Run the "test_addLoyaltyInstance" test before running this test.
        GET http://xxx/hmspass/v1/loyalty/instance/{instanceId}
        """
        print("getLoyaltyInstance begin.")
        # ID of the loyalty instance you want to get.
        instanceId = "LoyaltyPass40001"
        # This is used to construct http URL.
        urlSegment = "loyalty/instance/"
        # Get the loyalty instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                            sort_keys=True,
                                                            indent=4))
        print("getLoyaltyInstance end.")

    def test_getLoyaltyInstanceList(self):
        """
        Get some loyalty instance belongs to a specific loyalty model.
        Run the "test_addLoyaltyInstance" test before running this test.
        GET http://xxx/hmspass/v1/loyalty/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getLoyaltyInstanceList begin.")

        # Model ID of loyalty instances you want to get.
        modelId = "LoyaltyTestModel"

        # Get a list of the loyalty instances.
        urlSegment = "loyalty/instance"
        responseInstances = getInstances(urlSegment, modelId, 5)
        print("Loyalty instances list: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("getLoyaltyInstanceList end.")

    def test_fullUpdateLoyaltyInstance(self):
        """
        Overwrite a loyalty instance.
        Run the "test_addLoyaltyInstance" test before running this test.
        PUT http://xxx/hmspass/v1/loyalty/instance/{instanceId}
        """
        print("fullUpdateLoyaltyInstance begin.")

        # Read an example HwWalletObject from a JSON file. The corresponding loyalty instance will be replaced by this
        # HwWalletObject.
        instanceJson = read_json_file("FullUpdateLoyaltyInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the loyalty instance.
        urlSegment = "loyalty/instance/"
        responseInstance = fullUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("fullUpdateLoyaltyInstance end.")

    def test_partialUpdateLoyaltyInstance(self):
        """
        Update a loyalty instance.
        Run the "test_addLoyaltyInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}
        """
        print("partialUpdateLoyaltyInstance begin.")
        # ID of the loyalty instance to be updated.
        instanceId = "LoyaltyPass40001"
        # Read an example HwWalletObject from a JSON file. The corresponding loyalty model will merge with this
        # HwWalletObject
        instanceJson = read_json_file("PartialUpdateLoyaltyInstance.json")
        to_dict = str_to_dict(instanceJson)
        hwWalletObject: HwWalletObject = dict_to_obj(to_dict, HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the loyalty instance.
        urlSegment = "loyalty/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("partialUpdateLoyaltyInstance end.")

    def test_addMessageToLoyaltyInstance(self):
        """
        Add messages to a loyalty instance.
        Run the "test_addLoyaltyInstance" test before running this test.
        POST http://xxx/hmspass/v1/loyalty/instance/{instanceId}/addMessage
        """
        print("addMessageToLoyaltyInstance begin.")
        # ID of the loyalty instance to be updated.
        instanceId = "LoyaltyPass40001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the loyalty instance.
        urlSegment = "loyalty/instance/addMessage"
        to_dict = str_to_dict(messagesJson)
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, to_dict)
        print("Updated loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("addMessageToLoyaltyInstance end.")

    def test_updateLinkedOffersToLoyaltyInstance(self):
        """
        Update linked offers of a loyalty instance.
        Run the "test_addLoyaltyInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}/linkedoffers
        """
        print("updateLinkedOffersToLoyaltyInstance begin.")
        # ID of the loyalty instance to be updated.
        instanceId = "LoyaltyPass40001"
        """
        Create two lists of linked offer instances, one for offers you want to link to a loyalty instance and the
        other one for offers you want to remove from it. Each linked offer object has two parameters,
        passTypeIdentifier and instanceId, indicating which offer instance you want to add or remove. The adding list
        and the removing list should not contain same offer instances (with the same modelId and the same
        instanceId). You should make sure the offer instances you want to link already exist before using this
        interface.
        """
        # Read an example LinkedOfferInstanceIds from a JSON file.
        linkedOfferInstanceIdsJson = read_json_file("LinkedOfferInstanceIds.json")

        # Update relatedPassIds in the loyalty instance.
        urlSegment = "loyalty/instance/linkedoffers"
        to_dict = str_to_dict(linkedOfferInstanceIdsJson)
        responseInstance = updateLinkedOffersToLoyaltyInstance(urlSegment, instanceId, to_dict)
        print("Updated loyalty instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("updateLinkedOffersToLoyaltyInstance end.")

