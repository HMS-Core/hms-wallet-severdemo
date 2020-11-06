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

require_once '../../main/util/JweUtil.php';

final class JweTest extends PHPUnit\Framework\TestCase
{
    /**
     * Generate a JWE. This JWE contains full instance information. It's used to add a new instance to HMS wallet serve
     * and bind it to a user. You should generate a JWE with an instance that has not been added to HMS wallet server.
     *
     * @test
     */
    public function generateJWEToAddInstanceAndBindUser()
    {
        echo "generateJWEToAddInstanceAndBindUser begin.\n\n";

        // This is the app ID registered on Huawei AGC website.
        $config = parse_ini_file('../config/config.ini');
        $appId = $config['gw.appid'];
        // Read a new pass instance.
        $newInstance =
            json_decode(file_get_contents("Replace with the instance JSON file to be created. For example: ../config/data/EventTicketInstance.json"), true);
        $newInstance['iss'] = $appId;
        $payload = json_encode($newInstance);

        // You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        $jweSignPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" . $config['servicePrivateKey'] .
            "\n-----END RSA PRIVATE KEY-----";

        try {
            $jwe = JweUtil::generateJwe($jweSignPrivateKey, $payload);
        } catch (Exception $e) {
            echo "Generate JWE failed. " . $e->getMessage();
            return;
        }
        echo "JWE String: ", $jwe, "\n\n";

        echo "JWE link for browser: " . $config['walletWebsiteBaseUrl'] . '?content=' . urlencode($jwe) . "\n";

        static::assertTrue(true);
    }

    /**
     * Generate a thin JWE. This JWEs contains only instanceId information. It's used to bind an existing instance
     * to a user. You should generate a thin JWE with an instanceId that has already been added to HMS wallet server.
     *
     * @test
     */
    public function generateThinJWEToBindUser()
    {
        echo "generateThinJWEToBindUser begin.\n\n";

        // This is the app ID registered on Huawei AGC website.
        $config = parse_ini_file('../config/config.ini');
        $appId = $config['gw.appid'];
        // Bind existing pass instances to a user. Construct a list of instance IDs to be bound.
        $instanceIdList = [
            'instanceIds' => ["Replace with the instance ID to be bond. For example: EventTicketPass10001"],
            'iss' => $appId
        ];
        $payload = json_encode($instanceIdList);

        // You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        $jweSignPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" . $config['servicePrivateKey'] .
            "\n-----END RSA PRIVATE KEY-----";

        try {
            $jwe = JweUtil::generateJwe($jweSignPrivateKey, $payload);
        } catch (Exception $e) {
            echo "Generate JWE failed. " . $e->getMessage();
            return;
        }
        echo "JWE String: ", $jwe, "\n\n";

        echo "JWE link for browser: " . $config['walletWebsiteBaseUrl'] . '?content=' . urlencode($jwe) . "\n";

        static::assertTrue(true);
    }
}
