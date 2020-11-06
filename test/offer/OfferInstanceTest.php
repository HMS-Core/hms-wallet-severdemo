<?php
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

require_once '../../main/hms/ServerApiService.php';
require_once '../../main/util/HwWalletObjectUtil.php';

final class OfferInstanceTest extends PHPUnit\Framework\TestCase
{
    /**
     * Add an offer instance to HMS wallet server.
     * Run the "createOfferModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/offer/instance
     *
     * @test
     */
    public function addOfferInstance()
    {
        echo "addOfferInstance begin.\n";

        // Read an offer instance from a JSON file.
        $instance = json_decode(file_get_contents("../config/data/OfferInstance.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateInstance($instance);

        // Post the new offer model to HMS wallet server.
        $urlSegment = "offer/instance";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->postToWalletServer($urlSegment, json_encode($instance));
        echo "Posted offer instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Get an offer instance by its instance ID.
     * Run the "createOfferInstance" test before running this test.
     * GET http://xxx/hmspass/v1/offer/instance/{instanceId}
     *
     * @test
     */
    public function getOfferInstance()
    {
        echo "getOfferInstance begin.\n";

        // ID of the offer instance you want to get.
        $instanceId = "OfferPass50001";

        // Post the new offer model to HMS wallet server.
        $urlSegment = "offer/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->getHwWalletObjectById($urlSegment, $instanceId);
        echo "Corresponding offer instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Get offer instances belonging to a specific offer model.
     * Run the "createOfferInstance" test before running this test.
     * GET http://xxx/hmspass/v1/offer/instance?modelId=XXX&session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getOfferInstanceList()
    {
        echo "getOfferInstanceList begin.\n";

        // Model ID of offer instances you want to get.
        $modelId = "OfferTestModel";

        // Get a list of offer models.
        $urlSegment = "offer/instance";
        $serverApiService = new ServerApiService();
        $serverApiService->getInstances($urlSegment, $modelId, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite an offer instance.
     * Run the "createOfferInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/offer/instance/{instanceId}
     *
     * @test
     */
    public function fullUpdateOfferInstance()
    {
        echo "fullUpdateOfferInstance begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        $instance = json_decode(file_get_contents("../config/data/FullUpdateOfferInstance.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateInstance($instance);

        // Update the offer instance.
        $urlSegment = "offer/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->fullUpdateHwWalletObject($urlSegment, $instance["serialNumber"],
            json_encode($instance));
        echo "Updated offer model: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Update an offer instance.
     * Run the "createOfferInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/offer/instance/{instanceId}
     *
     * @test
     */
    public function partialUpdateOfferInstance()
    {
        echo "partialUpdateOfferInstance begin.\n";

        // ID of the offer instance you want to update.
        $instanceId = "OfferPass50001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        $instanceStr = file_get_contents("../config/data/PartialUpdateOfferInstance.json");

        // Update the offer instance.
        $urlSegment = "offer/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->partialUpdateHwWalletObject($urlSegment, $instanceId, $instanceStr);
        echo "Updated offer instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Add messages to an offer instance.
     * Run the "createOfferInstance" test before running this test.
     * POST http://xxx/hmspass/v1/offer/instance/{instanceId}/addMessage
     *
     * @test
     */
    public function addMessageToOfferInstance()
    {
        echo "addMessageToOfferInstance begin.\n";

        // ID of the offer instance you want to update.
        $instanceId = "OfferPass50001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the offer instance.
        $urlSegment = "offer/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->addMessageToHwWalletObject($urlSegment, $instanceId, $messagesStr);
        echo "Updated offer instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }
}