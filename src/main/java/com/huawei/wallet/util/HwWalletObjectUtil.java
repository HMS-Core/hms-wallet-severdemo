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

package com.huawei.wallet.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * HwWalletObject utility class.
 *
 * @since 2019-12-4
 */
public class HwWalletObjectUtil {

    private static final String STATE_ACTIVE = "active";

    private static final String STATE_INACTIVE = "inactive";

    private static final String STATE_COMPLETED = "completed";

    private static final String STATE_EXPIRED = "expired";

    private static final List<String> STATE_TYPE_LIST =
        new ArrayList<>(Arrays.asList(STATE_ACTIVE, STATE_INACTIVE, STATE_COMPLETED, STATE_EXPIRED));

    /**
     * Validate a model.
     *
     * @param model the pass model.
     */
    public static void validateModel(JSONObject model) {
        if (model != null) {
            checkRequiredParams(model, "passTypeIdentifier", 64);
            checkRequiredParams(model, "passStyleIdentifier", 64);
            checkRequiredParams(model, "organizationName", 64);
            checkRequiredParams(model, "passVersion", 64);
        } else {
            throw new IllegalArgumentException("The model is empty");
        }
    }

    /**
     * Validate an instance.
     *
     * @param instance the instance.
     */
    public static void validateInstance(JSONObject instance) {
        if (instance != null) {
            checkRequiredParams(instance, "passTypeIdentifier", 64);
            checkRequiredParams(instance, "passStyleIdentifier", 64);
            checkRequiredParams(instance, "organizationPassId", 64);
            checkRequiredParams(instance, "serialNumber", 64);
            validateStatusDate(instance);
        } else {
            throw new IllegalArgumentException("The instance is empty.");
        }
    }

    /**
     * Check a required attribute.
     *
     * @param jsonObject the object to be checked.
     * @param attrName the attribute's name.
     * @param maxLen the attribute's maximum length.
     */
    private static void checkRequiredParams(JSONObject jsonObject, String attrName, int maxLen) {
        if (!jsonObject.containsKey(attrName) || !(jsonObject.get(attrName) instanceof String)) {
            throw new IllegalArgumentException(attrName + " is missing.");
        }
        if (jsonObject.getString(attrName).length() > maxLen) {
            throw new IllegalArgumentException(attrName + " exceeds maximum length.");
        }
    }

    /**
     * Check if the Status of an instance is legal.
     *
     * @param instance the instance.
     */
    private static void validateStatusDate(JSONObject instance) {
        JSONObject fields = instance.getJSONObject("fields");
        if (fields == null || fields.getJSONObject("status") == null) {
            return;
        }
        String state = fields.getJSONObject("status").getString("state");
        String effectTime = fields.getJSONObject("status").getString("effectTime");
        String expireTime = fields.getJSONObject("status").getString("expireTime");
        if (!StringUtils.isEmpty(state)) {
            if (!STATE_TYPE_LIST.contains(state.toLowerCase(Locale.getDefault()))) {
                throw new IllegalArgumentException("state is invalid.");
            }
        }

        if ((StringUtils.isEmpty(effectTime) && !StringUtils.isEmpty(expireTime))
            || (!StringUtils.isEmpty(effectTime) && StringUtils.isEmpty(expireTime))) {
            throw new IllegalArgumentException("effectTime and expireTime should be both exist or not exist.");
        }

        if (StringUtils.isEmpty(expireTime)) {
            return;
        }

        Date statusEffectTime;
        Date statusExpireTime;

        statusEffectTime = getLocalTimeByUtcString(effectTime);
        statusExpireTime = getLocalTimeByUtcString(expireTime);

        if ((statusExpireTime.before(new Date())) || (statusExpireTime.before(statusEffectTime))) {
            throw new IllegalArgumentException("expireTime must be later than effectTime and current time.");
        }
    }

    /**
     * Convert UTC to local time.
     *
     * @param utcTimeString the UTC string.
     * @return the local time in Date type.
     */
    private static Date getLocalTimeByUtcString(String utcTimeString) {
        Date date = null;

        if (utcTimeString != null) {
            try {
                date = Date.from(Instant.parse(utcTimeString));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid time format. Error: " + e.getMessage());
            }
        }
        return date;
    }
}
