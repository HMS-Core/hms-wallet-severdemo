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

# @Date     : 2020/06/12 17:43
# @Software : PyCharm
# @version  ：Python 3.6.8
# @File     : event_ticket_instance_test.py
import json
from unittest import TestCase
from urllib import parse

from wallet.hms.dto.hw_wallet_object import HwWalletObject
from wallet.hms.wallet_build_service import postHwWalletObjectToWalletServer, \
    addMessageToHwWalletObject, getHwWalletObjectById, getInstances, \
    fullUpdateHwWalletObject, partialUpdateHwWalletObject

from wallet.util.common_util import read_json_file, str_to_dict, dict_to_obj
from wallet.util.hw_wallet_object_util import validateWalletInstance


class Test(TestCase):
    """
    Event ticket instance tests.
    """

    def test_addEventTicketInstance(self):
        """
        Add an event ticket instance to HMS wallet server.
        Run the "test_createEventTicketModel" test before running this test.
        After using this API, you will use a thin JWE to bind this instance to a user. You can also add an instance by
        sending a JWE with all instance information, without using this API. See JWE example methods at the bottom.
        POST http://XXX/hmspass/v1/eventticket/instance
        """
        print("addEventTicketInstance begin")
        # Read an event ticket instance from a JSON file.
        objectJson = read_json_file("EventTicketInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(objectJson), HwWalletObject())
        if not validateWalletInstance(hwWalletObject):
            raise Exception("Invalid instance parameters.")
        urlSegment = "eventticket/instance"
        # Post the new event ticket instance to HMS wallet server.
        responseInstance = postHwWalletObjectToWalletServer(urlSegment, hwWalletObject)
        print("Posted event ticket instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                            sort_keys=True, indent=4))
        print("addEventTicketInstance end")

    def test_getEventTicketInstance(self):
        """
        Get an event ticket instance by its instance ID.
        Run the "test_addEventTicketInstance" test before running this test.
        GET http://xxx/hmspass/v1/eventticket/instance/{instanceId}
        """
        print("getEventTicketInstance begin.")
        # ID of the event ticket instance you want to get.
        instanceId = "EventTicketPass10001"
        # This is used to construct http URL.
        urlSegment = "eventticket/instance/"
        # Get the event ticket instance.
        responseInstance = getHwWalletObjectById(urlSegment, instanceId)
        print(
            "Corresponding event ticket instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                                 sort_keys=True,
                                                                 indent=4))
        print("getEventTicketInstance end.")

    def test_getEventTicketInstanceList(self):
        """
        Get event ticket instances belonging to a specific event ticket model.
        Run the "test_addEventTicketInstance" test before running this test.
        GET http://xxx/hmspass/v1/eventticket/instance?modelId=XXX&session=XXX&pageSize=XXX
        """
        print("getEventTicketInstanceList begin.")

        # Model ID of event ticket instances you want to get.
        modelId = "EventTicketTestModel"

        # Get a list of the event ticket instances.
        urlParameter = "eventticket/instance"
        responseInstances = getInstances(urlParameter, modelId, 5)
        print("Event ticket instances list:: " + json.dumps(responseInstances, default=lambda obj: obj.__dict__,
                                                            sort_keys=True, indent=4))
        print("getEventTicketInstanceList end.")

    def test_fullUpdateEventTicketInstance(self):
        """
        Overwrite an event ticket instance.
        Run the "test_addEventTicketInstance" test before running this test.
        PUT http://xxx/hmspass/v1/eventticket/instance/{instanceId}
        """
        print("fullUpdateEventTicketInstance begin.")

        # Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        instanceJson = read_json_file("FullUpdateEventTicketInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(instanceJson), HwWalletObject())
        instanceId = hwWalletObject.get_serialNumber()

        # Update the Event ticket instance.
        urlParameter = "eventticket/instance/"
        responseInstance = fullUpdateHwWalletObject(urlParameter, instanceId, hwWalletObject)
        print("Updated event ticket instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                             sort_keys=True, indent=4))
        print("fullUpdateEventTicketInstance end.")

    def test_partialUpdateEventTicketInstance(self):
        """
        Update an event ticket instance.
        Run the "test_addEventTicketInstance" test before running this test.
        PATCH http://xxx/hmspass/v1/eventticket/instance/{instanceId}
        """
        print("partialUpdateEventTicketInstance begin.")
        # ID of the event ticket instance you want to update.
        instanceId = "EventTicketPass10001"
        # Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        instanceJson = read_json_file("PartialUpdateEventTicketInstance.json")
        hwWalletObject: HwWalletObject = dict_to_obj(str_to_dict(instanceJson), HwWalletObject())
        hwWalletObject.set_serialNumber(instanceId)

        # Update the event ticket instance.
        urlSegment = "eventticket/instance/"
        responseInstance = partialUpdateHwWalletObject(urlSegment, instanceId, hwWalletObject)
        print("Updated event ticket instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                             sort_keys=True, indent=4))
        print("partialUpdateEventTicketInstance end.")

    def test_addMessageToEventTicketInstance(self):
        """
        Add messages to an event ticket instance.
        Run the "test_addEventTicketInstance" test before running this test.
        POST http://xxx/hmspass/v1/eventticket/instance/{instanceId}/addMessage
        """
        print("addMessageToEventTicketInstance begin.")
        # ID of the event ticket instance you want to update.
        instanceId = "EventTicketPass10001"

        # Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        # The list should not contain multiple messages with the same key. You can update an existing message by adding
        # a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        # messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        # 10 messages at a time.

        # Read messages from a JSON file.
        messagesJson = read_json_file("Messages.json")

        # Add messages to the event ticket instance.
        urlSegment = "eventticket/instance/addMessage"
        responseInstance = addMessageToHwWalletObject(urlSegment, instanceId, str_to_dict(messagesJson))
        print("Updated event ticket instance: " + json.dumps(responseInstance, default=lambda obj: obj.__dict__,
                                                             sort_keys=True, indent=4))
        print("addMessageToEventTicketInstance end.")


