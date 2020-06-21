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

package com.huawei.wallet.hms.hmssdk.flight;

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
 * Flight-model tests.
 *
 * @since 2019-12-12
 */
public class HwFlightModelTest {

    /**
     * Create a new flight model.
     * Each flight model indicates a style of flight instances. The body of a flight model is HwWalletObject, which is
     * the same as a flight instance. Some parameters of all flight instances with the same style are the same. These
     * common parameters are stored in their flight model.
     * POST http://XXX/hmspass/v1/flight/model
     */
    @Test
    public void createFlightModel() {
        System.out.println("createFlightModel begin");

        // Read an example flight model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("FlightModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the flight model you want to create. Flight instances belong to this model share these
        // parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new flight model to the wallet server.
            String urlParameter = "flight/model";
            HwWalletObject flightModel = walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("createFlightModel JSON is: " + CommonUtil.toJson(flightModel));
            System.out.println("createFlightModel end");
        }
    }

    /**
     * Overwrite a flight model.
     * Run the "createFlightModel" test before running this test.
     * PUT http://xxx/hmspass/v1/flight/model/{modelId}
     */
    @Test
    public void fullUpdateFlightModel() {
        System.out.println("fullUpdateFlightModel begin.");

        // ID of the flight model you want to update.
        String modelId = "flightModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding flight model will be replaced by this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateFlightModel.json");

        // Update the flight model.
        String urlParameter = "flight/model/";
        WalletBuildService flightBuildService = new WalletBuildServiceImpl();
        HwWalletObject flightModelUpdated = flightBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(flightModelUpdated.getPassStyleIdentifier()));
        System.out.println("flightModelUpdated JSON is: " + CommonUtil.toJson(flightModelUpdated));
        System.out.println("fullUpdateFlightModel end.");
    }

    /**
     * Update a flight model.
     * Run the "createFlightModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/flight/model/{modelId}
     */
    @Test
    public void partialUpdateFlightModel() {
        System.out.println("partialUpdateFlightModel begin.");

        // ID of the flight model you want to update.
        String modelId = "flightModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding flight model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateFlightModel.json");

        // Update the flight model.
        String urlParameter = "flight/model/";
        WalletBuildService flightBuildService = new WalletBuildServiceImpl();
        HwWalletObject flightModelUpdated =
            flightBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(flightModelUpdated.getPassStyleIdentifier()));
        System.out.println("flightModelUpdated JSON is: " + CommonUtil.toJson(flightModelUpdated));
        System.out.println("partialUpdateFlightModel end.");
    }

    /**
     * Get a flight model by its model ID.
     * Run the "createFlightModel" test before running this test.
     * GET http://xxx/hmspass/v1/flight/model/{modelId}
     */
    @Test
    public void getFlightModel() {
        System.out.println("getFlightModel begin.");

        // ID of the flight model you want to get.
        String modelId = "FlightModelTest";

        // Get the flight model.
        String urlParameter = "flight/model/";
        HwWalletObject flightModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("flightModel JSON is: " + CommonUtil.toJson(flightModel));
        System.out.println("getFlightModel end.");
    }

    /**
     * Get some flight models created by the developer who is sending this request.
     * Run the "createFlightModel" test before running this test.
     * GET http://xxx/hmspass/v1/flight/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getFlightModelList() {
        System.out.println("getFlightModelList begin.");

        // Get a list of flight models.
        String urlParameter = "flight/model";
        List<HwWalletObject> flightModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("flightModels JSON is: " + CommonUtil.toJson(flightModels));
        System.out.println("getFlightModelList end.");
    }

    /**
     * Add messages to a flight model.
     * Run the "createFlightModel" test before running this test.
     * POST http://xxx/hmspass/v1/flight/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToFlightModel() {
        System.out.println("addMessageToFlightModel begin.");

        // ID of the flight model you want to update.
        String modelId = "flightModelTest";

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the flight model.
        String urlParameter = "flight/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject flightModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(flightModelUpdated.getPassStyleIdentifier()));
        System.out.println("flightModelUpdated JSON is: " + CommonUtil.toJson(flightModelUpdated));
        System.out.println("addMessageToFlightModel end.");
    }
}
