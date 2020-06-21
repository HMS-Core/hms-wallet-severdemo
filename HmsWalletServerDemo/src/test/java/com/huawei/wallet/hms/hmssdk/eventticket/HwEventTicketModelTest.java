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

package com.huawei.wallet.hms.hmssdk.eventticket;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.alibaba.fastjson.JSONObject;
import com.huawei.wallet.hms.hmssdk.dto.HwWalletObject;
import com.huawei.wallet.hms.hmssdk.WalletBuildService;
import com.huawei.wallet.hms.hmssdk.impl.WalletBuildServiceImpl;
import com.huawei.wallet.util.CommonUtil;

import com.huawei.wallet.util.HwWalletObjectUtil;
import org.junit.Test;

import java.util.List;

/**
 * Event-ticket-model tests.
 *
 * @since 2019-12-12
 */
public class HwEventTicketModelTest {

    /**
     * Create a new event ticket model.
     * Each event ticket model indicates a style of event ticket instances. The body of an event ticket model is
     * HwWalletObject, which is the same as an event ticket instance. Some parameters of all event ticket instances with
     * the same style are the same. These common parameters are stored in their event ticket model.
     * POST http://XXX/hmspass/v1/eventticket/model
     */
    @Test
    public void createEventTicketModel() {
        System.out.println("createEventTicketModel begin");

        // Read an example event ticket model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("EventTicketModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the event ticket model you want to create. Event ticket instances belong to this model
        // share these parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new event ticket model to the wallet server.
            String urlParameter = "eventticket/model";
            HwWalletObject eventTicketModel =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("eventTicketModel JSON is: " + CommonUtil.toJson(eventTicketModel));
            System.out.println("createEventTicketModel end");
        }
    }

    /**
     * Overwrite an event ticket model.
     * Run the "createEventTicketModel" test before running this test.
     * PUT http://xxx/hmspass/v1/eventticket/model/{modelId}
     */
    @Test
    public void fullUpdateEventTicketModel() {
        System.out.println("fullUpdateEventTicketModel begin.");

        // ID of the event ticket model you want to update.
        String modelId = "eventTicketModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding event ticket model will be replaced by
        // this HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateEventTicketModel.json");

        // Update the eventTicket model.
        String urlParameter = "eventticket/model/";
        WalletBuildService eventTicketBuildService = new WalletBuildServiceImpl();
        HwWalletObject eventTicketModelUpdated =
            eventTicketBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(eventTicketModelUpdated.getPassStyleIdentifier()));
        System.out.println("eventTicketModelUpdated JSON is: " + CommonUtil.toJson(eventTicketModelUpdated));
        System.out.println("fullUpdateEventTicketModel end.");
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

        // Read an example HwWalletObject from a JSON file. The corresponding event ticket model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateEventTicketModel.json");

        // Update the eventTicket model.
        String urlParameter = "eventticket/model/";
        WalletBuildService eventTicketBuildService = new WalletBuildServiceImpl();
        HwWalletObject eventTicketModelUpdated =
            eventTicketBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(eventTicketModelUpdated.getPassStyleIdentifier()));
        System.out.println("eventTicketModelUpdated JSON is: " + CommonUtil.toJson(eventTicketModelUpdated));
        System.out.println("partialUpdateEventTicketModel end.");
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
        String urlParameter = "eventticket/model/";
        HwWalletObject eventTicketModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("eventTicketModel JSON is: " + CommonUtil.toJson(eventTicketModel));
        System.out.println("getEventTicketModel end.");
    }

    /**
     * Get some event ticket models created by the developer who is sending this request.
     * Run the "createEventTicketModel" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getEventTicketModelList() {
        System.out.println("getEventTicketModelList begin.");

        // Get a list of event ticket models.
        String urlParameter = "eventticket/model";
        List<HwWalletObject> eventTicketModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("eventTicketModels JSON is: " + CommonUtil.toJson(eventTicketModels));
        System.out.println("getEventTicketModelList end.");
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

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the event ticket model.
        String urlParameter = "eventticket/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject eventTicketModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(eventTicketModelUpdated.getPassStyleIdentifier()));
        System.out.println("eventTicketModelUpdated JSON is: " + CommonUtil.toJson(eventTicketModelUpdated));
        System.out.println("addMessageToEventTicketModel end.");
    }
}
