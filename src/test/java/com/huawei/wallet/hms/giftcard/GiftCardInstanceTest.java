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

package com.huawei.wallet.hms.giftcard;

import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

/**
 * Gift card instance tests.
 *
 * @since 2019-12-12
 */
public class GiftCardInstanceTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add a gift card instance to HMS wallet server.
     * Run the "createGiftCardModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/giftcard/instance
     */
    @Test
    public void addGiftCardInstance() {
        System.out.println("addGiftCardInstance begin.");

        // Read a gift card instance from a JSON file.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("GiftCardInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Post the new gift card instance to HMS wallet server.
        String urlSegment = "giftcard/instance";
        JSONObject responseInstance =
            serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(instance));
        System.out.println("Posted gift card instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get a gift card instance by its instance ID.
     * Run the "createGiftCardInstance" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/instance/{instanceId}
     */
    @Test
    public void getGiftCardInstance() {
        System.out.println("getGiftCardInstance begin.");

        // ID of the gift card instance you want to get.
        String instanceId = "30001";

        // Get the gift card instance.
        String urlSegment = "giftcard/instance/";
        JSONObject responseInstance = serverApiService.getHwWalletObjectById(urlSegment, instanceId);
        System.out.println("Corresponding gift card instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get gift card instance belonging to a specific gift card model.
     * Run the "createGiftCardInstance" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getGiftCardInstanceList() {
        System.out.println("getGiftCardInstanceList begin.");

        // Model ID of gift card instances you want to get.
        String modelId = "giftCardModelTest";

        // Get a list of gift card instances.
        String urlSegment = "giftcard/instance";
        JSONArray instances = serverApiService.getInstances(urlSegment, modelId, 5);
        System.out.println("Total instances count: " + instances.size());
        System.out.println("Instances list: " + instances.toJSONString());
    }

    /**
     * Overwrite a gift card instance.
     * Run the "createGiftCardInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/giftcard/instance/{instanceId}
     */
    @Test
    public void fullUpdateGiftCardInstance() {
        System.out.println("fullUpdateGiftCardInstance begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateGiftCardInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Update the gift card instance.
        String urlSegment = "giftcard/instance/";
        JSONObject responseInstance = serverApiService.fullUpdateHwWalletObject(urlSegment,
            instance.getString("serialNumber"), JSONObject.toJSONString(instance));
        System.out.println("Updated gift card instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update a gift card instance.
     * Run the "createGiftCardInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/giftcard/instance/{instanceId}
     */
    @Test
    public void partialUpdateGiftCardInstance() {
        System.out.println("partialUpdateGiftCardInstance begin.");

        // ID of the gift card instance you want to update.
        String instanceId = "30001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        String instanceStr = ConfigUtil.readFile("PartialUpdateGiftCardInstance.json");

        // Update the gift card instance.
        String urlSegment = "giftcard/instance/";
        JSONObject responseInstance = serverApiService.partialUpdateHwWalletObject(urlSegment, instanceId, instanceStr);
        System.out.println("Updated gift card instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Add messages to a gift card instance.
     * Run the "createGiftCardInstance" test before running this test.
     * POST http://xxx/hmspass/v1/giftcard/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToGiftCardInstance() {
        System.out.println("addMessageToGiftCardInstance begin.");

        // ID of the gift card instance you want to update.
        String instanceId = "30001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the gift card instance.
        String urlSegment = "giftcard/instance/";
        JSONObject responseInstance = serverApiService.addMessageToHwWalletObject(urlSegment, instanceId, messagesStr);
        System.out.println("Updated gift card instance: " + JSONObject.toJSONString(responseInstance));
    }
}
