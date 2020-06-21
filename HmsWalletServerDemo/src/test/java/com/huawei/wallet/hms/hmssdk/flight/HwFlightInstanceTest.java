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
 * Flight-instance tests.
 *
 * @since 2019-12-12
 */
public class HwFlightInstanceTest {

    /**
     * Create a flight instance.
     * Run the "createFlightModel" test before running this test.
     * POST http://XXX/hmspass/v1/flight/instance
     */
    @Test
    public void createFlightInstance() {
        System.out.println("createFlightInstance begin");

        // Read an example flight instance from a JSON file.
        String objectJson = CommonUtil.readWalletObject("FlightInstance.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);

        // Every flight instance has a style, which is a flight model. This model ID indicates which model the new
        // flight instance belongs to. Before creating a flight instance, its associated flight model should already
        // exist.
        String modelId = hwWalletObject.getPassStyleIdentifier();

        // Set the ID of the new flight instance.
        String instanceId = hwWalletObject.getSerialNumber();
        System.out.println("instanceId is: " + instanceId);

        WalletBuildService walletBuildService = new WalletBuildServiceImpl();

        // Set the flight instance's parameters.
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateInstance = HwWalletObjectUtil.validateWalletInstance(requestData);
        if (validateInstance) {
            // Post requestData to the wallet server to create a new flight instance.
            String urlParameter = "flight/instance";
            HwWalletObject flightInstance =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("flightInstance JSON is: " + CommonUtil.toJson(flightInstance));
            System.out.println("createFlightInstance end");
        }
    }

    /**
     * Get a flight instance by its instance ID.
     * Run the "createFlightInstance" test before running this test.
     * GET http://xxx/hmspass/v1/flight/instance/{instanceId}
     */
    @Test
    public void getFlightInstance() {
        System.out.println("getFlightInstance begin.");

        // ID of the flight instance you want to get.
        String instanceId = "20001";

        // This is used to construct http URL.
        String postHwFlightInstance = "flight/instance/";

        // Get the flight instance.
        HwWalletObject flightInstance =
            new WalletBuildServiceImpl().getWalletInstanceByInstanceId(postHwFlightInstance, instanceId);
        System.out.println("flightInstance JSON is: " + CommonUtil.toJson(flightInstance));
        System.out.println("getFlightInstance end.");
    }

    /**
     * Get some flight instance belongs to a specific flight model.
     * Run the "createFlightInstance" test before running this test.
     * GET http://xxx/hmspass/v1/flight/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getFlightInstanceList() {
        System.out.println("getFlightInstanceList begin.");

        // Model ID of flight instances you want to get.
        String modelId = "flightModelTest";

        // Get a list of the flight instances.
        String urlParameter = "flight/instance";
        List<HwWalletObject> flightInstances =
            new WalletBuildServiceImpl().listWalletInstance(urlParameter, modelId, 5);
        System.out.println("flightInstances JSON is: " + CommonUtil.toJson(flightInstances));
        System.out.println("getFlightInstanceList end.");
    }

    /**
     * Overwrite a flight instance.
     * Run the "createFlightInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/flight/instance/{instanceId}
     */
    @Test
    public void fullUpdateFlightInstance() {
        System.out.println("fullUpdateFlightInstance begin.");

        // ID of the flight instance to be updated.
        String instanceId = "20001";

        // Read an example HwWalletObject from a JSON file. The corresponding flight instance will be replaced by this
        // HwWalletObject.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        String instanceJson = CommonUtil.readWalletObject("FullUpdateFlightInstance.json");
        String modelId = JSONObject.parseObject(instanceJson, HwWalletObject.class).getPassStyleIdentifier();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, modelId);

        // Update the flight instance.
        String urlParameter = "flight/instance/";
        HwWalletObject flightInstanceUpdated =
            walletBuildService.fullUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(flightInstanceUpdated.getSerialNumber()));
        System.out.println("flightInstanceUpdated JSON is: " + CommonUtil.toJson(flightInstanceUpdated));
        System.out.println("fullUpdateFlightInstance end.");
    }

    /**
     * Update a flight instance.
     * Run the "createFlightInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/flight/instance/{instanceId}
     */
    @Test
    public void partialUpdateFlightInstance() {
        System.out.println("partialUpdateFlightInstance begin.");

        // ID of the flight instance to be updated.
        String instanceId = "20001";

        // Read an example HwWalletObject from a JSON file. The corresponding flight model will merge with this
        // HwWalletObject.
        String instanceJson = CommonUtil.readWalletObject("PartialUpdateFlightInstance.json");
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, null);

        // Update the flight instance.
        String urlParameter = "flight/instance/";
        HwWalletObject flightInstanceUpdated =
            walletBuildService.partialUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(flightInstanceUpdated.getSerialNumber()));
        System.out.println("flightInstanceUpdated JSON is: " + CommonUtil.toJson(flightInstanceUpdated));
        System.out.println("partialUpdateFlightInstance end.");
    }

    /**
     * Add messages to a flight instance.
     * Run the "createFlightInstance" test before running this test.
     * POST http://xxx/hmspass/v1/flight/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToFlightInstance() {
        System.out.println("addMessageToFlightInstance begin.");

        // ID of the flight instance to be updated.
        String instanceId = "20001";

        // Create a list of messages you want to add to a wallet instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet instance contains at most 10 messages. If a wallet instance
        // already have 10 messages and you keep adding new messages, the oldest messages will be removed. You should
        // not add more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the flight instance.
        String urlParameter = "flight/instance/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject flightInstanceUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, instanceId, messagesJson);
        assertThat(instanceId, equalTo(flightInstanceUpdated.getSerialNumber()));
        System.out.println("flightInstanceUpdated JSON is: " + CommonUtil.toJson(flightInstanceUpdated));
        System.out.println("addMessageToFlightInstance end.");
    }

    /**
     * Generate Thin JWEs. These JWEs are used to bind flight instances to users.
     */
    @Test
    public void generateThinJWEToBindUser() {
        System.out.println("generateThinJWEToBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Bind existing flight instances to users.
        // Construct a list of flight-instance IDs to be bound.
        String instanceIdListJson = "{\"instanceIds\": [\"20001\"]}";
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
     * Generate JWEs. These JWEs are used to bind flight instances to users.
     */
    @Test
    public void generateJWEToAddPassAndBindUser() {
        System.out.println("generateJWEToAddPassAndBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Generate a new flight instance and bind it to a user.
        String newInstanceJson = CommonUtil.readWalletObject("FlightInstance.json");
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
