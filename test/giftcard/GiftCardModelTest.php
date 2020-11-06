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

final class GiftCardModelTest extends PHPUnit\Framework\TestCase
{
    /**
     * Create a new gift card model.
     * Each gift card model indicates a style of gift card passes..
     * POST http://XXX/hmspass/v1/giftcard/model
     *
     * @test
     */
    public function createGiftCardModel()
    {
        echo "createGiftCardModel begin.\n";

        // Read a gift card model from a JSON file.
        $model = json_decode(file_get_contents("../config/data/GiftCardModel.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateModel($model);

        // Post the new gift card model to HMS wallet server.
        $urlSegment = "giftcard/model";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->postToWalletServer($urlSegment, json_encode($model));
        echo "Posted gift card model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get a gift card model by its model ID.
     * Run the "createGiftCardModel" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/model/{modelId}
     *
     * @test
     */
    public function getGiftCardModel()
    {
        echo "getGiftCardModel begin.\n";

        // ID of the gift card model you want to get.
        $modelId = "GiftCardTestModel";

        // Post the new gift card model to HMS wallet server.
        $urlSegment = "giftcard/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->getHwWalletObjectById($urlSegment, $modelId);
        echo "Corresponding gift card model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get gift card models belonging to a specific appId.
     * Run the "createGiftCardModel" test before running this test.
     * GET http://xxx/hmspass/v1/giftcard/model?session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getGiftCardModelList()
    {
        echo "getGiftCardModelList begin.\n";

        // Get a list of gift card models.
        $urlSegment = "giftcard/model";
        $serverApiService = new ServerApiService();
        $serverApiService->getModels($urlSegment, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * PUT http://xxx/hmspass/v1/giftcard/model/{modelId}
     *
     * @test
     */
    public function fullUpdateGiftCardModel()
    {
        echo "fullUpdateGiftCardModel begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        $model = json_decode(file_get_contents("../config/data/FullUpdateGiftCardModel.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateModel($model);

        // Update the gift card model.
        $urlSegment = "giftcard/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->fullUpdateHwWalletObject($urlSegment, $model["passStyleIdentifier"],
            json_encode($model));
        echo "Updated gift card model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Update a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/giftcard/model/{modelId}
     *
     * @test
     */
    public function partialUpdateGiftCardModel() {
        echo "partialUpdateGiftCardModel begin.\n";

        // ID of the gift card model you want to update.
        $modelId = "GiftCardTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        $modelStr = file_get_contents("../config/data/PartialUpdateGiftCardModel.json");

        // Update the gift card model.
        $urlSegment = "giftcard/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->partialUpdateHwWalletObject($urlSegment, $modelId, $modelStr);
        echo "Updated gift card model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Add messages to a gift card model.
     * Run the "createGiftCardModel" test before running this test.
     * POST http://xxx/hmspass/v1/giftcard/model/{modelId}/addMessage
     *
     * @test
     */
    public function addMessageToGiftCardModel() {
        echo "addMessageToGiftCardModel begin.\n";

        // ID of the gift card model you want to update.
        $modelId = "GiftCardTestModel";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the gift card model.
        $urlSegment = "giftcard/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->addMessageToHwWalletObject($urlSegment, $modelId, $messagesStr);
        echo "Updated gift card model: " . json_encode($responseModel);

        static::assertTrue(true);
    }
}