/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.wallet.hms.eventticket;

import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

/**
 * Event ticket instance tests.
 *
 * @since 2019-12-12
 */
public class EventTicketInstanceTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add an event ticket instance to HMS wallet server.
     * Run the "createEventTicketModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/eventticket/instance
     */
    @Test
    public void addEventTicketInstance() {
        System.out.println("addEventTicketInstance begin.");

        // Read an event ticket instance from a JSON file.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("EventTicketInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Post the new event ticket instance to HMS wallet server.
        String urlSegment = "/v1/eventticket/instance";
        JSONObject responseInstance =
            serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(instance));
        System.out.println("Posted event ticket instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get an event ticket instance by its instance ID.
     * Run the "createEventTicketInstance" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void getEventTicketInstance() {
        System.out.println("getEventTicketInstance begin.");

        // ID of the event ticket instance you want to get.
        String instanceId = "EventTicketPass10001";

        // Get the event ticket instance.
        String urlSegment = "/v1/eventticket/instance/";
        JSONObject responseInstance = serverApiService.getHwWalletObjectById(urlSegment, instanceId);
        System.out.println("Corresponding event ticket instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get event ticket instances belonging to a specific event ticket model.
     * Run the "createEventTicketInstance" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getEventTicketInstanceList() {
        System.out.println("getEventTicketInstanceList begin.");

        // Model ID of event ticket instances you want to get.
        String modelId = "EventTicketTestModel";

        // Get a list of the event ticket instances.
        String urlSegment = "/v1/eventticket/instance";

        JSONArray instances = serverApiService.getInstances(urlSegment, modelId, 5);
        System.out.println("Total instances count: " + instances.size());
        System.out.println("Instances list: " + instances.toJSONString());
    }

    /**
     * Overwrite an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void fullUpdateEventTicketInstance() {
        System.out.println("fullUpdateEventTicketInstance begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateEventTicketInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Update the event ticket instance.
        String urlSegment = "/v1/eventticket/instance/";
        JSONObject responseInstance = serverApiService.fullUpdateHwWalletObject(urlSegment,
            instance.getString("serialNumber"), JSONObject.toJSONString(instance));
        System.out.println("Updated event ticket instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void partialUpdateEventTicketInstance() {
        System.out.println("partialUpdateEventTicketInstance begin.");

        // ID of the event ticket instance you want to update.
        String instanceId = "EventTicketPass10001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        String instanceStr = ConfigUtil.readFile("PartialUpdateEventTicketInstance.json");

        // Update the event ticket instance.
        String urlSegment = "/v1/eventticket/instance/";
        JSONObject responseInstance = serverApiService.partialUpdateHwWalletObject(urlSegment, instanceId, instanceStr);
        System.out.println("Updated event ticket instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Add messages to an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * POST http://xxx/hmspass/v1/eventticket/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToEventTicketInstance() {
        System.out.println("addMessageToEventTicketInstance begin.");

        // ID of the event ticket instance you want to update.
        String instanceId = "EventTicketPass10001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the event ticket instance.
        String urlSegment = "/v1/eventticket/instance/";
        JSONObject responseInstance = serverApiService.addMessageToHwWalletObject(urlSegment, instanceId, messagesStr);
        System.out.println("Updated event ticket instance: " + JSONObject.toJSONString(responseInstance));
    }
}
