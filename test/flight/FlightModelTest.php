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

final class FlightModelTest extends PHPUnit\Framework\TestCase
{
    /**
     * Create a new flight model.
     * Each flight model indicates a style of flight passes..
     * POST http://XXX/hmspass/v1/flight/model
     *
     * @test
     */
    public function createFlightModel()
    {
        echo "createFlightModel begin.\n";

        // Read a flight model from a JSON file.
        $model = json_decode(file_get_contents("../config/data/FlightModel.json"), true);

        // Validate parameters.
        HwWalletObjectUtil::validateModel($model);

        // Post the new flight model to HMS wallet server.
        $urlSegment = "flight/model";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->postToWalletServer($urlSegment, json_encode($model));
        echo "Posted flight model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get a flight model by its model ID.
     * Run the "createFlightModel" test before running this test.
     * GET http://xxx/hmspass/v1/flight/model/{modelId}
     *
     * @test
     */
    public function getFlightModel()
    {
        echo "getFlightModel begin.\n";

        // ID of the flight model you want to get.
        $modelId = "FlightTestModel";

        // Post the new flight model to HMS wallet server.
        $urlSegment = "flight/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->getHwWalletObjectById($urlSegment, $modelId);
        echo "Corresponding flight model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Get flight models belonging to a specific appId.
     * Run the "createFlightModel" test before running this test.
     * GET http://xxx/hmspass/v1/flight/model?session=XXX&pageSize=XXX
     *
     * @test
     */
    public function getFlightModelList()
    {
        echo "getFlightModelList begin.\n";

        // Get a list of flight models.
        $urlSegment = "flight/model";
        $serverApiService = new ServerApiService();
        $serverApiService->getModels($urlSegment, 5);

        static::assertTrue(true);
    }

    /**
     * Overwrite a flight model.
     * Run the "createFlightModel" test before running this test.
     * PUT http://xxx/hmspass/v1/flight/model/{modelId}
     *
     * @test
     */
    public function fullUpdateFlightModel()
    {
        echo "fullUpdateFlightModel begin.\n";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        $model = json_decode(file_get_contents("../config/data/FullUpdateFlightModel.json"), true);

        // Validate parameters.
        $hwWalletObjectUtil = new HwWalletObjectUtil();
        $hwWalletObjectUtil->validateModel($model);

        // Update the flight model.
        $urlSegment = "flight/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->fullUpdateHwWalletObject($urlSegment, $model["passStyleIdentifier"],
            json_encode($model));
        echo "Updated flight model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Update a flight model.
     * Run the "createFlightModel" test before running this test.
     * PATCH http://xxx/hmspass/v1/flight/model/{modelId}
     *
     * @test
     */
    public function partialUpdateFlightModel() {
        echo "partialUpdateFlightModel begin.\n";

        // ID of the flight model you want to update.
        $modelId = "FlightTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        $modelStr = file_get_contents("../config/data/PartialUpdateFlightModel.json");

        // Update the flight model.
        $urlSegment = "flight/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->partialUpdateHwWalletObject($urlSegment, $modelId, $modelStr);
        echo "Updated flight model: " . json_encode($responseModel);

        static::assertTrue(true);
    }

    /**
     * Add messages to a flight model.
     * Run the "createFlightModel" test before running this test.
     * POST http://xxx/hmspass/v1/flight/model/{modelId}/addMessage
     *
     * @test
     */
    public function addMessageToFlightModel() {
        echo "addMessageToFlightModel begin.\n";

        // ID of the flight model you want to update.
        $modelId = "FlightTestModel";

        // Create a list of messages you want to add to a model. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
        // and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
        // messages at a time.

        // Read messages from a JSON file.
        $messagesStr = file_get_contents("../config/data/Messages.json");

        // Add messages to the flight model.
        $urlSegment = "flight/model/";
        $serverApiService = new ServerApiService();
        $responseModel = $serverApiService->addMessageToHwWalletObject($urlSegment, $modelId, $messagesStr);
        echo "Updated flight model: " . json_encode($responseModel);

        static::assertTrue(true);
    }
}