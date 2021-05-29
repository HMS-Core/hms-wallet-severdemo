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

import com.alibaba.fastjson.JSONObject;
import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import org.junit.Test;

/**
 * Queries the function list supported by Huawei pay according to the specified mobile phone's terminal, rom and client version.
 *
 * @since 2021-05-27
 */
public class StdCarKeyQuerySupportFeatureTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add a digital car key instance to HMS wallet server.
     * Run the "createDigitalCarKeyInstance" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v2/key_stdcar/instance
     */
    @Test
    public void querySupportedFeatured() {
        System.out.println("querySupportedFeatured begin.");
        // Post the new digital car key instance to HMS wallet server.
        String terminal = "Replace with your real mobile's terminal number";
        String romVersion = "Replace with your real mobile's rom version";
        String clientVersion = "Replace with your real mobile's client version";
        String urlSegment = "/v1/" + terminal + "/" + romVersion + "/" + clientVersion;

        JSONObject response = serverApiService.queryIssuerAndFlag(urlSegment);
        System.out.println("Queried supported featured: " + JSONObject.toJSONString(response));
    }
}
