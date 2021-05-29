/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
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
 * Std car key instance tests.
 *
 * @since 2021-05-27
 */
public class StdCarKeyInstanceTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add a std car key instance to HMS wallet server.
     * Run the "createStdCarKeyInstance" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v2/key_stdcar/instance
     */
    @Test
    public void addStdCarKeyInstance() {
        System.out.println("addStdCarKeyInstance begin.");

        // Read a std car key instance from a JSON file.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("StdCarKeyInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Post the new std car key instance to HMS wallet server.
        String urlSegment = "/v2/key_stdcar/instance";
        JSONObject responseInstance =
            serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(instance));
        System.out.println("Posted std car key instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get a std car key instance by its instance ID.
     * Run the "createStdCarKeyInstance" test before running this test.
     * GET http://xxx/hmspass/v2/key_stdcar/instance/{instanceId}
     */
    @Test
    public void getStdCarKeyInstance() {
        System.out.println("getStdCarKeyInstance begin.");

        // ID of the std car key instance you want to get.
        String instanceId = "StdCarKeyPass40001";

        // Get the std car key instance.
        String urlSegment = "/v2/key_stdcar/instance/";
        JSONObject responseInstance = serverApiService.getHwWalletObjectById(urlSegment, instanceId);
        System.out.println("Corresponding std car key instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get std car key instance belonging to a specific std car key model.
     * Run the "createStdCarKeyInstance" test before running this test.
     * GET http://xxx/hmspass/v2/key_stdcar/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getStdCarKeyInstanceList() {
        System.out.println("getStdCarKeyyInstanceList begin.");

        // Model ID of std car key instances you want to get.
        String modelId = "StdCarKeyTestModel";

        // Get a list of std car key instances.
        String urlSegment = "/v2/key_stdcar/instance";

        JSONArray instances = serverApiService.getInstances(urlSegment, modelId, 5);
        System.out.println("Total instances count: " + instances.size());
        System.out.println("Instances list: " + instances.toJSONString());
    }

    /**
     * Overwrite a std car key instance.
     * Run the "createStdCarKeyInstance" test before running this test.
     * PUT http://xxx/hmspass/v2/key_stdcar/instance/{instanceId}
     */
    @Test
    public void fullUpdateStdCarKeyInstance() {
        System.out.println("fullUpdateStdCarKeyInstance begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateStdCarKeyInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Update the std car key instance.
        String urlSegment = "/v2/key_stdcar/instance/";
        JSONObject responseInstance = serverApiService.fullUpdateHwWalletObject(urlSegment,
            instance.getString("serialNumber"), JSONObject.toJSONString(instance));
        System.out.println("Updated std car key instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update a std car key instance.
     * Run the "createStdCarKeyInstance" test before running this test.
     * PATCH http://xxx/hmspass/v2/key_stdcar/instance/{instanceId}
     */
    @Test
    public void partialUpdateStdCarKeyInstance() {
        System.out.println("partialUpdateStdCarKeyInstance begin.");

        // ID of the std car key instance you want to update.
        String instanceId = "StdCarKeyPass40001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        String instanceStr = ConfigUtil.readFile("PartialUpdateStdCarKeyInstance.json");

        // Update the std car key instance.
        String urlSegment = "/v2/key_stdcar/instance/";
        JSONObject responseInstance = serverApiService.partialUpdateHwWalletObject(urlSegment, instanceId, instanceStr);
        System.out.println("Updated std car key instance: " + JSONObject.toJSONString(responseInstance));
    }
}
