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

require_once '../../main/nsp/NetworkService.php';

/**
 * Interface of invoking wallet api gateway service.
 *
 * @since 2019-11-05
 */
class ServerApiService
{
    const AUTHORIZATION_HEAD = "Bearer ";

    const SUCCEED_CODE = "200";

    /**
     * Post a HwWalletObject to wallet server.
     *
     * @param string $urlSegment request URL segment.
     * @param string $body JSON-type HwWalletObject.
     * @return array the posted HwWalletObject.
     */
    public function postToWalletServer(string $urlSegment, string $body)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $url);
        curl_setopt($request, CURLOPT_POST, 1);
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_POSTFIELDS, $body);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($request);
        if ($response == null) {
            throw new BadMethodCallException("Post " . $url . "\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . $response . "\n");
        }
        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != static::SUCCEED_CODE) {
            throw new UnexpectedValueException("Post " . $url . "\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true);
    }

    /**
     * Return the model/instance for a given model/instance ID.
     *
     * @param string $urlSegment request URL segment.
     * @param string $id model ID or instance ID.
     * @return array the model/instance.
     */
    public function getHwWalletObjectById(string $urlSegment, string $id)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment . $id;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $url);
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($request);
        if ($response == null) {
            throw new BadMethodCallException("Get " . $url . "\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . "\n");
        }
        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != static::SUCCEED_CODE) {
            throw new UnexpectedValueException("Get " . $url . "\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true);
    }

    /**
     * Return a list of models belonging to a specific appId.
     *
     * @param string $urlSegment request URL segment.
     * @param string $pageSize maximum number of model in the returned list. All model created by the issuer
     *        will be returned if pageSize is null.
     */
    public function getModels(string $urlSegment, $pageSize)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        if ($pageSize == null) {
            curl_setopt($request, CURLOPT_URL, $url);
            $response = curl_exec($request);
            $batchQueryResult = json_decode($response, true);
            if ($response == null) {
                throw new BadMethodCallException("Get models failed. Get null response.");
            } else {
                echo "Total models count: " . $batchQueryResult["pageInfo"]["pageSize"] . "\n";
                echo "Models list: " . json_encode($batchQueryResult["data"]);
            }
        } else {
            $modelList = array();
            $finalUrl = $url . "?pageSize=" . $pageSize;
            $session = null;
            $count = 0;
            while (true) {
                if ($session != null) {
                    $finalUrl .= "&session=" . $session;
                }
                curl_setopt($request, CURLOPT_URL, $url);
                $response = curl_exec($request);
                $batchQueryResult = json_decode($response, true);
                if ($batchQueryResult == null) {
                    throw new BadMethodCallException("Get models failed. Get null response.");
                }
                $data = $batchQueryResult["data"];
                $modelList[] = $data;
                $count += count($data);
                $pageInfo = $batchQueryResult["pageInfo"];
                if ($pageInfo == null) {
                    throw new BadMethodCallException("Get models failed. Get null pageInfo.");
                }
                if (!array_key_exists("nextSession", $pageInfo)) {
                    echo "Total models count: " . $count . "\n";
                    echo "Models list: " . json_encode($modelList);
                    return;
                }
                $nextSession = $pageInfo["nextSession"];
                $session = $nextSession;
            }
        }
    }

    /**
     * Return a list of some instance belonging to a specific model.
     *
     * @param string $urlSegment request URL segment.
     * @param string $modelId model ID.
     * @param string $pageSize max number of instance in the returned list. All instance belongs to the wallet
     *        model will be returned if pageSize is null.
     */
    public function getInstances(string $urlSegment, string $modelId, string $pageSize)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment . '?modelId=' . $modelId;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        if ($pageSize == null) {
            curl_setopt($request, CURLOPT_URL, $url);
            $response = curl_exec($request);
            $batchQueryResult = json_decode($response, true);
            if ($response == null) {
                throw new BadMethodCallException("Get instances failed. Get null response.");
            } else {
                echo "Total instances count: " . $batchQueryResult["pageInfo"]["pageSize"] . "\n";
                echo "Instances list: " . json_encode($batchQueryResult["data"]);
            }
        } else {
            $instanceList = array();
            $finalUrl = $url . "&pageSize=" . $pageSize;
            $session = null;
            $count = 0;
            while (true) {
                if ($session != null) {
                    $finalUrl .= "&session=" . $session;
                }
                curl_setopt($request, CURLOPT_URL, $url);
                $response = curl_exec($request);
                $batchQueryResult = json_decode($response, true);
                if ($batchQueryResult == null) {
                    throw new BadMethodCallException("Get instances failed. Get null response.");
                }
                $data = $batchQueryResult["data"];
                $instanceList[] = $data;
                $count += count($data);
                $pageInfo = $batchQueryResult["pageInfo"];
                if ($pageInfo == null) {
                    throw new BadMethodCallException("Get instances failed. Get null pageInfo.");
                }
                if (!array_key_exists("nextSession", $pageInfo)) {
                    echo "Total instances count: " . $count . "\n";
                    echo "Instances list: " . json_encode($instanceList);
                    return;
                }
                $nextSession = $pageInfo["nextSession"];
                $session = $nextSession;
            }
        }
    }

    /**
     * Overwrite a model/instance.
     *
     * @param string $urlSegment request URL segment.
     * @param string $id model/instance ID.
     * @param string $body JSON-type HwWalletObject.
     * @return array the updated model/instance.
     */
    public function fullUpdateHwWalletObject(string $urlSegment, string $id, string $body)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment . $id;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $url);
        curl_setopt($request, CURLOPT_CUSTOMREQUEST, "PUT");
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_POSTFIELDS, $body);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($request);
        if ($response == null) {
            throw new BadMethodCallException("Put " . $url . "\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . $response . "\n");
        }
        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != static::SUCCEED_CODE) {
            throw new UnexpectedValueException("Put " . $url . "\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true);
    }

    /**
     * Update a model/instance.
     *
     * @param string $urlSegment request URL segment.
     * @param string $id model/instance ID.
     * @param string $body JSON-type HwWalletObject.
     * @return array the updated model/instance.
     */
    public function partialUpdateHwWalletObject(string $urlSegment, string $id, string $body)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment . $id;
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $url);
        curl_setopt($request, CURLOPT_CUSTOMREQUEST, "PATCH");
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_POSTFIELDS, $body);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($request);
        if ($response == null) {
            throw new BadMethodCallException("Patch " . $url . "\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . $response . "\n");
        }
        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != static::SUCCEED_CODE) {
            throw new UnexpectedValueException("Patch " . $url . "\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true);
    }

    /**
     * Add messages to a HwWalletObject.
     *
     * @param string $urlSegment request URL segment.
     * @param string $id model/instance ID.
     * @param string $body JSON-type messageList object.
     * @return array the updated model/instance.
     */
    public function addMessageToHwWalletObject(string $urlSegment, string $id, string $body)
    {
        // Construct the http header.
        $headers = static::constructHttpHeaders();
        // Construct the http URL.
        $config = parse_ini_file('../config/config.ini');
        $url = $config['walletServerBaseUrl'] . $urlSegment . $id . "/addMessage";
        $request = curl_init();
        curl_setopt($request, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($request, CURLOPT_URL, $url);
        curl_setopt($request, CURLOPT_POST, 1);
        curl_setopt($request, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($request, CURLOPT_POSTFIELDS, $body);
        curl_setopt($request, CURLOPT_RETURNTRANSFER, true);

        $response = curl_exec($request);
        if ($response == null) {
            throw new BadMethodCallException("Post " . $url . "\nError code: " . curl_errno($request) .
                ". Error message: " . curl_error($request) . $response . "\n");
        }
        $returnCode = curl_getinfo($request, CURLINFO_HTTP_CODE);
        curl_close($request);
        if ($returnCode != static::SUCCEED_CODE) {
            throw new UnexpectedValueException("Post " . $url . "\nError code: " . $returnCode .
                ". Error message: " . $response . "\n");
        }

        return json_decode($response, true);
    }

    private function constructHttpHeaders()
    {
        $headers = array();
        $config = parse_ini_file('../config/config.ini');
        // Get access token.
        $accessToken = NetworkService::getToken($config["gw.appid"], $config["gw.appid.secret"]);
        $headers[] = "Content-Type: application/json;charset=utf-8";
        $headers[] = "Authorization: " . static::AUTHORIZATION_HEAD . $accessToken;
        $headers[] = "Accept: application/json;charset=utf-8";

        return $headers;
    }
}
