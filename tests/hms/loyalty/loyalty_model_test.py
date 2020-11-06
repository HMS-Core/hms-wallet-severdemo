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
# @Date     : 2020/6/20
# @version  : Python 3.8.3
# @File     : loyalty_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Loyalty model tests.
    """

    def test_createLoyaltyModel(self):
        """
        Create a new loyalty model.
        Each loyalty model indicates a style of loyalty passes.
        POST http://XXX/hmspass/v1/loyalty/model
        """
        print("createLoyaltyModel begin")
        # Read a loyalty model from a JSON file.
        modelJson = read_json_file("LoyaltyModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("hwWallet model is error")
        urlSegment = "loyalty/model"
        # Post the new loyalty model to HMS wallet server.
        responseModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted loyalty model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("createLoyaltyModel end")

    def test_getLoyaltyModel(self):
        """
        Get a loyalty model by its model ID.
        Run the "test_createLoyaltyModel" test before running this test.
        GET http://xxx/hmspass/v1/loyalty/model/{modelId}
        """
        print("getLoyaltyModel begin.")
        # ID of the loyalty model you want to get.
        modelId = "LoyaltyTestModel"
        # This is used to construct http URL.
        urlSegment = "loyalty/model/"
        # Get the loyalty model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding loyalty model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                         sort_keys=True,
                                                         indent=4))
        print("getLoyaltyModel end.")

    def test_getLoyaltyModelList(self):
        """
        Get loyalty models belonging to a specific appId.
        Run the "test_createLoyaltyModel" test before running this test.
        GET http://xxx/hmspass/v1/loyalty/model?session=XXX&pageSize=XXX
        """
        print("getLoyaltyModelList begin.")
        # Get a list of loyalty models.
        urlSegment = "loyalty/model"
        responseModels = getModels(urlSegment, 5)
        print("Loyalty models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("getLoyaltyModelList end.")

    def test_fullUpdateLoyaltyModel(self):
        """
        Overwrite a loyalty model.
        Run the "test_createLoyaltyModel" test before running this test.
        PUT http://xxx/hmspass/v1/loyalty/model/{modelId}
        """
        print("fullUpdateLoyaltyModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateLoyaltyModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("model is error")
        # Update the loyalty model.
        urlSegment = "loyalty/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated loyalty model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("fullUpdateLoyaltyModel end.")

    def test_partialUpdateLoyaltyModel(self):
        """
        Update a loyalty model.
        Run the "test_createLoyaltyModel" test before running this test.
        PATCH http://xxx/hmspass/v1/loyalty/model/{modelId}
        """
        print("partialUpdateLoyaltyModel begin.")
        # ID of the loyalty model you want to update.
        modelId = "LoyaltyTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateLoyaltyModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the loyalty model.
        urlSegment = "loyalty/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated loyalty model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("partialUpdateLoyaltyModel end.")

    def test_addMessageToLoyaltyModel(self):
        """
        Add messages to a loyalty model.
        Run the "test_createLoyaltyModel" test before running this test.
        POST http://xxx/hmspass/v1/loyalty/model/{modelId}/addMessage
        """
        print("addMessageToLoyaltyModel begin.")
        # ID of the loyalty model you want to update.
        modelId = "LoyaltyTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the loyalty model.
        urlSegment = "loyalty/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated loyalty model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("addMessageToLoyaltyModel end.")
