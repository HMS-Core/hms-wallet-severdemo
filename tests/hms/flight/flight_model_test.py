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
# @File     : flight_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Flight model tests.
    """

    def test_createFlightModel(self):
        """
        Create a new flight model.
        Each flight model indicates a style of flight passes.
        POST http://XXX/hmspass/v1/flight/model
        """
        print("createFlightModel begin")
        # Read a flight model from a JSON file.
        modelJson = read_json_file("FlightModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("Invalid model parameters.")
        urlSegment = "flight/model"
        # Post the new flight model to HMS wallet server.
        responseModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted flight model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                   sort_keys=True, indent=4))
        print("createFlightModel end")

    def test_getFlightModel(self):
        """
        Get a flight model by its model ID.
        Run the "test_createFlightModel" test before running this test.
        GET http://xxx/hmspass/v1/flight/model/{modelId}
        """
        print("getFlightModel begin.")
        # ID of the flight model you want to get.
        modelId = "FlightTestModel"
        # This is used to construct http URL.
        urlSegment = "flight/model/"
        # Get the flight model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding flight model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__, sort_keys=True,
                                                        indent=4))
        print("getFlightModel end.")

    def test_getFlightModelList(self):
        """
        Get flight models belonging to a specific appId.
        Run the "test_createFlightModel" test before running this test.
        GET http://xxx/hmspass/v1/flight/model?session=XXX&pageSize=XXX
        """
        print("getFlightModelList begin.")
        # Get a list of flight models.
        urlSegment = "flight/model"
        responseModels = getModels(urlSegment, 5)
        print("Flight models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                  sort_keys=True, indent=4))
        print("getFlightModelList end.")

    def test_fullUpdateFlightModel(self):
        """
        Overwrite a flight model.
        Run the "test_createFlightModel" test before running this test.
        PUT http://xxx/hmspass/v1/flight/model/{modelId}
        """
        print("fullUpdateFlightModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateFlightModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("Invalid model parameters.")
        # Update the flight model.
        urlSegment = "flight/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated flight model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("fullUpdateFlightModel end.")

    def test_partialUpdateFlightModel(self):
        """
        Update a flight model.
        Run the "test_createFlightModel" test before running this test.
        PATCH http://xxx/hmspass/v1/flight/model/{modelId}
        """
        print("partialUpdateFlightModel begin.")
        # ID of the flight model you want to update.
        modelId = "FlightTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateFlightModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the flight model.
        urlSegment = "flight/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated flight model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("partialUpdateFlightModel end.")

    def test_addMessageToFlightModel(self):
        """
        Add messages to a flight model.
        Run the "test_createFlightModel" test before running this test.
        POST http://xxx/hmspass/v1/flight/model/{modelId}/addMessage
        """
        print("addMessageToFlightModel begin.")
        # ID of the flight model you want to update.
        modelId = "FlightTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the flight model.
        urlSegment = "flight/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated flight model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                    sort_keys=True, indent=4))
        print("addMessageToFlightModel end.")
