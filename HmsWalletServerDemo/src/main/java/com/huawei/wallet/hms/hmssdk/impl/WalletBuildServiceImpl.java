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

package com.huawei.wallet.hms.hmssdk.impl;

import com.huawei.wallet.hms.hmssdk.WalletBuildService;
import com.huawei.wallet.hms.hmssdk.dto.BarCode;
import com.huawei.wallet.hms.hmssdk.dto.BatchGetHwWalletResult;
import com.huawei.wallet.hms.hmssdk.dto.Fields;
import com.huawei.wallet.hms.hmssdk.dto.HwWalletObject;
import com.huawei.wallet.hms.hmssdk.dto.LinkDevicePass;
import com.huawei.wallet.hms.hmssdk.dto.RelatedPassId;
import com.huawei.wallet.hms.hmssdk.dto.Localized;
import com.huawei.wallet.hms.hmssdk.dto.Location;
import com.huawei.wallet.hms.hmssdk.dto.PageInfo;
import com.huawei.wallet.hms.hmssdk.dto.Status;
import com.huawei.wallet.hms.hmssdk.dto.ValueObject;
import com.huawei.wallet.nsp.service.OpenGWClient;
import com.huawei.wallet.util.CommonUtil;
import com.huawei.wallet.util.ConfigHelper;

import com.alibaba.fastjson.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of invoking wallet api gateway service.
 *
 * @since 2019-11-05
 */
public class WalletBuildServiceImpl implements WalletBuildService {
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
    public HwWalletObject createHwWalletObject(String requestParam, String instanceId, String modelId) {
        HwWalletObject requestObject = JSONObject.parseObject(requestParam, HwWalletObject.class);
        HwWalletObject hwWalletObject = new HwWalletObject();
        hwWalletObject.setFormatVersion(requestObject.getFormatVersion());
        hwWalletObject.setPassVersion(requestObject.getPassVersion());
        hwWalletObject.setPassTypeIdentifier(requestObject.getPassTypeIdentifier());
        hwWalletObject.setPassStyleIdentifier(modelId);
        hwWalletObject.setOrganizationName(requestObject.getOrganizationName());
        hwWalletObject.setOrganizationPassId(requestObject.getOrganizationPassId());
        hwWalletObject.setWebServiceURL(requestObject.getWebServiceURL());
        hwWalletObject.setAuthorizationToken(requestObject.getAuthorizationToken());
        hwWalletObject.setSerialNumber(instanceId);
        LinkDevicePass linkDevicePass = constructLinkDevicePass(requestObject.getLinkDevicePass());
        hwWalletObject.setLinkDevicePass(linkDevicePass);
        Fields fields = constructFields(requestObject.getFields());
        hwWalletObject.setFields(fields);
        return hwWalletObject;
    }

    private LinkDevicePass constructLinkDevicePass(LinkDevicePass requestLinkDevicePass){
        if(CommonUtil.isNull(requestLinkDevicePass)){
            return null;
        }
        LinkDevicePass newLinkDevicePass = new LinkDevicePass();
        newLinkDevicePass.setNfcType(requestLinkDevicePass.getNfcType());
        newLinkDevicePass.setPassVersion(requestLinkDevicePass.getPassVersion());
        newLinkDevicePass.setSerialNumber(requestLinkDevicePass.getSerialNumber());
        newLinkDevicePass.setSpPublickey(requestLinkDevicePass.getSpPublickey());
        newLinkDevicePass.setToken(requestLinkDevicePass.getToken());
        newLinkDevicePass.setWebServiceURL(requestLinkDevicePass.getWebServiceURL());

        return newLinkDevicePass;
    }

    private Fields constructFields(Fields requestFields) {
        // Create a new Fields entity.
        Fields fields = new Fields();
        fields.setCountryCode(requestFields.getCountryCode());
        fields.setIsUserDiy(requestFields.getIsUserDiy());

        // Set Status in the Fields.
        Status statusRequest = requestFields.getStatus();
        if (!CommonUtil.isNull(statusRequest)) {
            Status status = new Status();
            status.setState(statusRequest.getState());
            status.setEffectTime(statusRequest.getEffectTime());
            status.setExpireTime(statusRequest.getExpireTime());
            fields.setStatus(status);
        }

        // Set RelatedPassIds in the Fields.
        List<RelatedPassId> relatedPassIdsRequest = requestFields.getRelatedPassIds();
        if (!CommonUtil.isNull(relatedPassIdsRequest)) {
            List<RelatedPassId> relatedPassIds = new ArrayList<>();
            for (RelatedPassId relatedPassIdRequest : relatedPassIdsRequest) {
                RelatedPassId relatedPassId = new RelatedPassId();
                relatedPassId.setId(relatedPassIdRequest.getId());
                relatedPassId.setTypeId(relatedPassIdRequest.getTypeId());
                relatedPassIds.add(relatedPassId);
            }
            fields.setRelatedPassIds(relatedPassIds);
        }

        // Set Location in the Fields.
        List<Location> locationListRequest = requestFields.getLocationList();
        if (!CommonUtil.isNull(locationListRequest)) {
            List<Location> locationList = new ArrayList<>();
            for (Location locationRequest : locationListRequest) {
                Location location = new Location();
                location.setLongitude(locationRequest.getLongitude());
                location.setLatitude(locationRequest.getLatitude());
                locationList.add(location);
            }
            fields.setLocationList(locationList);
        }

        // Set BarCode in the Fields.
        BarCode barCodeRequest = requestFields.getBarCode();
        if (!CommonUtil.isNull(barCodeRequest)) {
            BarCode barCode = new BarCode();
            barCode.setText(barCodeRequest.getText());
            barCode.setEncoding(barCodeRequest.getEncoding());
            barCode.setType(barCodeRequest.getType());
            barCode.setValue(barCodeRequest.getValue());
            fields.setBarCode(barCode);
        }

        // Set CommonFields in the Fields.
        List<ValueObject> commonFieldsRequest = requestFields.getCommonFields();
        if (!CommonUtil.isNull(commonFieldsRequest)) {
            List<ValueObject> commonFields = constructValueObjectList(commonFieldsRequest);
            fields.setCommonFields(commonFields);
        }

        // Set AppendFields in the Fields.
        List<ValueObject> appendFieldsRequest = requestFields.getAppendFields();
        if (!CommonUtil.isNull(appendFieldsRequest)) {
            List<ValueObject> appendFields = constructValueObjectList(appendFieldsRequest);
            fields.setAppendFields(appendFields);
        }

        // Set MessageList in the Fields.
        List<ValueObject> messageListRequest = requestFields.getMessageList();
        if (!CommonUtil.isNull(messageListRequest)) {
            List<ValueObject> messageList = constructValueObjectList(messageListRequest);
            fields.setMessageList(messageList);
        }

        // Set TimeList in the Fields.
        List<ValueObject> timeListRequest = requestFields.getTimeList();
        if (!CommonUtil.isNull(timeListRequest)) {
            List<ValueObject> timeList = constructValueObjectList(timeListRequest);
            fields.setTimeList(timeList);
        }

        // Set ImageList in the Fields.
        List<ValueObject> imageListRequest = requestFields.getImageList();
        if (!CommonUtil.isNull(imageListRequest)) {
            List<ValueObject> imageList = constructValueObjectList(imageListRequest);
            fields.setImageList(imageList);
        }

        // Set TextList in the Fields.
        List<ValueObject> textListRequest = requestFields.getTextList();
        if (!CommonUtil.isNull(textListRequest)) {
            List<ValueObject> textList = constructValueObjectList(textListRequest);
            fields.setTextList(textList);
        }

        // Set Localized in the Fields.
        List<Localized> localizedListRequest = requestFields.getLocalized();
        if (!CommonUtil.isNull(localizedListRequest)) {
            List<Localized> localizedList = new ArrayList<>();
            for (Localized localizedRequest : localizedListRequest) {
                Localized localized = new Localized();
                localized.setKey(localizedRequest.getKey());
                localized.setLanguage(localizedRequest.getLanguage());
                localized.setValue(localizedRequest.getValue());
                localizedList.add(localized);
            }
            fields.setLocalized(localizedList);
        }

        // Set TicketInfoList in the Fields.
        List<ValueObject> ticketInfoListRequest = requestFields.getTicketInfoList();
        if (!CommonUtil.isNull(ticketInfoListRequest)) {
            List<ValueObject> ticketInfoList = constructValueObjectList(ticketInfoListRequest);
            fields.setTicketInfoList(ticketInfoList);
        }

        return fields;
    }

    private List<ValueObject> constructValueObjectList(List<ValueObject> valueObjectListRequest) {
        List<ValueObject> valueObjectList = new ArrayList<>();
        for (ValueObject valueObjectRequest : valueObjectListRequest) {
            ValueObject valueObject = new ValueObject();
            valueObject.setKey(valueObjectRequest.getKey());
            valueObject.setValue(valueObjectRequest.getValue());
            valueObject.setLabel(valueObjectRequest.getLabel());
            valueObject.setLocalizedLabel(valueObjectRequest.getLocalizedLabel());
            valueObject.setLocalizedValue(valueObjectRequest.getLocalizedValue());
            valueObject.setRedirectUrl(valueObjectRequest.getRedirectUrl());
            valueObjectList.add(valueObject);
        }
        return valueObjectList;
    }

    @Override
    public HwWalletObject postHwWalletObjectToWalletServer(String url, HwWalletObject hwWalletObject) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http body.
        String body = CommonUtil.toJson(hwWalletObject);
        JSONObject jsonObj = JSONObject.parseObject(body);
        System.out.println("Request JSON : " + jsonObj);
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url;

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> exchange =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.POST, entity, HwWalletObject.class);

        // Return the posted wallet model.
        return exchange.getBody();
    }

    @Override
    public HwWalletObject getWalletModelByModelId(String url, String modelId) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + modelId;

        // Send the http request and get response.
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.GET, new HttpEntity(header), HwWalletObject.class);

        // Return the wallet model with the corresponding model ID.
        return response.getBody();
    }

    @Override
    public List<HwWalletObject> listWalletModel(String url, Integer pageSize) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url;

        HttpEntity entity = new HttpEntity(header);
        if (pageSize == null) {
            ResponseEntity<BatchGetHwWalletResult> response =
                REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.GET, entity, BatchGetHwWalletResult.class);
            BatchGetHwWalletResult batchGetHwWalletResult = response.getBody();
            if (batchGetHwWalletResult == null) {
                System.out.println("listWalletModel failed. batchGetHwWalletResult is null.");
                return null;
            } else {
                return batchGetHwWalletResult.getData();
            }
        } else {
            List<HwWalletObject> walletModelList = new LinkedList<>();
            String tempUrl = walletServerUrl + "?pageSize=" + pageSize;
            String session = "";
            while (true) {
                String finalUrl = tempUrl;
                if (!session.isEmpty()) {
                    finalUrl = finalUrl + "&session=" + session;
                }
                ResponseEntity<BatchGetHwWalletResult> response =
                    REST_TEMPLATE.exchange(finalUrl, HttpMethod.GET, entity, BatchGetHwWalletResult.class);
                BatchGetHwWalletResult batchGetHwWalletResult = response.getBody();
                if (batchGetHwWalletResult == null) {
                    System.out.println("listWalletModel failed. batchGetHwWalletResult is null.");
                    return null;
                }
                List<HwWalletObject> data = batchGetHwWalletResult.getData();
                if (data == null || data.isEmpty()) {
                    return walletModelList;
                }
                walletModelList.addAll(data);
                PageInfo pageInfo = batchGetHwWalletResult.getPageInfo();
                if (pageInfo == null) {
                    System.out.println("listWalletModel failed. pageInfo is null.");
                    return null;
                }
                String nextSession = pageInfo.getNextSession();
                if (nextSession == null || nextSession.isEmpty()) {
                    return walletModelList;
                }
                session = nextSession;
            }
        }
    }

    @Override
    public HwWalletObject getWalletInstanceByInstanceId(String url, String instanceId) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + instanceId;

        // Send the http request and get response.
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.GET, new HttpEntity(header), HwWalletObject.class);

        // Return the wallet instance with the corresponding instanceId.
        return response.getBody();
    }

    @Override
    public List<HwWalletObject> listWalletInstance(String url, String modelId, Integer pageSize) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl") + url;

        HttpEntity entity = new HttpEntity(header);
        if (pageSize == null) {
            ResponseEntity<BatchGetHwWalletResult> response = REST_TEMPLATE.exchange(url + "?modelId=" + modelId,
                HttpMethod.GET, entity, BatchGetHwWalletResult.class);
            BatchGetHwWalletResult batchGetHwWalletResult = response.getBody();
            if (batchGetHwWalletResult == null) {
                System.out.println("listWalletInstance failed. batchGetHwWalletResult is null.");
                return null;
            } else {
                return batchGetHwWalletResult.getData();
            }
        } else {
            List<HwWalletObject> walletInstanceList = new LinkedList<>();
            String tempUrl = baseUrl + "?modelId=" + modelId + "&pageSize=" + pageSize;
            String session = "";
            while (true) {
                String finalUrl = tempUrl;
                if (!session.isEmpty()) {
                    finalUrl = finalUrl + "&session=" + session;
                }
                ResponseEntity<BatchGetHwWalletResult> response =
                    REST_TEMPLATE.exchange(finalUrl, HttpMethod.GET, entity, BatchGetHwWalletResult.class);
                BatchGetHwWalletResult batchGetHwWalletResult = response.getBody();
                if (batchGetHwWalletResult == null) {
                    System.out.println("listWalletInstance failed. batchGetHwWalletResult is null.");
                    return null;
                }
                List<HwWalletObject> data = batchGetHwWalletResult.getData();
                if (data == null || data.isEmpty()) {
                    return walletInstanceList;
                }
                walletInstanceList.addAll(data);
                PageInfo pageInfo = batchGetHwWalletResult.getPageInfo();
                if (pageInfo == null) {
                    System.out.println("listWalletInstance failed. pageInfo is null.");
                    return null;
                }
                String nextSession = pageInfo.getNextSession();
                if (nextSession == null || nextSession.isEmpty()) {
                    return walletInstanceList;
                }
                session = nextSession;
            }
        }
    }

    private HttpHeaders constructHttpHeaders() {
        HttpHeaders header = new HttpHeaders();
        // Get access token.
        try {
            String clientId = ConfigHelper.instants().getValue("gw.appid");
            String clientSecret = ConfigHelper.instants().getValue("gw.appid.secret");
            String accessToken = OpenGWClient.getToken(clientId, clientSecret);
            String authorization = AUTHORIZATION_HEAD.concat(accessToken);
            header.set("Content-Type", "application/json;charset=utf-8");
            header.set("Authorization", authorization);
            header.set("Accept", "application/json;charset=utf-8");
        } catch (Exception e) {
            throw new IllegalStateException("build HttpHeaders Fail", e);
        }
        return header;
    }

    @Override
    public HwWalletObject fullUpdateWalletModel(String url, String modelId, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + modelId;
        // Construct the http body.
        JSONObject jsonObj = JSONObject.parseObject(body);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PUT, entity, HwWalletObject.class);

        // Return the updated wallet model.
        return response.getBody();
    }

    @Override
    public HwWalletObject partialUpdateWalletModel(String url, String modelId, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + modelId;
        // Construct the http body.
        JSONObject jsonObj = JSONObject.parseObject(body);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PATCH, entity, HwWalletObject.class);

        // Return the updated wallet model.
        return response.getBody();
    }

    @Override
    public HwWalletObject fullUpdateWalletInstance(String url, String instanceId, HwWalletObject body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + instanceId;
        // Construct the http body.
        String hwLoyaltyInstance = CommonUtil.toJson(body);
        JSONObject jsonObj = JSONObject.parseObject(hwLoyaltyInstance);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PUT, entity, HwWalletObject.class);

        // Return the updated wallet instance.
        return response.getBody();
    }

    @Override
    public HwWalletObject partialUpdateWalletInstance(String url, String instanceId, HwWalletObject body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url + instanceId;
        // Construct the http body.
        String hwLoyaltyInstance = CommonUtil.toJson(body);
        JSONObject jsonObj = JSONObject.parseObject(hwLoyaltyInstance);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PATCH, entity, HwWalletObject.class);

        // Return the updated wallet instance.
        return response.getBody();
    }

    @Override
    public HwWalletObject addMessageToHwWalletObject(String url, String resourceId, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url.replace("addMessage", "") + resourceId + "/addMessage";
        // Construct the http body.
        JSONObject jsonObj = JSONObject.parseObject(body);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.POST, entity, HwWalletObject.class);

        // Return the updated HwWalletObject.
        return response.getBody();
    }

    @Override
    public HwWalletObject updateLinkedOffersToLoyaltyInstance(String url, String instanceId, String body) {
        // Construct the http header.
        HttpHeaders header = constructHttpHeaders();
        // Construct the http URL.
        String baseUrl = ConfigHelper.instants().getValue("walletServerBaseUrl");
        String walletServerUrl = baseUrl + url.replace("linkedoffers", instanceId) + "/linkedoffers";
        // Construct the http body.
        JSONObject jsonObj = JSONObject.parseObject(body);

        // Send the http request and get response.
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObj, header);
        ResponseEntity<HwWalletObject> response =
            REST_TEMPLATE.exchange(walletServerUrl, HttpMethod.PATCH, entity, HwWalletObject.class);

        // Return the updated wallet instance.
        return response.getBody();
    }
}
