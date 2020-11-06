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

final class OfferModelTest extends PHPUnit\Framework\TestCase
{
    /**
     * Create a new offer model.
     * Each offer model indicates a style of offer passes..
     * POST http://XXX/hmspass/v1/offer/model
     *
     * @test
     */
    public function createOfferModel()
    {
        echo "createOfferModel begin.\n";

        // Read an offer model from a JSON file.
        $model = json_decode(file_get_contents("../config/data/OfferModel.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateModel($model);

        // Post the new offer model to HMS wallet server.
        $urlSegment = "offer/model";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->postToWalletServer($urlSegment, json_encode($model));
        echo "Posted offer model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get an offer model by its model ID.
     * Run the "createOfferModel" test before running this test.
     * GET http://xxx/hmspass/v1/offer/model/{modelId}
     *
     * @test
     */
    public function getOfferModel()
    {
        echo "getOfferModel begin.\n";

        // ID of the offer model you want to get.
        $modelId = "OfferTestModel";

        // Post the new offer model to HMS wallet server.
        $urlSegment = "offer/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->getHwWalletObjectById($urlSegment, $modelId);
        echo "Corresponding offer model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get offer models belonging to a specific appId.
     * Run the "createOfferModel" test before running this test.
     * GET http://xxx/hmspass/v1/offer/model?session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getOfferModelList()
    {
        echo "getOfferModelList begin.\n";

        // Get a list of offer models.
        $urlSegment = "offer/model";
        $serverApiService = new ServerApiService();
        $serverApiService->getModels($urlSegment, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite an offer model.
     * Run the "createOfferModel" test before running this test.
     * PUT http://xxx/hmspass/v1/offer/model/{modelId}
     *
     * @test
     */
    public function fullUpdateOfferModel()
    {
        echo "fullUpdateOfferModel begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        $model = json_decode(file_get_contents("../config/data/FullUpdateOfferModel.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateModel($model);

        // Update the offer model.
        $urlSegment = "offer/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->fullUpdateHwWalletObject($urlSegment, $model["passStyleIdentifier"],
            json_encode($model));
        echo "Updated offer model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Update an offer model.
     * Run the "createOfferModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/offer/model/{modelId}
     *
     * @test
     */
    public function partialUpdateOfferModel() {
        echo "partialUpdateOfferModel begin.\n";

        // ID of the offer model you want to update.
        $modelId = "OfferTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        $modelStr = file_get_contents("../config/data/PartialUpdateOfferModel.json");

        // Update the offer model.
        $urlSegment = "offer/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->partialUpdateHwWalletObject($urlSegment, $modelId, $modelStr);
        echo "Updated offer model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Add messages to an offer model.
     * Run the "createOfferModel" test before running this test.
     * POST http://xxx/hmspass/v1/offer/model/{modelId}/addMessage
     *
     * @test
     */
    public function addMessageToOfferModel() {
        echo "addMessageToOfferModel begin.\n";

        // ID of the offer model you want to update.
        $modelId = "OfferTestModel";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the offer model.
        $urlSegment = "offer/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->addMessageToHwWalletObject($urlSegment, $modelId, $messagesStr);
        echo "Updated offer model: " . json_encode($responseModel);

        static::assertTrue(true);
    }
}