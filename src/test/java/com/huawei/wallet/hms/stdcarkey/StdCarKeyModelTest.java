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

package com.huawei.wallet.hms.stdcarkey;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;
import org.junit.Test;

/**
 * Std car key model tests.
 *
 * @since 2021-05-27
 */
public class StdCarKeyModelTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Create a new std car key model.
     * Each std car key model indicates a style of std car key passes.
     * POST http://XXX/hmspass/v2/key_stdcar/model
     */
    @Test
    public void createStdCarKeyModel() {
        System.out.println("createStdCarKeyModel begin.");

        // Read a std car key model from a JSON file.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("StdCarKeyModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Post the new std car key model to HMS wallet server.
        String urlSegment = "/v2/key_stdcar/model";
        JSONObject responseModel = serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(model));
        System.out.println("Posted std car key model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get a std car key model by its model ID.
     * Run the "createStdCarKeyModel" test before running this test.
     * GET http://xxx/hmspass/v2/key_stdcar/model/{modelId}
     */
    @Test
    public void getStdCarKeyModel() {
        System.out.println("getStdCarKeyModel begin.");

        // ID of the std car key model you want to get.
        String modelId = "StdCarKeyTestModel";

        // Get the std car key model.
        String urlSegment = "/v2/key_stdcar/model/";
        JSONObject responseModel = serverApiService.getHwWalletObjectById(urlSegment, modelId);
        System.out.println("Corresponding std car key model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Get std car key models belonging to a specific appId.
     * Run the "createStdCarKeyModel" test before running this test.
     * GET http://xxx/hmspass/v2/key_stdcar/model?session=XXX&pageSize=XXX
     */
    @Test
    public void getStdCarKeyModelList() {
        System.out.println("getStdCarKeyModelList begin.");

        // Get a list of std car key models.
        String urlSegment = "/v2/key_stdcar/model";

        JSONArray models = serverApiService.getModels(urlSegment, 5);
        System.out.println("Total models count: " + models.size());
        System.out.println("Models list: " + models.toJSONString());
    }

    /**
     * Overwrite a std car key model.
     * Run the "createStdCarKeyModel" test before running this test.
     * PUT http://xxx/hmspass/v2/key_stdcar/model/{modelId}
     */
    @Test
    public void fullUpdateStdCarKeyModel() {
        System.out.println("fullUpdateStdCarKeyModel begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
        JSONObject model = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateStdCarKeyModel.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateModel(model);

        // Update the std car key model.
        String urlSegment = "/v2/key_stdcar/model/";
        JSONObject responseModel = serverApiService.fullUpdateHwWalletObject(urlSegment,
            model.getString("passStyleIdentifier"), JSONObject.toJSONString(model));
        System.out.println("Updated std car key model: " + JSONObject.toJSONString(responseModel));
    }

    /**
     * Update a std car key model.
     * Run the "createStdCarKeyModel" test before running this test.
     * PATCH http://xxx/hmspass/v2/key_stdcar/model/{modelId}
     */
    @Test
    public void partialUpdateStdCarKeyModel() {
        System.out.println("partialUpdateStdCarKeyModel begin.");

        // ID of the std car key model you want to update.
        String modelId = "StdCarKeyTestModel";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
        String modelStr = ConfigUtil.readFile("PartialUpdateStdCarKeyModel.json");

        // Update the std car key model.
        String urlSegment = "/v2/key_stdcar/model/";
        JSONObject responseModel = serverApiService.partialUpdateHwWalletObject(urlSegment, modelId, modelStr);
        System.out.println("Updated std car key model: " + JSONObject.toJSONString(responseModel));
    }
}
