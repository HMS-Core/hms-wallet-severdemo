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

package com.huawei.wallet.hms.hmssdk.eventticket;

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
 * Event-ticket-instance tests.
 *
 * @since 2019-12-12
 */
public class HwEventTicketInstanceTest {

    /**
     * Create an event ticket instance.
     * Run the "createEventTicketModel" test before running this test.
     * POST http://XXX/hmspass/v1/eventticket/instance
     */
    @Test
    public void createEventTicketInstance() {
        System.out.println("createEventTicketInstance begin");

        // Read an example event ticket instance from a JSON file.
        String objectJson = CommonUtil.readWalletObject("EventTicketInstance.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);

        // Every event ticket instance has a style, which is an event ticket model. This model ID indicates which model
        // the new event ticket instance belongs to. Before creating an event ticket instance, its associated event
        // ticket model should already exist.
        String modelId = hwWalletObject.getPassStyleIdentifier();

        // Set the ID of the new event ticket instance.
        String instanceId = hwWalletObject.getSerialNumber();
        System.out.println("instanceId is: " + instanceId);

        WalletBuildService walletBuildService = new WalletBuildServiceImpl();

        // Set the event ticket instance's parameters.
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateInstance = HwWalletObjectUtil.validateWalletInstance(requestData);
        if (validateInstance) {
            // Post requestData to the wallet server to create a new event ticket instance.
            String urlParameter = "eventticket/instance";
            HwWalletObject eventTicketInstance =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("eventTicketInstance JSON is: " + CommonUtil.toJson(eventTicketInstance));
            System.out.println("createEventTicketInstance end");
        }
    }

    /**
     * Get an event ticket instance by its instance ID.
     * Run the "createEventTicketInstance" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void getEventTicketInstance() {
        System.out.println("getEventTicketInstance begin.");

        // ID of the event ticket instance you want to get.
        String instanceId = "10001";

        // This is used to construct http URL.
        String postHwEventTicketInstance = "eventticket/instance/";

        // Get the event ticket instance.
        HwWalletObject eventTicketInstance =
            new WalletBuildServiceImpl().getWalletInstanceByInstanceId(postHwEventTicketInstance, instanceId);
        System.out.println("eventTicketInstance JSON is: " + CommonUtil.toJson(eventTicketInstance));
        System.out.println("getEventTicketInstance end.");
    }

    /**
     * Get some event ticket instances belongs to a specific event ticket model.
     * Run the "createEventTicketInstance" test before running this test.
     * GET http://xxx/hmspass/v1/eventticket/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getEventTicketInstanceList() {
        System.out.println("getEventTicketInstanceList begin.");

        // Model ID of event ticket instances you want to get.
        String modelId = "eventTicketModelTest";

        // Get a list of the event ticket instances.
        String urlParameter = "eventticket/instance";
        List<HwWalletObject> eventTicketInstances =
            new WalletBuildServiceImpl().listWalletInstance(urlParameter, modelId, 5);
        System.out.println("eventTicketInstances JSON is: " + CommonUtil.toJson(eventTicketInstances));
        System.out.println("getEventTicketInstanceList end.");
    }

    /**
     * Overwrite an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void fullUpdateEventTicketInstance() {
        System.out.println("fullUpdateEventTicketInstance begin.");

        // ID of the event ticket instance to be updated.
        String instanceId = "10001";

        // Read an example HwWalletObject from a JSON file. The corresponding event ticket instance will be replaced by
        // this HwWalletObject.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        String instanceJson = CommonUtil.readWalletObject("FullUpdateEventTicketInstance.json");
        String modelId = JSONObject.parseObject(instanceJson, HwWalletObject.class).getPassStyleIdentifier();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, modelId);

        // Update the event ticket instance.
        String urlParameter = "eventticket/instance/";
        HwWalletObject eventTicketInstanceUpdated =
            walletBuildService.fullUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(eventTicketInstanceUpdated.getSerialNumber()));
        System.out.println("eventTicketInstanceUpdated JSON is: " + CommonUtil.toJson(eventTicketInstanceUpdated));
        System.out.println("fullUpdateEventTicketInstance end.");
    }

    /**
     * Update an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/eventticket/instance/{instanceId}
     */
    @Test
    public void partialUpdateEventTicketInstance() {
        System.out.println("partialUpdateEventTicketInstance begin.");

        // ID of the event ticket instance to be updated.
        String instanceId = "10001";

        // Read an example HwWalletObject from a JSON file. The corresponding event ticket model will merge with this
        // HwWalletObject.
        String instanceJson = CommonUtil.readWalletObject("PartialUpdateEventTicketInstance.json");
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, null);

        // Update the event ticket instance.
        String urlParameter = "eventticket/instance/";
        HwWalletObject eventTicketInstanceUpdated =
            walletBuildService.partialUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(eventTicketInstanceUpdated.getSerialNumber()));
        System.out.println("eventTicketInstanceUpdated JSON is: " + CommonUtil.toJson(eventTicketInstanceUpdated));
        System.out.println("partialUpdateEventTicketInstance end.");
    }

    /**
     * Add messages to an event ticket instance.
     * Run the "createEventTicketInstance" test before running this test.
     * POST http://xxx/hmspass/v1/eventticket/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToEventTicketInstance() {
        System.out.println("addMessageToEventTicketInstance begin.");

        // ID of the event ticket instance to be updated.
        String instanceId = "10001";

        // Create a list of messages you want to add to a wallet instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet instance contains at most 10 messages. If a wallet instance
        // already have 10 messages and you keep adding new messages, the oldest messages will be removed. You should
        // not add more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the event ticket instance.
        String urlParameter = "eventticket/instance/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject eventTicketInstanceUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, instanceId, messagesJson);
        assertThat(instanceId, equalTo(eventTicketInstanceUpdated.getSerialNumber()));
        System.out.println("eventTicketInstanceUpdated JSON is: " + CommonUtil.toJson(eventTicketInstanceUpdated));
        System.out.println("addMessageToEventTicketInstance end.");
    }

    /**
     * Generate Thin JWEs. These JWEs are used to bind event ticket instances to users.
     */
    @Test
    public void generateThinJWEToBindUser() {
        System.out.println("generateThinJWEToBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Bind existing event ticket instances to users.
        // Construct a list of event-ticket-instance IDs to be bound.
        String instanceIdListJson = "{\"instanceIds\": [\"10001\"]}";
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
     * Generate JWEs. These JWEs are used to add passes and bind event ticket instances to users.
     */
    @Test
    public void generateJWEToAddPassAndBindUser() {
        System.out.println("generateJWEToAddPassAndBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Generate a new event ticket instance and bind it to a user.
        String newInstanceJson = CommonUtil.readWalletObject("EventTicketInstance.json");
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
