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

package com.huawei.wallet.hms;

import com.huawei.wallet.nsp.NetworkService;
import com.huawei.wallet.util.ConfigUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of invoking HMS wallet server APIs.
 *
 * @since 2019-11-05
 */
public class ServerApiServiceImpl implements ServerApiService {
    /**
     * Parameter for appending tokens.
     */
    private static final String AUTHORIZATION_HEAD = "Bearer ";

    private static final RestTemplate REST_TEMPLATE;

    static {
        REST_TEMPLATE = new RestTemplate();
        // Use HttpComponentsClientHttpRequestFactory for http requests.
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(20000);
        requestFactory.setReadTimeout(20000);
        // Set request factory.
        REST_TEMPLATE.setRequestFactory(requestFactory);
    }

    @Override
    public JSONObject postToWalletServer(String urlSegment, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment;

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(JSONObject.parseObject(body), header);
        ResponseEntity<JSONObject> exchange =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.POST, entity, JSONObject.class);

        // Return the posted model or instance or NFC card personalized data.
        return exchange.getBody();
    }

    @Override
    public JSONObject getHwWalletObjectById(String urlSegment, String id) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment + id;

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(header);
        ResponseEntity<JSONObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.GET, entity, JSONObject.class);

        // Return the model or instance with the corresponding ID.
        return response.getBody();
    }

    @Override
    public JSONArray getModels(String urlSegment, Integer pageSize) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment;

        HttpEntity<JSONObject> entity = new HttpEntity<>(header);
        if (pageSize == null) {
            ResponseEntity<JSONObject> response =
                REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.GET, entity, JSONObject.class);
            JSONObject batchQueryResult = response.getBody();
            if (batchQueryResult == null) {
                throw new IllegalStateException("Get models failed. Get null response.");
            }
            return batchQueryResult.getJSONArray("data");
        }

        JSONArray modelList = new JSONArray();
        String url = walletServerUrl + "?pageSize=" + pageSize;
        String session = "";
        String finalUrl;
        while (true) {
            if (!session.isEmpty()) {
                finalUrl = url + "&session=" + session;
            } else {
                finalUrl = url;
            }
            ResponseEntity<JSONObject> response =
                REST_TEMPLATE.exchange(finalUrl, HttpMethod.GET, entity, JSONObject.class);
            JSONObject batchQueryResult = response.getBody();
            if (batchQueryResult == null) {
                throw new IllegalStateException("Get models failed. Get null response.");
            }
            JSONArray data = batchQueryResult.getJSONArray("data");
            modelList.addAll(data);
            if (!batchQueryResult.containsKey("pageInfo")) {
                throw new IllegalStateException("Get models failed. Get null pageInfo.");
            }
            JSONObject pageInfo = batchQueryResult.getJSONObject("pageInfo");
            if (!pageInfo.containsKey("nextSession")) {
                return modelList;
            }
            session = pageInfo.getString("nextSession");
        }
    }

    @Override
    public JSONArray getInstances(String urlSegment, String modelId, Integer pageSize) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment;

        HttpEntity<JSONObject> entity = new HttpEntity<>(header);
        if (pageSize == null) {
            ResponseEntity<JSONObject> response = REST_TEMPLATE.exchange(walletServerUrl + "?modelId=" + modelId,
                HttpMethod.GET, entity, JSONObject.class);
            JSONObject batchQueryResult = response.getBody();
            if (batchQueryResult == null) {
                throw new IllegalStateException("Get instances failed. Get null response.");
            }
            return batchQueryResult.getJSONArray("data");
        }
        JSONArray instanceList = new JSONArray();
        String url = walletServerUrl + "?modelId=" + modelId + "&pageSize=" + pageSize;
        String finalUrl;
        String session = "";
        while (true) {
            if (!session.isEmpty()) {
                finalUrl = url + "&session=" + session;
            } else {
                finalUrl = url;
            }
            ResponseEntity<JSONObject> response =
                REST_TEMPLATE.exchange(finalUrl, HttpMethod.GET, entity, JSONObject.class);
            JSONObject batchQueryResult = response.getBody();
            if (batchQueryResult == null) {
                throw new IllegalStateException("Get instances failed. Get null response.");
            }
            JSONArray data = batchQueryResult.getJSONArray("data");
            instanceList.addAll(data);
            if (!batchQueryResult.containsKey("pageInfo")) {
                throw new IllegalStateException("Get instances failed. Get null pageInfo.");
            }
            JSONObject pageInfo = batchQueryResult.getJSONObject("pageInfo");
            if (!pageInfo.containsKey("nextSession")) {
                return instanceList;
            }
            session = pageInfo.getString("nextSession");
        }
    }

    @Override
    public JSONObject fullUpdateHwWalletObject(String urlSegment, String id, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment + id;

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(JSONObject.parseObject(body), header);
        ResponseEntity<JSONObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PUT, entity, JSONObject.class);

        // Return the updated model or instance.
        return response.getBody();
    }

    @Override
    public JSONObject partialUpdateHwWalletObject(String urlSegment, String id, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment + id;

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(JSONObject.parseObject(body), header);
        ResponseEntity<JSONObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PATCH, entity, JSONObject.class);

        // Return the updated model or instance.
        return response.getBody();
    }

    @Override
    public JSONObject addMessageToHwWalletObject(String urlSegment, String id, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment + id + "/addMessage";

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(JSONObject.parseObject(body), header);
        ResponseEntity<JSONObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.POST, entity, JSONObject.class);

        // Return the updated model or instance.
        return response.getBody();
    }

    @Override
    public JSONObject updateLinkedOffersToLoyaltyInstance(String urlSegment, String instanceId, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment + "/linkedoffers";

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(JSONObject.parseObject(body), header);
        ResponseEntity<JSONObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PATCH, entity, JSONObject.class);

        // Return the updated instance.
        return response.getBody();
    }

    /**
     * delete from wallet server.
     *
     * @param urlSegment request URL segment.
     * @return resp
     */
    @Override
    public String deleteFromWalletServer(String urlSegment) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigUtil.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + urlSegment;
        HttpEntity<JSONObject> entity = new HttpEntity<>(null, header);
        // Send the http request and get response.
        ResponseEntity<String> exchange =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.DELETE, entity, String.class);
        return exchange.getBody();
    }

    private HttpHeaders constructHttpHeaders() {
        HttpHeaders header = new HttpHeaders();
        // Get access token.
        try {
            String clientId = ConfigUtil.instants().getValue("gw.appid");
            String clientSecret = ConfigUtil.instants().getValue("gw.appid.secret");
            String accessToken = NetworkService.getToken(clientId, clientSecret);
            String authorization = AUTHORIZATION_HEAD.concat(accessToken);
            header.set("Content-Type", "application/json;charset=utf-8");
            header.set("Authorization", authorization);
            header.set("Accept", "application/json;charset=utf-8");
        } catch (Exception e) {
            throw new IllegalStateException("Build HTTP header failed.", e);
        }
        return header;
    }
}
