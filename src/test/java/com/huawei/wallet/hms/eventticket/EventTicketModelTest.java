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
 * Event ticket model tests.
 *
 * @since 2019-12-12
 */
public class EventTicketModelTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Create a new event ticket model.
     * Each event ticket model indicates a style of event ticket passes.
     * POST http://XXX/hmspass/v1/eventticket/model
     */
    @Test
    public void createEventTicketModel() {
        System.out.println("createEventTicketModel begin.");

        // Read an event ticket model from a JSON file.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("EventTicketModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Post the new event ticket model to HMS wallet server.
        String urlSegment = "eventticket/model";
        JSONObject responseModel = serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(model));
        System.out.println("Posted event ticket model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get an event ticket model by its model ID.
     * Run the "createEventTicketModel" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/model/{modelId}
     */
    @Test
    public void getEventTicketModel() {
        System.out.println("getEventTicketModel begin.");

        // ID of the event ticket model you want to get.
        String modelId = "EventTicketModelTest";

        // Get the event ticket model.
        String urlSegment = "eventticket/model/";
        JSONObject responseModel = serverApiService.getHwWalletObjectById(urlSegment, modelId);
        System.out.println("Corresponding event ticket model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get event ticket models belonging to a specific appId.
     * Run the "createEventTicketModel" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getEventTicketModelList() {
        System.out.println("getEventTicketModelList begin.");

        // Get a list of event ticket models.
        String urlSegment = "eventticket/model";

        JSONArray models = serverApiService.getModels(urlSegment, 5);
        System.out.println("Total models count: " + models.size());
        System.out.println("Models list: " + models.toJSONString());
    }

    /**
     * Overwrite an event ticket model.
     * Run the "createEventTicketModel" test before running this test.
     * PUT http://xxx/hmspass/v1/eventticket/model/{modelId}
     */
    @Test
    public void fullUpdateEventTicketModel() {
        System.out.println("fullUpdateEventTicketModel begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateEventTicketModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Update the event ticket model.
        String urlSegment = "eventticket/model/";
        JSONObject responseModel = serverApiService.fullUpdateHwWalletObject(urlSegment,
            model.getString("passStyleIdentifier"), JSONObject.toJSONString(model));
        System.out.println("Updated event ticket model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Update an event ticket model.
     * Run the "createEventTicketModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/eventticket/model/{modelId}
     */
    @Test
    public void partialUpdateEventTicketModel() {
        System.out.println("partialUpdateEventTicketModel begin.");

        // ID of the event ticket model you want to update.
        String modelId = "eventTicketModelTest";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        String modelStr = ConfigUtil.readFile("PartialUpdateEventTicketModel.json");

        // Update the event ticket model.
        String urlSegment = "eventticket/model/";
        JSONObject responseModel = serverApiService.partialUpdateHwWalletObject(urlSegment, modelId, modelStr);
        System.out.println("Updated event ticket model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Add messages to an event ticket model.
     * Run the "createEventTicketModel" test before running this test.
     * POST http://xxx/hmspass/v1/eventticket/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToEventTicketModel() {
        System.out.println("addMessageToEventTicketModel begin.");

        // ID of the event ticket model you want to update.
        String modelId = "eventTicketModelTest";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the event ticket model.
        String urlSegment = "eventticket/model/";
        JSONObject responseModel = serverApiService.addMessageToHwWalletObject(urlSegment, modelId, messagesStr);
        System.out.println("Updated event ticket model: " + JSONObject.toJSONString(responseModel));
    }
}
