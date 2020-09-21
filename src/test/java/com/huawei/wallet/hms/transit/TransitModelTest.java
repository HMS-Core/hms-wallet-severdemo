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

package com.huawei.wallet.hms.transit;

import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

/**
 * Transit model tests.
 *
 * @since 2019-12-12
 */
public class TransitModelTest {
    private ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Create a new transit model.
     * Each transit model indicates a style of transit passes.
     * POST http://XXX/hmspass/v1/transit/model
     */
    @Test
    public void createTransitModel() {
        System.out.println("createTransitModel begin.");

        // Read a transit model from a JSON file.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("TransitModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Post the new transit model to HMS wallet server.
        String urlSegment = "transit/model";
        JSONObject responseModel = serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(model));
        System.out.println("Posted transit model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get a transit model by its model ID.
     * Run the "createTransitModel" test before running this test.
     * GET http://xxx/hmspass/v1/transit/model/{modelId}
     */
    @Test
    public void getTransitModel() {
        System.out.println("getTransitModel begin.");

        // ID of the transit model you want to get.
        String modelId = "TransitModelTest";

        // Get the transit model.
        String urlSegment = "transit/model/";
        JSONObject responseModel = serverApiService.getHwWalletObjectById(urlSegment, modelId);
        System.out.println("Corresponding transit model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get transit model belonging to a specific appId.
     * Run the "createTransitModel" test before running this test.
     * GET http://xxx/hmspass/v1/transit/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getTransitModelList() {
        System.out.println("getTransitModelList begin.");

        // Get a list of transit models.
        String urlSegment = "transit/model";

        JSONArray models = serverApiService.getModels(urlSegment, 5);
        System.out.println("Total models count: " + models.size());
        System.out.println("Models list: " + models.toJSONString());
    }

    /**
     * Overwrite a transit model.
     * Run the "createTransitModel" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/model/{modelId}
     */
    @Test
    public void fullUpdateTransitModel() {
        System.out.println("fullUpdateTransitModel begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateTransitModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Update the transit model.
        String urlSegment = "transit/model/";
        JSONObject responseModel = serverApiService.fullUpdateHwWalletObject(urlSegment,
            model.getString("passStyleIdentifier"), JSONObject.toJSONString(model));
        System.out.println("Updated transit model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Update a transit model.
     * Run the "createTransitModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/transit/model/{modelId}
     */
    @Test
    public void partialUpdateTransitModel() {
        System.out.println("partialUpdateTransitModel begin.");

        // ID of the transit model you want to update.
        String modelId = "transitModelTest";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        String modelStr = ConfigUtil.readFile("PartialUpdateTransitModel.json");

        // Update the transit model.
        String urlSegment = "transit/model/";
        JSONObject responseModel = serverApiService.partialUpdateHwWalletObject(urlSegment, modelId, modelStr);
        System.out.println("Updated transit model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Add messages to a transit model.
     * Run the "createTransitModel" test before running this test.
     * POST http://xxx/hmspass/v1/transit/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToTransitModel() {
        System.out.println("addMessageToTransitModel begin.");

        // ID of the transit model you want to update.
        String modelId = "transitModelTest";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the transit model.
        String urlSegment = "transit/model/addMessage";
        JSONObject responseModel = serverApiService.addMessageToHwWalletObject(urlSegment, modelId, messagesStr);
        System.out.println("Updated transit model: " + JSONObject.toJSONString(responseModel));
    }
}
