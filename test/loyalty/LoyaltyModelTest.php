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

final class LoyaltyModelTest extends PHPUnit\Framework\TestCase
{
    /**
     * Create a new loyalty model.
     * Each loyalty model indicates a style of loyalty passes..
     * POST http://XXX/hmspass/v1/loyalty/model
     *
     * @test
     */
    public function createLoyaltyModel()
    {
        echo "createLoyaltyModel begin.\n";

        // Read a loyalty model from a JSON file.
        $model = json_decode(file_get_contents("../config/data/LoyaltyModel.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateModel($model);

        // Post the new loyalty model to HMS wallet server.
        $urlSegment = "loyalty/model";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->postToWalletServer($urlSegment, json_encode($model));
        echo "Posted loyalty model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get a loyalty model by its model ID.
     * Run the "createLoyaltyModel" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/model/{modelId}
     *
     * @test
     */
    public function getLoyaltyModel()
    {
        echo "getLoyaltyModel begin.\n";

        // ID of the loyalty model you want to get.
        $modelId = "LoyaltyTestModel";

        // Post the new loyalty model to HMS wallet server.
        $urlSegment = "loyalty/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->getHwWalletObjectById($urlSegment, $modelId);
        echo "Corresponding loyalty model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get loyalty models belonging to a specific appId.
     * Run the "createLoyaltyModel" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/model?session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getLoyaltyModelList()
    {
        echo "getLoyaltyModelList begin.\n";

        // Get a list of loyalty models.
        $urlSegment = "loyalty/model";
        $serverApiService = new ServerApiService();
        $serverApiService->getModels($urlSegment, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * PUT http://xxx/hmspass/v1/loyalty/model/{modelId}
     *
     * @test
     */
    public function fullUpdateLoyaltyModel()
    {
        echo "fullUpdateLoyaltyModel begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        $model = json_decode(file_get_contents("../config/data/FullUpdateLoyaltyModel.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateModel($model);

        // Update the loyalty model.
        $urlSegment = "loyalty/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->fullUpdateHwWalletObject($urlSegment, $model["passStyleIdentifier"],
            json_encode($model));
        echo "Updated loyalty model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Update a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/model/{modelId}
     *
     * @test
     */
    public function partialUpdateLoyaltyModel() {
        echo "partialUpdateLoyaltyModel begin.\n";

        // ID of the loyalty model you want to update.
        $modelId = "LoyaltyTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        $modelStr = file_get_contents("../config/data/PartialUpdateLoyaltyModel.json");

        // Update the loyalty model.
        $urlSegment = "loyalty/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->partialUpdateHwWalletObject($urlSegment, $modelId, $modelStr);
        echo "Updated loyalty model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Add messages to a loyalty model.
     * Run the "createLoyaltyModel" test before running this test.
     * POST http://xxx/hmspass/v1/loyalty/model/{modelId}/addMessage
     *
     * @test
     */
    public function addMessageToLoyaltyModel() {
        echo "addMessageToLoyaltyModel begin.\n";

        // ID of the loyalty model you want to update.
        $modelId = "LoyaltyTestModel";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the loyalty model.
        $urlSegment = "loyalty/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->addMessageToHwWalletObject($urlSegment, $modelId, $messagesStr);
        echo "Updated loyalty model: " . json_encode($responseModel);

        static::assertTrue(true);
    }
}