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

package com.huawei.wallet.hms.hmssdk.transit;

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
 * Transit-model tests.
 *
 * @since 2019-12-12
 */
public class HwTransitModelTest {

    /**
     * Create a new transit model.
     * Each transit model indicates a style of transit instances. The body of a transit model is HwWalletObject, which
     * is the same as a transit instance. Some parameters of all transit instance with the same style are the same.
     * These common parameters are stored in their transit model.
     * POST http://XXX/hmspass/v1/transit/model
     */
    @Test
    public void createTransitModel() {
        System.out.println("createTransitModel begin");

        // Read an example transit model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("TransitModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the transit model you want to create. Transit instance belong to this class share these
        // parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new transit model to the wallet server.
            String urlParameter = "transit/model";
            HwWalletObject transitModel =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("createTransitModel JSON is: " + CommonUtil.toJson(transitModel));
            System.out.println("createTransitModel end");
        }
    }

    /**
     * Overwrite a transit model.
     * Run the "createTransitModel" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/model/{modelId}
     */
    @Test
    public void fullUpdateTransitModel() {
        System.out.println("fullUpdateTransitModel begin.");

        // ID of the transit model you want to update.
        String modelId = "transitModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding transit model will be replaced by this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateTransitModel.json");

        // Update the transit model.
        String urlParameter = "transit/model/";
        WalletBuildService transitBuildService = new WalletBuildServiceImpl();
        HwWalletObject transitModelUpdated =
            transitBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(transitModelUpdated.getPassStyleIdentifier()));
        System.out.println("transitModelUpdated JSON is: " + CommonUtil.toJson(transitModelUpdated));
        System.out.println("fullUpdateTransitModel end.");
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

        // Read an example HwWalletObject from a JSON file. The corresponding transit model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateTransitModel.json");

        // Update the transit model.
        String urlParameter = "transit/model/";
        WalletBuildService transitBuildService = new WalletBuildServiceImpl();
        HwWalletObject transitModelUpdated =
            transitBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(transitModelUpdated.getPassStyleIdentifier()));
        System.out.println("transitModelUpdated JSON is: " + CommonUtil.toJson(transitModelUpdated));
        System.out.println("partialUpdateTransitModel end.");
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
        String urlParameter = "transit/model/";
        HwWalletObject transitModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("transitModel JSON is: " + CommonUtil.toJson(transitModel));
        System.out.println("getTransitModel end.");
    }

    /**
     * Get some transit model created by the developer who is sending this request.
     * Run the "createTransitModel" test before running this test.
     * GET http://xxx/hmspass/v1/transit/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getTransitModelList() {
        System.out.println("getTransitModelList begin.");

        // Get a list of transit models.
        String urlParameter = "transit/model";
        List<HwWalletObject> transitModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("transitModels JSON is: " + CommonUtil.toJson(transitModels));
        System.out.println("getTransitModelList end.");
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

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the transit model.
        String urlParameter = "transit/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject transitModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(transitModelUpdated.getPassStyleIdentifier()));
        System.out.println("transitModelUpdated JSON is: " + CommonUtil.toJson(transitModelUpdated));
        System.out.println("addMessageToTransitModel end.");
    }
}
