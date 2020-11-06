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
# @File     : transit_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Transit model tests.
    """

    def test_createTransitModel(self):
        """
        Create a new transit model.
        Each transit model indicates a style of transit passes.
        POST http://XXX/hmspass/v1/transit/model
        """
        print("createTransitModel begin")
        # Read a transit model from a JSON file.
        modelJson = read_json_file("TransitModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("hwWallet model is error")
        urlSegment = "transit/model"
        # Post the new transit model to HMS wallet server.
        responseModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted transit model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("createTransitModel end")

    def test_getTransitModel(self):
        """
        Get a transit model by its model ID.
        Run the "test_createTransitModel" test before running this test.
        GET http://xxx/hmspass/v1/transit/model/{modelId}
        """
        print("getTransitModel begin.")
        # ID of the transit model you want to get.
        modelId = "TransitTestModel"
        # This is used to construct http URL.
        urlSegment = "transit/model/"
        # Get the transit model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding transit model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                         sort_keys=True,
                                                         indent=4))
        print("getTransitModel end.")

    def test_getTransitModelList(self):
        """
        Get transit models belonging to a specific appId.
        Run the "test_createTransitModel" test before running this test.
        GET http://xxx/hmspass/v1/transit/model?session=XXX&pageSize=XXX
        """
        print("getTransitModelList begin.")
        # Get a list of transit models.
        urlSegment = "transit/model"
        responseModels = getModels(urlSegment, 5)
        print("Transit models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("getTransitModelList end.")

    def test_fullUpdateTransitModel(self):
        """
        Overwrite a transit model.
        Run the "test_createTransitModel" test before running this test.
        PUT http://xxx/hmspass/v1/transit/model/{modelId}
        """
        print("fullUpdateTransitModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateTransitModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("model is error")
        # Update the transit model.
        urlSegment = "transit/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated transit model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("fullUpdateTransitModel end.")

    def test_partialUpdateTransitModel(self):
        """
        Update a transit model.
        Run the "test_createTransitModel" test before running this test.
        PATCH http://xxx/hmspass/v1/transit/model/{modelId}
        """
        print("partialUpdateTransitModel begin.")
        # ID of the transit model you want to update.
        modelId = "TransitTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateTransitModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the transit model.
        urlSegment = "transit/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated transit model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("partialUpdateTransitModel end.")

    def test_addMessageToTransitModel(self):
        """
        Add messages to a transit model.
        Run the "test_createTransitModel" test before running this test.
        POST http://xxx/hmspass/v1/transit/model/{modelId}/addMessage
        """
        print("addMessageToTransitModel begin.")
        # ID of the transit model you want to update.
        modelId = "TransitTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read an example Messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the transit model.
        urlSegment = "transit/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated transit model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("addMessageToTransitModel end.")
