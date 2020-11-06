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
# @File     : flight_instance_test.py
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
    Flight instance tests.
    """

    def test_addFlightInstance(self):
        """
        Add a flight instance to HMS wallet server.
        Run the "test_createFlightModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/flight/instance
        """
        print("addFlightInstance begin")
        # Read a flight instance from a JSON file.
        objectJson = read_json_file("FlightInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(objectJson), HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("Invalid instance parameters.")
        urlSegment = "flight/instance"
        # Post the new flight instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted flight instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                      sort_keys=True, indent=4))
        print("addFlightInstance end")

    def test_getFlightInstance(self):
        """
        Get a flight instance by its instance ID.
        Run the "test_addFlightInstance" test before running this test.
        GET http://xxx/hmspass/v1/flight/instance/{instanceId}
        """
        print("getFlightInstance begin.")
        # ID of the flight instance you want to get.
        instanceId = "FlightPass20001"
        # This is used to construct http URL.
        urlSegment = "flight/instance/"
        # Get the flight instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding flight instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                           sort_keys=True,
                                                           indent=4))
        print("getFlightInstance end.")

    def test_getFlightInstanceList(self):
        """
        Get flight instance belonging to a specific flight model.
        Run the "test_addFlightInstance" test before running this test.
        GET http://xxx/hmspass/v1/flight/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getFlightInstanceList begin.")

        # Model ID of flight instances you want to get.
        modelId = "FlightTestModel"

        # Get a list of flight instances.
        urlSegment = "flight/instance"
        responseInstances = getInstances(urlSegment, modelId, 5)
        print("Flight instances list: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                     sort_keys=True, indent=4))
        print("getFlightInstanceList end.")

    def test_fullUpdateFlightInstance(self):
        """
        Overwrite a flight instance.
        Run the "test_addFlightInstance" test before running this test.
        PUT http://xxx/hmspass/v1/flight/instance/{instanceId}
        """
        print("fullUpdateFlightInstance begin.")

        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        instanceJson = read_json_file("FullUpdateFlightInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(instanceJson), HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the flight instance.
        urlSegment = "flight/instance/"
        responseInstance = fullUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated flight instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("fullUpdateFlightInstance end.")

    def test_partialUpdateFlightInstance(self):
        """
        Update a flight instance.
        Run the "test_addFlightInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/flight/instance/{instanceId}
        """
        print("partialUpdateFlightInstance begin.")
        # ID of the flight instance you want to update.
        instanceId = "FlightPass20001"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        instanceJson = read_json_file("PartialUpdateFlightInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(instanceJson), HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the flight instance.
        urlSegment = "flight/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated flight instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("partialUpdateFlightInstance end.")

    def test_addMessageToFlightInstance(self):
        """
        Add messages to a flight instance.
        Run the "test_addFlightInstance" test before running this test.
        POST http://xxx/hmspass/v1/flight/instance/{instanceId}/addMessage
        """
        print("addMessageToFlightInstance begin.")
        # ID of the flight instance you want to update.
        instanceId = "FlightPass20001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the flight instance.
        urlSegment = "flight/instance/addMessage"
        to_dict = str_to_dict(messagesJson)
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, to_dict)
        print("Updated flight instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                       sort_keys=True, indent=4))
        print("addMessageToFlightInstance end.")


