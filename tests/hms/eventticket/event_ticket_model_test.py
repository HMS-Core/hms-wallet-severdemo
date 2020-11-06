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
# @File     : event_ticket_model_test
import json
from unittest import TestCase

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, getHwWalletObjectById, getModels, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject, addMessageToHwWalletObject
from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletModel


class Test(TestCase):
    """
    Event ticket model tests.
    """

    def test_createEventTicketModel(self):
        """
        Create a new event ticket model.
        Each event ticket model indicates a style of event ticket passes..
        POST http://XXX/hmspass/v1/eventticket/model
        """
        print("createEventTicketModel begin")
        # Read an event ticket model from a JSON file.
        modelJson = read_json_file("EventTicketModel.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(hwWalletObject):
            raise Exception("Invalid model parameters.")
        urlSegment = "eventticket/model"
        # Post the new event ticket model to HMS wallet server.
        responseModel = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted event ticket model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                         sort_keys=True, indent=4))
        print("createEventTicketModel end")

    def test_getEventTicketModel(self):
        """
        Get an event ticket model by its model ID.
        Run the "test_createEventTicketModel" test before running this test.
        GET http://xxx/hmspass/v1/eventticket/model/{modelId}
        """
        print("getEventTicketModel begin.")
        # ID of the event ticket model you want to get.
        modelId = "EventTicketTestModel"
        # This is used to construct http URL.
        urlSegment = "eventticket/model/"
        # Get the event ticket model.
        responseModel = getHwWalletObjectById(urlSegment, modelId)
        print(
            "Corresponding event ticket model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                              sort_keys=True,
                                                              indent=4))
        print("getEventTicketModel end.")

    def test_getEventTicketModelList(self):
        """
        Get event ticket models belonging to a specific appId.
        Run the "test_createEventTicketModel" test before running this test.
        GET http://xxx/hmspass/v1/eventticket/model?session=XXX&pageSize=XXX
        """
        print("getEventTicketModelList begin.")
        # Get a list of event ticket models.
        urlSegment = "eventticket/model"
        responseModels = getModels(urlSegment, 5)
        print("Event ticket models list: " + json.dumps(responseModels, default=lambda obj: obj.__dict__,
                                                        sort_keys=True, indent=4))
        print("getEventTicketModelList end.")

    def test_fullUpdateEventTicketModel(self):
        """
        Overwrite an event ticket model.
        Run the "test_createEventTicketModel" test before running this test.
        PUT http://xxx/hmspass/v1/eventticket/model/{modelId}
        """
        print("fullUpdateEventTicketModel begin.")
        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        modelJson = read_json_file("FullUpdateEventTicketModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        if not validateWalletModel(model):
            raise Exception("Invalid model parameters.")
        # Update the event ticket model.
        urlSegment = "eventticket/model/"
        responseModel = fullUpdateHwWalletObject(urlSegment, model.get_passStyleIdentifier(), model)
        print("Updated event ticket model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("fullUpdateEventTicketModel end.")

    def test_partialUpdateEventTicketModel(self):
        """
        Update an event ticket model.
        Run the "test_createEventTicketModel" test before running this test.
        PATCH http://xxx/hmspass/v1/eventticket/model/{modelId}
        """
        print("partialUpdateEventTicketModel begin.")
        # ID of the event ticket model you want to update.
        modelId = "EventTicketTestModel"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        modelJson = read_json_file("PartialUpdateEventTicketModel.json")
        model: HwWalletObject = dict_to_obj(str_to_dict(modelJson), HwWalletObject())
        model.set_serialNumber(modelId)

        # Update the event ticket model.
        urlSegment = "eventticket/model/"
        responseModel = partialUpdateHwWalletObject(urlSegment, modelId, model)
        print("Updated event ticket model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("partialUpdateEventTicketModel end.")

    def test_addMessageToEventTicketModel(self):
        """
        Add messages to an event ticket model.
        Run the "test_createEventTicketModel" test before running this test.
        POST http://xxx/hmspass/v1/eventticket/model/{modelId}/addMessage
        """
        print("addMessageToEventTicketModel begin.")
        # ID of the event ticket model you want to update.
        modelId = "EventTicketTestModel"

        # Create a list of messages you want to add to a model. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        # and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        # messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the event ticket model.
        urlSegment = "eventticket/model/addMessage"
        responseModel = addMessageToHwWalletObject(urlSegment, modelId, str_to_dict(messagesJson))
        print("Updated event ticket model: " + json.dumps(responseModel, default=lambda obj: obj.__dict__,
                                                          sort_keys=True, indent=4))
        print("addMessageToEventTicketModel end.")
