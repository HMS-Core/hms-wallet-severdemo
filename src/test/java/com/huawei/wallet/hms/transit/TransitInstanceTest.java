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
 * Transit instance tests.
 *
 * @since 2019-12-12
 */
public class TransitInstanceTest {
    private ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add a transit instance to HMS wallet server.
     * Run the "createTransitModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/transit/instance
     */
    @Test
    public void addTransitInstance() {
        System.out.println("addTransitInstance begin.");

        // Read a transit instance from a JSON file.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("TransitInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Post the new transit instance to HMS wallet server.
        String urlSegment = "/v1/transit/instance";
        JSONObject responseInstance =
            serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(instance));
        System.out.println("Posted transit instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get a transit instance by its instance ID.
     * Run the "createTransitInstance" test before running this test.
     * GET http://xxx/hmspass/v1/transit/instance/{instanceId}
     */
    @Test
    public void getTransitInstance() {
        System.out.println("getTransitInstance begin.");

        // ID of the transit instance you want to get.
        String instanceId = "TransitPass60001";

        // Get the transit instance.
        String urlSegment = "/v1/transit/instance/";
        JSONObject responseInstance = serverApiService.getHwWalletObjectById(urlSegment, instanceId);
        System.out.println("Corresponding transit instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get transit instance belonging to a specific transit model.
     * Run the "createTransitInstance" test before running this test.
     * GET http://xxx/hmspass/v1/transit/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getTransitInstanceList() {
        System.out.println("getTransitInstanceList begin.");

        // Model ID of offer instances you want to get.
        String modelId = "TransitTestModel";

        // Get a list of transit instances.
        String urlSegment = "/v1/transit/instance";

        JSONArray instances = serverApiService.getInstances(urlSegment, modelId, 5);
        System.out.println("Total instances count: " + instances.size());
        System.out.println("Instances list: " + instances.toJSONString());
    }

    /**
     * Overwrite a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/instance/{instanceId}
     */
    @Test
    public void fullUpdateTransitInstance() {
        System.out.println("fullUpdateTransitInstance begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateTransitInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Update the transit instance.
        String urlSegment = "/v1/transit/instance/";
        JSONObject responseInstance = serverApiService.fullUpdateHwWalletObject(urlSegment,
            instance.getString("serialNumber"), JSONObject.toJSONString(instance));
        System.out.println("Updated transit instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/transit/instance/{instanceId}
     */
    @Test
    public void partialUpdateTransitInstance() {
        System.out.println("partialUpdateTransitInstance begin.");

        // ID of the transit instance to you want to update.
        String instanceId = "TransitPass60001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        String instanceStr = ConfigUtil.readFile("PartialUpdateTransitInstance.json");

        // Update the transit instance.
        String urlSegment = "/v1/transit/instance/";
        JSONObject responseInstance = serverApiService.partialUpdateHwWalletObject(urlSegment, instanceId, instanceStr);
        System.out.println("Updated transit instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Add messages to a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * POST http://xxx/hmspass/v1/transit/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToTransitInstance() {
        System.out.println("addMessageToTransitInstance begin.");

        // ID of the transit instance to you want to update.
        String instanceId = "TransitPass60001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the transit instance.
        String urlSegment = "/v1/transit/instance/";
        JSONObject responseInstance = serverApiService.addMessageToHwWalletObject(urlSegment, instanceId, messagesStr);
        System.out.println("Updated transit instance: " + JSONObject.toJSONString(responseInstance));
    }
}
