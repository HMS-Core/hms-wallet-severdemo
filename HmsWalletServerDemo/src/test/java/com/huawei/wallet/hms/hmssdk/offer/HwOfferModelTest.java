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

package com.huawei.wallet.hms.hmssdk.offer;

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
 * Offer-model tests.
 *
 * @since 2019-12-12
 */
public class HwOfferModelTest {

    /**
     * Create a new offer model.
     * Each offer model indicates a style of offer instances. The body of an offer model is HwWalletObject, which is the
     * same as an offer instance. Some parameters of all offer instances with the same style are the same. These common
     * parameters are stored in their offer model.
     * POST http://XXX/hmspass/v1/offer/model
     */
    @Test
    public void createOfferModel() {
        System.out.println("createOfferModel begin");

        // Read an example offer model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("OfferModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the offer model you want to create. Offer instances belong to this model share these
        // parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new offer model to the wallet server.
            String urlParameter = "offer/model";
            HwWalletObject offerModel = walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("createOfferModel JSON is: " + CommonUtil.toJson(offerModel));
            System.out.println("createOfferModel end");
        }
    }

    /**
     * Overwrite an offer model.
     * Run the "createOfferModel" test before running this test.
     * PUT http://xxx/hmspass/v1/offer/model/{modelId}
     */
    @Test
    public void fullUpdateOfferModel() {
        System.out.println("fullUpdateOfferModel begin.");

        // ID of the offer model you want to update.
        String modelId = "offerModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding offer model will be replaced by this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateOfferModel.json");

        // Update the offer model.
        String urlParameter = "offer/model/";
        WalletBuildService offerBuildService = new WalletBuildServiceImpl();
        HwWalletObject offerModelUpdated = offerBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(offerModelUpdated.getPassStyleIdentifier()));
        System.out.println("offerModelUpdated JSON is: " + CommonUtil.toJson(offerModelUpdated));
        System.out.println("fullUpdateOfferModel end.");
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

        // Read an example HwWalletObject from a JSON file. The corresponding offer model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateOfferModel.json");

        // Update the offer model.
        String urlParameter = "offer/model/";
        WalletBuildService offerBuildService = new WalletBuildServiceImpl();
        HwWalletObject offerModelUpdated = offerBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(offerModelUpdated.getPassStyleIdentifier()));
        System.out.println("offerModelUpdated JSON is: " + CommonUtil.toJson(offerModelUpdated));
        System.out.println("partialUpdateOfferModel end.");
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
        String urlParameter = "offer/model/";
        HwWalletObject offerModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("offerModel JSON is: " + CommonUtil.toJson(offerModel));
        System.out.println("getOfferModel end.");
    }

    /**
     * Get some offer models created by the developer who is sending this request.
     * Run the "createOfferModel" test before running this test.
     * GET http://xxx/hmspass/v1/offer/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getOfferModelList() {
        System.out.println("getOfferModelList begin.");

        // Get a list of offer models.
        String urlParameter = "offer/model";
        List<HwWalletObject> offerModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("offerModels JSON is: " + CommonUtil.toJson(offerModels));
        System.out.println("getOfferModelList end.");
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

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the offer model.
        String urlParameter = "offer/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject offerModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(offerModelUpdated.getPassStyleIdentifier()));
        System.out.println("offerModelUpdated JSON is: " + CommonUtil.toJson(offerModelUpdated));
        System.out.println("addMessageToOfferModel end.");
    }
}
