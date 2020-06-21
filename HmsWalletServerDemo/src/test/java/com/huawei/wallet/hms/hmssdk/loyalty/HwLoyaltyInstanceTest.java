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
 * Loyalty-instance tests.
 *
 * @since 2019-12-12
 */
public class HwLoyaltyInstanceTest {

    /**
     * Create a loyalty instance.
     * Run the "createLoyaltyModel" test before running this test.
     * POST http://XXX/hmspass/v1/loyalty/instance
     */
    @Test
    public void createLoyaltyInstance() {
        System.out.println("createLoyaltyInstance begin");

        // Read an example loyalty instance from a JSON file.
        String objectJson = CommonUtil.readWalletObject("LoyaltyInstance.json");
        HwWalletObject hwWalletObject = JSONObject.parseObject(objectJson, HwWalletObject.class);

        // Every loyalty instance has a style, which is a loyalty model. This model ID indicates which model the new
        // loyalty instance belongs to. Before creating a loyalty instance, its associated loyalty model should already
        // exist.
        String modelId = hwWalletObject.getPassStyleIdentifier();

        // Set the ID of the new loyalty instance.
        String instanceId = hwWalletObject.getSerialNumber();
        System.out.println("instanceId is: " + instanceId);

        WalletBuildService walletBuildService = new WalletBuildServiceImpl();

        // Set the loyalty instance's parameters.
        HwWalletObject requestData = walletBuildService.createHwWalletObject(objectJson, instanceId, modelId);

        // Validate the parameters.
        boolean validateInstance = HwWalletObjectUtil.validateWalletInstance(requestData);
        if (validateInstance) {
            // Post requestData to the wallet server to create a new loyalty instance.
            String urlParameter = "loyalty/instance";
            HwWalletObject loyaltyInstance =
                walletBuildService.postHwWalletObjectToWalletServer(urlParameter, requestData);
            System.out.println("loyaltyInstance JSON is: " + CommonUtil.toJson(loyaltyInstance));
            System.out.println("createLoyaltyInstance end");
        }
    }

    /**
     * Get a loyalty instance by its instance ID.
     * Run the "createLoyaltyInstance" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void getLoyaltyInstance() {
        System.out.println("getLoyaltyInstance begin.");

        // ID of the loyalty instance you want to get.
        String instanceId = "40001";

        // This is used to construct http URL.
        String postHwLoyaltyInstance = "loyalty/instance/";

        // Get the loyalty instance.
        HwWalletObject loyaltyInstance =
            new WalletBuildServiceImpl().getWalletInstanceByInstanceId(postHwLoyaltyInstance, instanceId);
        System.out.println("loyaltyInstance JSON is: " + CommonUtil.toJson(loyaltyInstance));
        System.out.println("getLoyaltyInstance end.");
    }

    /**
     * Get some loyalty instance belongs to a specific loyalty model.
     * Run the "createLoyaltyInstance" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getLoyaltyInstanceList() {
        System.out.println("getLoyaltyInstanceList begin.");

        // Model ID of loyalty instances you want to get.
        String modelId = "loyaltyModelTest";

        // Get a list of the loyalty instances.
        String urlParameter = "loyalty/instance";
        List<HwWalletObject> loyaltyInstances =
            new WalletBuildServiceImpl().listWalletInstance(urlParameter, modelId, 5);
        System.out.println("loyaltyInstances JSON is: " + CommonUtil.toJson(loyaltyInstances));
        System.out.println("getLoyaltyInstanceList end.");
    }

    /**
     * Overwrite a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void fullUpdateLoyaltyInstance() {
        System.out.println("fullUpdateLoyaltyInstance begin.");

        // ID of the loyalty instance to be updated.
        String instanceId = "40001";

        // Read an example HwWalletObject from a JSON file. The corresponding loyalty instance will be replaced by this
        // HwWalletObject.
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        String instanceJson = CommonUtil.readWalletObject("FullUpdateLoyaltyInstance.json");
        String modelId = JSONObject.parseObject(instanceJson, HwWalletObject.class).getPassStyleIdentifier();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, modelId);

        // Update the loyalty instance.
        String urlParameter = "loyalty/instance/";
        HwWalletObject loyaltyInstanceUpdated =
            walletBuildService.fullUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(loyaltyInstanceUpdated.getSerialNumber()));
        System.out.println("loyaltyInstanceUpdated JSON is: " + CommonUtil.toJson(loyaltyInstanceUpdated));
        System.out.println("fullUpdateLoyaltyInstance end.");
    }

    /**
     * Update a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void partialUpdateLoyaltyInstance() {
        System.out.println("partialUpdateLoyaltyInstance begin.");

        // ID of the loyalty instance to be updated.
        String instanceId = "40001";

        // Read an example HwWalletObject from a JSON file. The corresponding loyalty model will merge with this
        // HwWalletObject.
        String instanceJson = CommonUtil.readWalletObject("PartialUpdateLoyaltyInstance.json");
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject hwWalletInstanceNew = walletBuildService.createHwWalletObject(instanceJson, instanceId, null);

        // Update the loyalty instance.
        String urlParameter = "loyalty/instance/";
        HwWalletObject loyaltyInstanceUpdated =
            walletBuildService.partialUpdateWalletInstance(urlParameter, instanceId, hwWalletInstanceNew);
        assertThat(instanceId, equalTo(loyaltyInstanceUpdated.getSerialNumber()));
        System.out.println("loyaltyInstanceUpdated JSON is: " + CommonUtil.toJson(loyaltyInstanceUpdated));
        System.out.println("partialUpdateLoyaltyInstance end.");
    }

    /**
     * Add messages to a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * POST http://xxx/hmspass/v1/loyalty/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToLoyaltyInstance() {
        System.out.println("addMessageToLoyaltyInstance begin.");

        // ID of the loyalty instance to be updated.
        String instanceId = "40001";

        // Create a list of messages you want to add to a wallet instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One wallet instance contains at most 10 messages. If a wallet instance
        // already have 10 messages and you keep adding new messages, the oldest messages will be removed. You should
        // not add more than 10 messages at a time.

        // Read an example Messages from a JSON file.
        String messagesJson = CommonUtil.readWalletObject("Messages.json");

        // Add messages to the loyalty instance.
        String urlParameter = "loyalty/instance/addMessage";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();
        HwWalletObject loyaltyInstanceUpdated =
            walletBuildService.addMessageToHwWalletObject(urlParameter, instanceId, messagesJson);
        assertThat(instanceId, equalTo(loyaltyInstanceUpdated.getSerialNumber()));
        System.out.println("loyaltyInstanceUpdated JSON is: " + CommonUtil.toJson(loyaltyInstanceUpdated));
        System.out.println("addMessageToLoyaltyInstance end.");
    }

    /**
     * Update linked offers of a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}/linkedoffers
     */
    @Test
    public void updateLinkedOffersToLoyaltyInstance() {
        System.out.println("updateLinkedOffersToLoyaltyInstance begin.");

        // ID of the loyalty instance to be updated.
        String instanceId = "40001";
        WalletBuildService walletBuildService = new WalletBuildServiceImpl();

        // Create two lists of linked offer instances, one for offers you want to link to a loyalty instance and the
        // other one for offers you want to remove from it. Each linked offer object has two parameters,
        // passTypeIdentifier and instanceId, indicating which offer instance you want to add or remove. The adding list
        // and the removing list should not contain same offer instances (with the same modelId and the same
        // instanceId). You should make sure the offer instances you want to link already exist before using this
        // interface.

        // Read an example LinkedOfferInstanceIds from a JSON file.
        String linkedOfferInstanceIdsJson = CommonUtil.readWalletObject("LinkedOfferInstanceIds.json");

        // Update relatedPassIds in the loyalty instance.
        String urlParameter = "loyalty/instance/linkedoffers";
        HwWalletObject loyaltyInstanceUpdated = walletBuildService.updateLinkedOffersToLoyaltyInstance(urlParameter,
            instanceId, linkedOfferInstanceIdsJson);
        assertThat(instanceId, equalTo(loyaltyInstanceUpdated.getSerialNumber()));
        System.out.println("loyaltyInstanceUpdated JSON is: " + CommonUtil.toJson(loyaltyInstanceUpdated));
        System.out.println("updateLinkedOffersToLoyaltyInstance end.");
    }

    /**
     * Generate Thin JWEs. These JWEs are used to bind loyalty instances to users.
     */
    @Test
    public void generateThinJWEToBindUser() {
        System.out.println("generateThinJWEToBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Bind existing loyalty instances to users.
        // Construct a list of loyalty-instance IDs to be bound.
        String instanceIdListJson = "{\"instanceIds\": [\"40001\"]}";
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
     * Generate JWEs. These JWEs are used to bind loyalty instances to users.
     */
    @Test
    public void generateJWEToAddPassAndBindUser() {
        System.out.println("generateJWEToAddPassAndBindUser begin.");

        // The app ID registered on the Huawei AppGallery Connect website.
        String appId = "Replace with your app ID";

        // Generate a new loyalty instance and bind it to a user.
        String newInstanceJson = CommonUtil.readWalletObject("LoyaltyInstance.json");
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
