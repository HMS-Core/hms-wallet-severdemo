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
# @File     : gift_card_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Gift card model tests.
    """

    def test_createGiftCardModel(self):
        """
        Create a new gift card model.
        Each gift card model indicates a style of gift card passes.
        POST http://XXX/hmspass/v1/giftcard/model
        """
        print("createGiftCardModel begin")
        # Read a gift card model from a JSON file.
        modelJson = read_json_file("GiftCardModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("Invalid model parameters.")
        urlSegment = "giftcard/model"
        # Post the new gift card model to HMS wallet server.
        responseModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted gift card model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("createGiftCardModel end")

    def test_getGiftCardModel(self):
        """
        Get a gift card model by its model ID.
        Run the "test_createGiftCardModel" test before running this test.
        GET http://xxx/hmspass/v1/giftcard/model/{modelId}
        """
        print("getGiftCardModel begin.")
        # ID of the gift card model you want to get.
        modelId = "GiftCardTestModel"
        # This is used to construct http URL.
        urlSegment = "giftcard/model/"
        # Get the gift card model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding gift card model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                           sort_keys=True,
                                                           indent=4))
        print("getGiftCardModel end.")

    def test_getGiftCardModelList(self):
        """
        Get gift card models belonging to a specific appId.
        Run the "test_createGiftCardModel" test before running this test.
        GET http://xxx/hmspass/v1/giftcard/model?session=XXX&pageSize=XXX
        """
        print("getGiftCardModelList begin.")
        # Get a list of gift card models.
        urlSegment = "giftcard/model"
        responseModels = getModels(urlSegment, 5)
        print("Gift card models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("getGiftCardModelList end.")

    def test_fullUpdateGiftCardModel(self):
        """
        Overwrite a gift card model.
        Run the "test_createGiftCardModel" test before running this test.
        PUT http://xxx/hmspass/v1/giftcard/model/{modelId}
        """
        print("fullUpdateGiftCardModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateGiftCardModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("Invalid model parameters.")
        # Update the gift card model.
        urlSegment = "giftcard/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated gift card model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("fullUpdateGiftCardModel end.")

    def test_partialUpdateGiftCardModel(self):
        """
        Update a gift card model.
        Run the "test_createGiftCardModel" test before running this test.
        PATCH http://xxx/hmspass/v1/giftcard/model/{modelId}
        """
        print("partialUpdateGiftCardModel begin.")
        # ID of the gift card model you want to update.
        modelId = "GiftCardTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateGiftCardModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the gift card model.
        urlSegment = "giftcard/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated gift card model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("partialUpdateGiftCardModel end.")

    def test_addMessageToGiftCardModel(self):
        """
        Add messages to a gift card model.
        Run the "test_createGiftCardModel" test before running this test.
        POST http://xxx/hmspass/v1/giftcard/model/{modelId}/addMessage
        """
        print("addMessageToGiftCardModel begin.")
        # ID of the gift card model you want to update.
        modelId = "GiftCardTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the gift card model.
        urlSegment = "giftcard/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated gift card model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("addMessageToGiftCardModel end.")
