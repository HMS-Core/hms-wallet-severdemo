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
# @File     : offer_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Offer model tests.
    """

    def test_createOfferModel(self):
        """
        Create a new offer model.
        Each offer model indicates a style of offer passes.
        POST http://XXX/hmspass/v1/offer/model
        """
        print("createOfferModel begin")
        # Read a loyalty model from a JSON file.
        modelJson = read_json_file("OfferModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("hwWallet model is error")
        urlSegment = "offer/model"
        # Post the new offer model to HMS wallet server.
        loyaltyModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted offer model: " + json.dumps(loyaltyModel, default=lambda obj: obj.__dict__,
                                                  sort_keys=True, indent=4))
        print("createOfferModel end")

    def test_getOfferModel(self):
        """
        Get an offer model by its model ID.
        Run the "test_createOfferModel" test before running this test.
        GET http://xxx/hmspass/v1/offer/model/{modelId}
        """
        print("getOfferModel begin.")
        # ID of the offer model you want to get.
        modelId = "OfferTestModel"
        # This is used to construct http URL.
        urlSegment = "offer/model/"
        # Get the offer model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding offer model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__, sort_keys=True,
                                                       indent=4))
        print("getOfferModel end.")

    def test_getOfferModelList(self):
        """
        Get offer models belonging to a specific appId.
        Run the "test_createOfferModel" test before running this test.
        GET http://xxx/hmspass/v1/offer/model?session=XXX&pageSize=XXX
        """
        print("getOfferModelList begin.")
        # Get a list of offer models.
        urlSegment = "offer/model"
        responseModels = getModels(urlSegment, 5)
        print("Offer models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                 sort_keys=True, indent=4))
        print("getOfferModelList end.")

    def test_fullUpdateOfferModel(self):
        """
        Overwrite an offer model.
        Run the "test_createOfferModel" test before running this test.
        PUT http://xxx/hmspass/v1/offer/model/{modelId}
        """
        print("fullUpdateOfferModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateOfferModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("model is error")
        # Update the offer model.
        urlSegment = "offer/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated offer model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("fullUpdateOfferModel end.")

    def test_partialUpdateOfferModel(self):
        """
        Update an offer model.
        Run the "test_createOfferModel" test before running this test.
        PATCH http://xxx/hmspass/v1/offer/model/{modelId}
        """
        print("partialUpdateOfferModel begin.")
        # ID of the offer model you want to update.
        modelId = "OfferTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateOfferModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the offer model.
        urlSegment = "offer/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated offer model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("partialUpdateOfferModel end.")

    def test_addMessageToOfferModel(self):
        """
        Add messages to an offer model.
        Run the "test_createOfferModel" test before running this test.
        POST http://xxx/hmspass/v1/offer/model/{modelId}/addMessage
        """
        print("addMessageToOfferModel begin.")
        # ID of the offer model you want to update.
        modelId = "OfferTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the offer model.
        urlSegment = "offer/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated offer model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("addMessageToOfferModel end.")
