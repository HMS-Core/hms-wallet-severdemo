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

package com.huawei.wallet.hms.hmssdk;

import com.huawei.wallet.hms.hmssdk.dto.HwWalletObject;

import java.util.List;

/**
 * Interface of invoking wallet api gateway service.
 *
 * @since 2019-11-05
 */
public interface WalletBuildService {
    /**
     * Create a HwWalletObject.
     *
     * @param requestParam JSON-type HwWalletObject.
     * @param instanceId instance ID.
     * @param modelId model ID.
     * @return the created HwWalletObject.
     */
    HwWalletObject createHwWalletObject(String requestParam, String instanceId, String modelId);

    /**
     * Post a HwWalletObject to wallet server.
     *
     * @param url request URL parameter.
     * @param hwWalletObject the HwWalletObject.
     * @return the posted HwWalletObject.
     */
    HwWalletObject postHwWalletObjectToWalletServer(String url, HwWalletObject hwWalletObject);

    /**
     * Return the wallet model for a given model ID.
     *
     * @param url request URL parameter.
     * @param modelId model ID.
     * @return the wallet model.
     */
    HwWalletObject getWalletModelByModelId(String url, String modelId);

    /**
     * Return a list of some wallet model created by a specific issuer.
     *
     * @param url request URL parameter.
     * @param pageSize maximum number of wallet model in the returned list. All wallet model created by the issuer
     *        will be returned if pageSize is null.
     * @return the wallet model list.
     */
    List<HwWalletObject> listWalletModel(String url, Integer pageSize);

    /**
     * Return the wallet instance for a given instance ID.
     *
     * @param url request URL parameter.
     * @param instanceId instance ID.
     * @return the wallet instance.
     */
    HwWalletObject getWalletInstanceByInstanceId(String url, String instanceId);

    /**
     * Return a list of some wallet instance belong to a specific wallet model.
     *
     * @param url request URL parameter.
     * @param modelId model ID.
     * @param pageSize max number of wallet instance in the returned list. All wallet instance belongs to the wallet
     *        model will be returned if pageSize is null.
     * @return the wallet instance list.
     */
    List<HwWalletObject> listWalletInstance(String url, String modelId, Integer pageSize);

    /**
     * Overwrite a wallet model.
     *
     * @param url request URL parameter.
     * @param modelId model ID.
     * @param body JSON-type HwWalletObject.
     * @return the updated wallet model.
     */
    HwWalletObject fullUpdateWalletModel(String url, String modelId, String body);

    /**
     * Update a wallet model.
     *
     * @param url request URL parameter.
     * @param modelId model ID.
     * @param body JSON-type HwWalletObject.
     * @return the updated wallet model.
     */
    HwWalletObject partialUpdateWalletModel(String url, String modelId, String body);

    /**
     * Overwrite a wallet instance.
     *
     * @param url request URL parameter.
     * @param instanceId instance ID.
     * @param body JSON-type wallet instance.
     * @return the updated wallet instance.
     */
    HwWalletObject fullUpdateWalletInstance(String url, String instanceId, HwWalletObject body);

    /**
     * Update a wallet instance.
     *
     * @param url request URL parameter.
     * @param instanceId instance ID.
     * @param body JSON-type wallet instance.
     * @return the updated wallet instance.
     */
    HwWalletObject partialUpdateWalletInstance(String url, String instanceId, HwWalletObject body);

    /**
     * Add messages to a HwWalletObject.
     *
     * @param url request URL parameter.
     * @param resourceId model ID or instance ID.
     * @param body JSON-type messageList object.
     * @return the updated HwWalletObject.
     */
    HwWalletObject addMessageToHwWalletObject(String url, String resourceId, String body);

    /**
     * Add/remove linked offer IDs to/from a loyalty instance.
     *
     * @param url request URL parameter.
     * @param instanceId instance ID.
     * @param body JSON-type LinkedOfferInstanceIds object.
     * @return the updated loyalty instance.
     */
    HwWalletObject updateLinkedOffersToLoyaltyInstance(String url, String instanceId, String body);
}
