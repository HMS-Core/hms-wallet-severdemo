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

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huawei.wallet.hms.hmssdk.dto.HwWalletObject;
import com.huawei.wallet.hms.hmssdk.WalletBuildService;
import com.huawei.wallet.util.HwWalletObjectUtil;
import com.huawei.wallet.util.JweUtil;
import com.huawei.wallet.util.RandomUtils;
import org.junit.Test;

import com.huawei.wallet.hms.hmssdk.impl.WalletBuildServiceImpl;
import com.huawei.wallet.util.CommonUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Transit-instance tests.
 *
 * @since 2019-12-12
 */
public class HwTransitInstanceTest {

    /**
     * Create a transit instance.
     * Run the "createTransitModel" test before running this test.
     * POST http://XXX/hmspass/v1/transit/instance
     */
    @Test
    public void createTransitInstance() {
        System.out.println("createTransitInstance begin");

        // Read an example transit instance fro a JSON file.
        String objectJson = CommonUtil.readWalletObject("TransitInstance.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);

        // Every transit instance has a style, which is a transit model. This model ID indicates which model the new
        // loyalty transit belongs to. Before creating a transit instance, its associated transit model should already
        // exist.
        String modelId = hwWalletObject.getPassStyleIdentifier();

        // Set the ID of the new transit instance.
        String instanceId = hwWalletObject.getSerialNumber();
        System.out.println("instanceId is: " + instanceId);

        WalletBuildService walletBuildService = new WalletBuildServiceImpl();

        // Set the transit instance's parameters.
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateInstance = HwWalletObjectUtil.validateWalletInstance(requestData);
        if (validateInstance) {
            // Post requestData to the wallet server to create a new transit instance.
            String urlParameter = "transit/instance";
            HwWalletObject transitInstance =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("transitInstance JSON is: " + CommonUtil.toJson(transitInstance));
            System.out.println("createTransitInstance end");
        }
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
        String instanceId = "60001";

        // This is used to construct http URL.
        String postHwTransitInstance = "transit/instance/";

        // Get the transit instance.
        HwWalletObject transitInstance =
            new WalletBuildServiceImpl().getWalletInstanceByInstanceId(postHwTransitInstance, instanceId);
        System.out.println("transitInstance JSON is: " + CommonUtil.toJson(transitInstance));
        System.out.println("getTransitInstance end.");
    }

    /**
     * Get some transit instance belongs to a specific transit model.
     * Run the "createTransitInstance" test before running this test.
     * GET http://xxx/hmspass/v1/transit/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getTransitInstanceList() {
        System.out.println("getTransitInstanceList begin.");

        // Model ID of offer instances you want to get.
        String modelId = "transitModelTest";

        // Get a list of the transit instances.
        String urlParameter = "transit/instance";
        List<HwWalletObject> transitInstances =
            new WalletBuildServiceImpl().listWalletInstance(urlParameter, modelId, 5);
        System.out.println("transitInstances JSON is: " + CommonUtil.toJson(transitInstances));
        System.out.println("getTransitInstanceList end.");
    }

    /**
     * Overwrite a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/instance/{instanceId}
     */
    @Test
    public void fullUpdateTransitInstance() {
        System.out.println("fullUpdateTransitInstance begin.");

        // ID of the transit instance to be updated.
        String instanceId = "60001";

        // Read an example HwWalletObject from a JSON file. The corresponding transit instance will be replaced by this
        // HwWalletObject.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        String instanceJson = CommonUtil.readWalletObject("FullUpdateTransitInstance.json");
        String modelId = JSONObject.parseObject(instanceJson, HwWalletObject.class).getPassStyleIdentifier();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, modelId);

        // Update the transit instance.
        String urlParameter = "transit/instance/";
        HwWalletObject transitInstanceUpdated =
            walletBuildService.fullUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(transitInstanceUpdated.getSerialNumber()));
        System.out.println("transitInstanceUpdated JSON is: " + CommonUtil.toJson(transitInstanceUpdated));
        System.out.println("fullUpdateTransitInstance end.");
    }

    /**
     * Update a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/transit/instance/{instanceId}
     */
    @Test
    public void partialUpdateTransitInstance() {
        System.out.println("partialUpdateTransitInstance begin.");

        // ID of the transit instance to be updated.
        String instanceId = "60001";

        // Read an example HwWalletObject from a JSON file. The corresponding transit model will merge with this
        // HwWalletObject.
        String instanceJson = CommonUtil.readWalletObject("PartialUpdateTransitInstance.json");
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, null);

        // Update the transit instance.
        String urlParameter = "transit/instance/";
        HwWalletObject transitInstanceUpdated =
            walletBuildService.partialUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(transitInstanceUpdated.getSerialNumber()));
        System.out.println("transitInstanceUpdated JSON is: " + CommonUtil.toJson(transitInstanceUpdated));
        System.out.println("partialUpdateTransitInstance end.");
    }

    /**
     * Add messages to a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * POST http://xxx/hmspass/v1/transit/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToTransitInstance() {
        System.out.println("addMessageToTransitInstance begin.");

        // ID of the transit instance to be updated.
        String instanceId = "60001";

        // Create a list of messages you want to add to a wallet instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet instance contains at most 10 messages. If a wallet instance
        // already have 10 messages and you keep adding new messages, the oldest messages will be removed. You should
        // not add more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the transit instance.
        String urlParameter = "transit/instance/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject transitInstanceUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, instanceId, messagesJson);
        assertThat(instanceId, equalTo(transitInstanceUpdated.getSerialNumber()));
        System.out.println("transitInstanceUpdated JSON is: " + CommonUtil.toJson(transitInstanceUpdated));
        System.out.println("addMessageToTransitInstance end.");
    }

    /**
     * Generate Thin JWEs. These JWEs are used to bind transit instances to users.
     */
    @Test
    public void generateThinJWEToBindUser() {
        System.out.println("generateThinJWEToBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Bind existing transit instances to users.
        // Construct a list of transit-instance IDs to be bound.
        String instanceIdListJson = "{\"instanceIds\": [\"60001\"]}";
        JSONObject instanceIdListJsonObject = JSONObject.parseObject(instanceIdListJson);
        instanceIdListJsonObject.put("iss", appId);

        // Generate a session key to encrypt payload data. A session key is a string of random hex numbers.
        String sessionKey = RandomUtils.generateSecureRandomFactor(16);
        System.out.println("sessionKey: " + sessionKey);

        // Huawei's fixed public key to encrypt session key.
        String sessionKeyPublicKey =
            "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE=";

        System.out.println("sessionKeyPublicKey: " + sessionKeyPublicKey);

        // You generated a pair of keys while applying for services on AGC. Use that private key here.
        String jweSignPrivateKey = "Replace with your private key.";

        // Generate JWEs.
        String jweStrByInstanceIds = JweUtil.generateJwe(sessionKey, jweSignPrivateKey,
            instanceIdListJsonObject.toJSONString(), sessionKeyPublicKey);
        System.out.println("JWE String: " + jweStrByInstanceIds);
    }

    /**
     * Generate JWEs. These JWEs are used to bind transit instances to users.
     */
    @Test
    public void generateJWEToAddPassAndBindUser() {
        System.out.println("generateJWEToAddPassAndBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Generate a new transit instance and bind it to a user.
        String newInstanceJson = CommonUtil.readWalletObject("TransitInstance.json");
        JSONObject newInstanceJsonObject = JSONObject.parseObject(newInstanceJson);
        newInstanceJsonObject.put("iss", appId);

        // Generate a session key to encrypt payload data. A session key is a string of random hex numbers.
        String sessionKey = RandomUtils.generateSecureRandomFactor(16);
        System.out.println("sessionKey: " + sessionKey);

        // Huawei's fixed public key to encrypt session key.
        String sessionKeyPublicKey =
            "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE=";

        System.out.println("sessionKeyPublicKey: " + sessionKeyPublicKey);

        // You generated a pair of keys while applying for services on AGC. Use that private key here.
        String jweSignPrivateKey = "Replace with your private key.";

        // Generate JWEs.
        String jweStrByInstanceIds = JweUtil.generateJwe(sessionKey, jweSignPrivateKey,
            newInstanceJsonObject.toJSONString(), sessionKeyPublicKey);
        System.out.println("JWE String: " + jweStrByInstanceIds);
    }
}
