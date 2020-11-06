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

package com.huawei.wallet.hms.jwe;

import com.alibaba.fastjson.JSONObject;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.JweUtil;
import org.junit.Test;

import java.net.URLEncoder;

/**
 * Tests for generating JWE and thin JWE.
 *
 * @since 2020-06-12
 */
public class JweTest {
    /**
     * Generate a JWE. This JWE contains full instance information. It's used to add a new instance to HMS wallet serve
     * and bind it to a user. You should generate a JWE with an instance that has not been added to HMS wallet server.
     */
    @Test
    public void generateJWEToAddInstanceAndBindUser() {
        System.out.println("generateJWEToAddPassAndBindUser begin.\n");

        // This is the app ID registered on Huawei AGC website.
        String appId = ConfigUtil.instants().getValue("gw.appid");
        // Read a new pass instance.
        JSONObject newInstance = JSONObject.parseObject(ConfigUtil
            .readFile("Replace with the instance JSON file to be created. For example: EventTicketInstance.json"));
        newInstance.put("iss", appId);
        String payload = newInstance.toJSONString();

        // You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        String jweSignPrivateKey = ConfigUtil.instants().getValue("servicePrivateKey");

        // Generate a JWE.
        String jwe = JweUtil.generateJwe(jweSignPrivateKey, payload);
        System.out.println("JWE String: " + jwe + "\n");

        System.out.println("JWE link for browser: " + ConfigUtil.instants().getValue("walletWebsiteBaseUrl")
            + "?content=" + URLEncoder.encode(jwe));
    }

    /**
     * Generate a thin JWE. This JWEs contains only instanceId information. It's used to bind an existing instance
     * to a user. You should generate a thin JWE with an instanceId that has already been added to HMS wallet server.
     */
    @Test
    public void generateThinJWEToBindUser() {
        System.out.println("generateThinJWEToBindUser begin.\n");

        // This is the app ID registered on Huawei AGC website.
        String appId = ConfigUtil.instants().getValue("gw.appid");
        // Bind existing pass instances to a user. Construct a list of instance IDs to be bound.
        String instanceIdListStr =
            "{\"instanceIds\": [\"Replace with the instance ID to be bond. For example: EventTicketPass10001\"]}";
        JSONObject instanceIdList = JSONObject.parseObject(instanceIdListStr);
        instanceIdList.put("iss", appId);
        String payload = instanceIdList.toJSONString();

        // You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        String jweSignPrivateKey = ConfigUtil.instants().getValue("servicePrivateKey");

        // Generate a thin JWE.
        String jwe = JweUtil.generateJwe(jweSignPrivateKey, payload);
        System.out.println("JWE String: " + jwe + "\n");

        System.out.println("JWE link for browser: " + ConfigUtil.instants().getValue("walletWebsiteBaseUrl")
            + "?content=" + URLEncoder.encode(jwe));
    }
}
