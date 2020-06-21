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

package com.huawei.wallet.hms.hmssdk.loyalty;

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
 * Loyalty-model tests.
 *
 * @since 2019-12-12
 */
public class HwLoyaltyModelTest {

    /**
     * Create a new loyalty model.
     * Each loyalty model indicates a style of loyalty instances. The body of a loyalty model is HwWalletObject, which
     * is the same as a loyalty instance. Some parameters of all loyalty instances with the same style are the same.
     * These common parameters are stored in their loyalty model.
     * POST http://XXX/hmspass/v1/loyalty/model
     */
    @Test
    public void createLoyaltyModel() {
        System.out.println("createLoyaltyModel begin");

        // Read an example loyalty model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("LoyaltyModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the loyalty model you want to create. Loyalty instances belong to this model share these
        // parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new loyalty model to the wallet server.
            String urlParameter = "loyalty/model";
            HwWalletObject loyaltyModel =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("createLoyaltyModel JSON is: " + CommonUtil.toJson(loyaltyModel));
            System.out.println("createLoyaltyModel end");
        }
    }

    /**
     * Overwrite a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * PUT http://xxx/hmspass/v1/loyalty/model/{modelId}
     */
    @Test
    public void fullUpdateLoyaltyModel() {
        System.out.println("fullUpdateLoyaltyModel begin.");

        // ID of the loyalty model you want to update.
        String modelId = "loyaltyModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding loyalty model will be replaced by this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateLoyaltyModel.json");

        // Update the loyalty model.
        String urlParameter = "loyalty/model/";
        WalletBuildService loyaltyBuildService = new WalletBuildServiceImpl();
        HwWalletObject loyaltyModelUpdated =
            loyaltyBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(loyaltyModelUpdated.getPassStyleIdentifier()));
        System.out.println("loyaltyModelUpdated JSON is: " + CommonUtil.toJson(loyaltyModelUpdated));
        System.out.println("fullUpdateLoyaltyModel end.");
    }

    /**
     * Update a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/model/{modelId}
     */
    @Test
    public void partialUpdateLoyaltyModel() {
        System.out.println("partialUpdateLoyaltyModel begin.");

        // ID of the loyalty model you want to update.
        String modelId = "loyaltyModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding loyalty model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateLoyaltyModel.json");

        // Update the loyalty model.
        String urlParameter = "loyalty/model/";
        WalletBuildService loyaltyBuildService = new WalletBuildServiceImpl();
        HwWalletObject loyaltyModelUpdated =
            loyaltyBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(loyaltyModelUpdated.getPassStyleIdentifier()));
        System.out.println("loyaltyModelUpdated JSON is: " + CommonUtil.toJson(loyaltyModelUpdated));
        System.out.println("partialUpdateLoyaltyModel end.");
    }

    /**
     * Get a loyalty model by its model ID.
     * Run the "createLoyaltyModel" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/model/{modelId}
     */
    @Test
    public void getLoyaltyModel() {
        System.out.println("getLoyaltyModel begin.");

        // ID of the loyalty model you want to get.
        String modelId = "LoyaltyModelTest";

        // Get the loyalty model.
        String urlParameter = "loyalty/model/";
        HwWalletObject loyaltyModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("loyaltyModel JSON is: " + CommonUtil.toJson(loyaltyModel));
        System.out.println("getLoyaltyModel end.");
    }

    /**
     * Get some loyalty models created by the developer who is sending this request.
     * Run the "createLoyaltyModel" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getLoyaltyModelList() {
        System.out.println("getLoyaltyModelList begin.");

        // Get a list of loyalty models.
        String urlParameter = "loyalty/model";
        List<HwWalletObject> loyaltyModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("loyaltyModels JSON is: " + CommonUtil.toJson(loyaltyModels));
        System.out.println("getLoyaltyModelList end.");
    }

    /**
     * Add messages to a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * POST http://xxx/hmspass/v1/loyalty/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToLoyaltyModel() {
        System.out.println("addMessageToLoyaltyModel begin.");

        // ID of the loyalty model you want to update.
        String modelId = "exampleclass4";

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the loyalty model.
        String urlParameter = "loyalty/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject loyaltyModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(loyaltyModelUpdated.getPassStyleIdentifier()));
        System.out.println("loyaltyModelUpdated JSON is: " + CommonUtil.toJson(loyaltyModelUpdated));
        System.out.println("addMessageToLoyaltyModel end.");
    }
}
