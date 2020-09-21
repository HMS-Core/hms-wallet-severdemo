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

package com.huawei.wallet.hms.offer;

import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

/**
 * Offer model tests.
 *
 * @since 2019-12-12
 */
public class OfferModelTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Create a new offer model.
     * Each offer model indicates a style of offer passes.
     * POST http://XXX/hmspass/v1/offer/model
     */
    @Test
    public void createOfferModel() {
        System.out.println("createOfferModel begin.");

        // Read an offer model from a JSON file.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("OfferModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Post the new offer model to HMS wallet server.
        String urlSegment = "offer/model";
        JSONObject responseModel = serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(model));
        System.out.println("Posted offer model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get an offer model by its model ID.
     * Run the "createOfferModel" test before running this test.
     * GET http://xxx/hmspass/v1/offer/model/{modelId}
     */
    @Test
    public void getOfferModel() {
        System.out.println("getOfferModel begin.");

        // ID of the offer model you want to get.
        String modelId = "OfferModelTest";

        // Get the offer model.
        String urlSegment = "offer/model/";
        JSONObject responseModel = serverApiService.getHwWalletObjectById(urlSegment, modelId);
        System.out.println("Corresponding offer model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get offer models belonging to a specific appId.
     * Run the "createOfferModel" test before running this test.
     * GET http://xxx/hmspass/v1/offer/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getOfferModelList() {
        System.out.println("getOfferModelList begin.");

        // Get a list of offer models.
        String urlSegment = "offer/model";

        JSONArray models = serverApiService.getModels(urlSegment, 5);
        System.out.println("Total models count: " + models.size());
        System.out.println("Models list: " + models.toJSONString());
    }

    /**
     * Overwrite an offer model.
     * Run the "createOfferModel" test before running this test.
     * PUT http://xxx/hmspass/v1/offer/model/{modelId}
     */
    @Test
    public void fullUpdateOfferModel() {
        System.out.println("fullUpdateOfferModel begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateOfferModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Update the offer model.
        String urlSegment = "offer/model/";
        JSONObject responseModel = serverApiService.fullUpdateHwWalletObject(urlSegment,
            model.getString("passStyleIdentifier"), JSONObject.toJSONString(model));
        System.out.println("Updated offer model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Update an offer model.
     * Run the "createOfferModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/offer/model/{modelId}
     */
    @Test
    public void partialUpdateOfferModel() {
        System.out.println("partialUpdateOfferModel begin.");

        // ID of the offer model you want to update.
        String modelId = "offerModelTest";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        String modelStr = ConfigUtil.readFile("PartialUpdateOfferModel.json");

        // Update the offer model.
        String urlSegment = "offer/model/";
        JSONObject responseModel = serverApiService.partialUpdateHwWalletObject(urlSegment, modelId, modelStr);
        System.out.println("Updated offer model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Add messages to an offer model.
     * Run the "createOfferModel" test before running this test.
     * POST http://xxx/hmspass/v1/offer/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToOfferModel() {
        System.out.println("addMessageToOfferModel begin.");

        // ID of the offer model you want to update.
        String modelId = "offerModelTest";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the offer model.
        String urlSegment = "offer/model/";
        JSONObject responseModel = serverApiService.addMessageToHwWalletObject(urlSegment, modelId, messagesStr);
        System.out.println("Updated offer model: " + JSONObject.toJSONString(responseModel));
    }
}
