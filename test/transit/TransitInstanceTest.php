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

final class TransitInstanceTest extends PHPUnit\Framework\TestCase
{
    /**
     * Add a transit instance to HMS wallet server.
     * Run the "createTransitModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/transit/instance
     *
     * @test
     */
    public function addTransitInstance()
    {
        echo "addTransitInstance begin.\n";

        // Read a transit instance from a JSON file.
        $instance = json_decode(file_get_contents("../config/data/TransitInstance.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateInstance($instance);

        // Post the new transit model to HMS wallet server.
        $urlSegment = "transit/instance";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->postToWalletServer($urlSegment, json_encode($instance));
        echo "Posted transit instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Get a transit instance by its instance ID.
     * Run the "createTransitInstance" test before running this test.
     * GET http://xxx/hmspass/v1/transit/instance/{instanceId}
     *
     * @test
     */
    public function getTransitInstance()
    {
        echo "getTransitInstance begin.\n";

        // ID of the transit instance you want to get.
        $instanceId = "TransitPass60001";

        // Post the new transit model to HMS wallet server.
        $urlSegment = "transit/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->getHwWalletObjectById($urlSegment, $instanceId);
        echo "Corresponding transit instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Get transit instances belonging to a specific transit model.
     * Run the "createTransitInstance" test before running this test.
     * GET http://xxx/hmspass/v1/transit/instance?modelId=XXX&session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getTransitInstanceList()
    {
        echo "getTransitInstanceList begin.\n";

        // Model ID of transit instances you want to get.
        $modelId = "TransitTestModel";

        // Get a list of transit models.
        $urlSegment = "transit/instance";
        $serverApiService = new ServerApiService();
        $serverApiService->getInstances($urlSegment, $modelId, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/instance/{instanceId}
     *
     * @test
     */
    public function fullUpdateTransitInstance()
    {
        echo "fullUpdateTransitInstance begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        $instance = json_decode(file_get_contents("../config/data/FullUpdateTransitInstance.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateInstance($instance);

        // Update the transit instance.
        $urlSegment = "transit/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->fullUpdateHwWalletObject($urlSegment, $instance["serialNumber"],
            json_encode($instance));
        echo "Updated transit model: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Update a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/transit/instance/{instanceId}
     *
     * @test
     */
    public function partialUpdateTransitInstance()
    {
        echo "partialUpdateTransitInstance begin.\n";

        // ID of the transit instance you want to update.
        $instanceId = "TransitPass60001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        $instanceStr = file_get_contents("../config/data/PartialUpdateTransitInstance.json");

        // Update the transit instance.
        $urlSegment = "transit/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->partialUpdateHwWalletObject($urlSegment, $instanceId, $instanceStr);
        echo "Updated transit instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }

    /**
     * Add messages to a transit instance.
     * Run the "createTransitInstance" test before running this test.
     * POST http://xxx/hmspass/v1/transit/instance/{instanceId}/addMessage
     *
     * @test
     */
    public function addMessageToTransitInstance()
    {
        echo "addMessageToTransitInstance begin.\n";

        // ID of the transit instance you want to update.
        $instanceId = "TransitPass60001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the transit instance.
        $urlSegment = "transit/instance/";
        $serverApiService = new ServerApiService();
        $responseInstance = $serverApiService->addMessageToHwWalletObject($urlSegment, $instanceId, $messagesStr);
        echo "Updated transit instance: " . json_encode($responseInstance);

        static::assertTrue(true);
    }
}