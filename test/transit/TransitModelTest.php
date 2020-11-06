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

final class TransitModelTest extends PHPUnit\Framework\TestCase
{
    /**
     * Create a new transit model.
     * Each transit model indicates a style of transit passes..
     * POST http://XXX/hmspass/v1/transit/model
     *
     * @test
     */
    public function createTransitModel()
    {
        echo "createTransitModel begin.\n";

        // Read a transit model from a JSON file.
        $model = json_decode(file_get_contents("../config/data/TransitModel.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateModel($model);

        // Post the new transit model to HMS wallet server.
        $urlSegment = "transit/model";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->postToWalletServer($urlSegment, json_encode($model));
        echo "Posted transit model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get a transit model by its model ID.
     * Run the "createTransitModel" test before running this test.
     * GET http://xxx/hmspass/v1/transit/model/{modelId}
     *
     * @test
     */
    public function getTransitModel()
    {
        echo "getTransitModel begin.\n";

        // ID of the transit model you want to get.
        $modelId = "TransitTestModel";

        // Post the new transit model to HMS wallet server.
        $urlSegment = "transit/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->getHwWalletObjectById($urlSegment, $modelId);
        echo "Corresponding transit model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get transit models belonging to a specific appId.
     * Run the "createTransitModel" test before running this test.
     * GET http://xxx/hmspass/v1/transit/model?session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getTransitModelList()
    {
        echo "getTransitModelList begin.\n";

        // Get a list of transit models.
        $urlSegment = "transit/model";
        $serverApiService = new ServerApiService();
        $serverApiService->getModels($urlSegment, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite a transit model.
     * Run the "createTransitModel" test before running this test.
     * PUT http://xxx/hmspass/v1/transit/model/{modelId}
     *
     * @test
     */
    public function fullUpdateTransitModel()
    {
        echo "fullUpdateTransitModel begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        $model = json_decode(file_get_contents("../config/data/FullUpdateTransitModel.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateModel($model);

        // Update the transit model.
        $urlSegment = "transit/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->fullUpdateHwWalletObject($urlSegment, $model["passStyleIdentifier"],
            json_encode($model));
        echo "Updated transit model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Update a transit model.
     * Run the "createTransitModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/transit/model/{modelId}
     *
     * @test
     */
    public function partialUpdateTransitModel() {
        echo "partialUpdateTransitModel begin.\n";

        // ID of the transit model you want to update.
        $modelId = "TransitTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        $modelStr = file_get_contents("../config/data/PartialUpdateTransitModel.json");

        // Update the transit model.
        $urlSegment = "transit/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->partialUpdateHwWalletObject($urlSegment, $modelId, $modelStr);
        echo "Updated transit model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Add messages to a transit model.
     * Run the "createTransitModel" test before running this test.
     * POST http://xxx/hmspass/v1/transit/model/{modelId}/addMessage
     *
     * @test
     */
    public function addMessageToTransitModel() {
        echo "addMessageToTransitModel begin.\n";

        // ID of the transit model you want to update.
        $modelId = "TransitTestModel";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the transit model.
        $urlSegment = "transit/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->addMessageToHwWalletObject($urlSegment, $modelId, $messagesStr);
        echo "Updated transit model: " . json_encode($responseModel);

        static::assertTrue(true);
    }
}