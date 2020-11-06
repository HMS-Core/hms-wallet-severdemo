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

/**
 * Implementation of invoking gateway service.
 *
 * @since 2020-05-25
 */
class NetworkService
{

    /**
     * Get access token with your App ID and App Secret.
     *
     * @param string $clientId your App ID on AGC website.
     * @param string $clientSecret your App Secret on AGC website.
     * @return string the token string
     */
    public static function getToken(string $clientId, string $clientSecret)
    {
        $config = parse_ini_file('../config/config.ini');
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $config['gw.tokenUrl']);
        curl_setopt($request, CURLOPT_POST, 1);
        curl_setopt($request, CURLOPT_HTTPHEADER, array('Content-Type: application/x-www-form-urlencoded'));
        curl_setopt($request, CURLOPT_POSTFIELDS,
            "grant_type=client_credentials&client_secret=" . $clientSecret . "&client_id=" . $clientId);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);
        $response = curl_exec($request);

        if ($response == null) {
            throw new BadMethodCallException("Get token failed.\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . "\n");
        }

        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != "200") {
            throw new UnexpectedValueException("Get token failed.\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true)["access_token"];
    }
}