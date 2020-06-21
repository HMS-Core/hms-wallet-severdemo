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

package com.huawei.wallet.hms.hmssdk.giftcard;

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
 * Gift-card-model tests.
 *
 * @since 2019-12-12
 */
public class HwGiftCardModelTest {

    /**
     * Create a new gift card model.
     * Each gift card model indicates a style of gift card instances. The body of a gift card model is HwWalletObject,
     * which is the same as a gift card instance. Some parameters of all gift card instances with the same style are the
     * same. These common parameters are stored in their gift card model.
     * POST http://XXX/hmspass/v1/giftcard/model
     */
    @Test
    public void createGiftCardModel() {
        System.out.println("createGiftCardModel begin");

        // Read an example gift card model from a JSON file.
        String objectJson = CommonUtil.readWalletObject("GiftCardModel.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);
        String modelId = hwWalletObject.getPassStyleIdentifier();
        System.out.println("modelId is: " + modelId);
        String instanceId = hwWalletObject.getSerialNumber();

        // Set parameters of the gift card model you want to create. Gift card instances belong to this model share
        // these parameters.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateModel = HwWalletObjectUtil.validateWalletModel(requestData);
        if (validateModel) {
            // Post the new gift card model to the wallet server.
            String urlParameter = "giftcard/model";
            HwWalletObject giftCardClass =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("createGiftCardModel JSON is: " + CommonUtil.toJson(giftCardClass));
            System.out.println("createGiftCardModel end");
        }
    }

    /**
     * Overwrite a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * PUT http://xxx/hmspass/v1/giftcard/model/{modelId}
     */
    @Test
    public void fullUpdateGiftCardModel() {
        System.out.println("fullUpdateGiftCardModel begin.");

        // ID of the gift card model you want to update.
        String modelId = "giftCardModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding gift card model will be replaced by this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("FullUpdateGiftCardModel.json");

        // Update the giftCard model.
        String urlParameter = "giftcard/model/";
        WalletBuildService giftCardBuildService = new WalletBuildServiceImpl();
        HwWalletObject giftCardModelUpdated =
            giftCardBuildService.fullUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(giftCardModelUpdated.getPassStyleIdentifier()));
        System.out.println("giftCardModelUpdated JSON is: " + CommonUtil.toJson(giftCardModelUpdated));
        System.out.println("fullUpdateGiftCardModel end.");
    }

    /**
     * Update a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/giftcard/model/{modelId}
     */
    @Test
    public void partialUpdateGiftCardModel() {
        System.out.println("partialUpdateGiftCardModel begin.");

        // ID of the gift card model you want to update.
        String modelId = "giftCardModelTest";

        // Read an example HwWalletObject from a JSON file. The corresponding gift card model will merge with this
        // HwWalletObject.
        String modelJson = CommonUtil.readWalletObject("PartialUpdateGiftCardModel.json");

        // Update the giftCard model.
        String urlParameter = "giftcard/model/";
        WalletBuildService giftCardBuildService = new WalletBuildServiceImpl();
        HwWalletObject giftCardModelUpdated =
            giftCardBuildService.partialUpdateWalletModel(urlParameter, modelId, modelJson);
        assertThat(modelId, equalTo(giftCardModelUpdated.getPassStyleIdentifier()));
        System.out.println("giftCardModelUpdated JSON is: " + CommonUtil.toJson(giftCardModelUpdated));
        System.out.println("partialUpdateGiftCardModel end.");
    }

    /**
     * Get a gift card model by its model ID.
     * Run the "createGiftCardModel" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/model/{modelId}
     */
    @Test
    public void getGiftCardModel() {
        System.out.println("getGiftCardModel begin.");

        // ID of the gift card model you want to get.
        String modelId = "GiftCardModelTest";

        // Get the gift card model.
        String urlParameter = "giftcard/model/";
        HwWalletObject giftCardModel = new WalletBuildServiceImpl().getWalletModelByModelId(urlParameter, modelId);
        System.out.println("giftCardModel JSON is: " + CommonUtil.toJson(giftCardModel));
        System.out.println("getGiftCardModel end.");
    }

    /**
     * Get some gift card models created by the developer who is sending this request.
     * Run the "createGiftCardModel" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getGiftCardModelList() {
        System.out.println("getGiftCardModelList begin.");

        // Get a list of gift card models.
        String urlParameter = "giftcard/model";
        List<HwWalletObject> giftCardModels = new WalletBuildServiceImpl().listWalletModel(urlParameter, 4);
        System.out.println("giftCardModels JSON is: " + CommonUtil.toJson(giftCardModels));
        System.out.println("getGiftCardModelList end.");
    }

    /**
     * Add messages to a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * POST http://xxx/hmspass/v1/giftcard/model/{modelId}/addMessage
     */
    @Test
    public void addMessageToGiftCardModel() {
        System.out.println("addMessageToGiftCardModel begin.");

        // ID of the gift card model you want to update.
        String modelId = "giftCardModelTest";

        // Create a list of messages you want to add to a wallet model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet model contains at most 10 messages. If a wallet model already
        // have 10 messages and you keep adding new messages, the oldest messages will be removed. You should not add
        // more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the gift card model.
        String urlParameter = "giftcard/model/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject giftCardModelUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, modelId, messagesJson);
        assertThat(modelId, equalTo(giftCardModelUpdated.getPassStyleIdentifier()));
        System.out.println("giftCardModelUpdated JSON is: " + CommonUtil.toJson(giftCardModelUpdated));
        System.out.println("addMessageToGiftCardModel end.");
    }
}
