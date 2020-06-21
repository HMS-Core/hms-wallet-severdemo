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

import com.huawei.wallet.hms.hmssdk.dto.Fields;
import com.huawei.wallet.hms.hmssdk.dto.HwWalletObject;
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

    private static final String STRING_STATE_ACTIVE = "active";

    private static final String STRING_STATE_INACTIVE = "inactive";

    private static final String STRING_STATE_COMPLETED = "completed";

    private static final String STRING_STATE_EXPIRED = "expired";

    private static final List<String> STATE_TYPE_LIST = new ArrayList<>(
            Arrays.asList(STRING_STATE_ACTIVE, STRING_STATE_INACTIVE, STRING_STATE_COMPLETED, STRING_STATE_EXPIRED));
    /**
     * Validate a wallet model.
     *
     * @param walletModel the wallet model.
     * @return if the wallet model is legal.
     */
    public static boolean validateWalletModel(HwWalletObject walletModel) {
        return checkModelParams(walletModel);
    }

    /**
     * Validate a wallet instance.
     *
     * @param walletInstance the wallet instance.
     * @return if the wallet instance is legal.
     */
    public static boolean validateWalletInstance(HwWalletObject walletInstance) {
        return checkInstanceParams(walletInstance);
    }

    /**
     * Check a required attribute.
     *
     * @param hwWalletObjectAttr the attribute's value.
     * @param attrName the attribute's name.
     * @return if the attribute is defined.
     */
    private static boolean checkRequiredParams(String hwWalletObjectAttr, String attrName) {
        if (StringUtils.isEmpty(hwWalletObjectAttr)) {
            System.out.println();
            System.out.println(attrName + " is empty");
            System.out.println();
            return false;
        }
        return true;
    }

    /**
     * Check required attributes of a wallet model.
     *
     * @param walletClass the wallet model.
     * @return if the wallet model is legal.
     */
    private static boolean checkModelParams(HwWalletObject walletClass) {
        boolean checkFlag;
        if (walletClass != null) {
            checkFlag = checkRequiredParams(walletClass.getPassTypeIdentifier(), "passTypeIdentifier");
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletClass.getPassStyleIdentifier(), "passStyleIdentifier");
            }
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletClass.getOrganizationName(), "organizationName");
            }
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletClass.getPassVersion(), "passVersion");
            }
        } else {
            System.out.println("walletClass is null");
            checkFlag = false;
        }
        return checkFlag;
    }

    /**
     * Check required attributes of a wallet instance.
     *
     * @param walletInstance the wallet instance.
     * @return if the wallet instance is legal.
     */
    private static boolean checkInstanceParams(HwWalletObject walletInstance) {
        boolean checkFlag;
        if (walletInstance != null) {
            checkFlag = checkRequiredParams(walletInstance.getPassTypeIdentifier(), "passTypeIdentifier");
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletInstance.getPassStyleIdentifier(), "passStyleIdentifier");
            }
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletInstance.getOrganizationPassId(), "organizationPassId");
            }
            if (checkFlag) {
                checkFlag = checkRequiredParams(walletInstance.getSerialNumber(), "serialNumber");
            }
            if (checkFlag) {
                checkFlag = validateStatusDate(walletInstance);
            }
        } else {
            System.out.println("walletObject is null");
            checkFlag = false;
        }
        return checkFlag;
    }

    /**
     * Check if the Status of a wallet instance is legal.
     *
     * @param walletInstance the wallet instance.
     * @return if the Status is legal.
     */
    private static boolean validateStatusDate(HwWalletObject walletInstance) {
        Fields fields = walletInstance.getFields();
        if (CommonUtil.isNull(fields) || CommonUtil.isNull(fields.getStatus())) {
            return true;
        }
        String state = fields.getStatus().getState();
        String effectTime = fields.getStatus().getEffectTime();
        String expireTime = fields.getStatus().getExpireTime();
        if (!CommonUtil.isNull(state)) {
            if (!STATE_TYPE_LIST.contains(state.toLowerCase(Locale.getDefault()))) {
                System.out.println("status is abnormal.");
                return false;
            }
            if (STRING_STATE_EXPIRED.equalsIgnoreCase(state)) {
                System.out.println("Instance state has been expired.");
                return false;
            }
        }

        if ((CommonUtil.isNull(effectTime) && !CommonUtil.isNull(expireTime))
                || (!CommonUtil.isNull(effectTime) && CommonUtil.isNull(expireTime))) {
            System.out.println("Instance effectTime and expireTime must be consistent.");
            return false;
        }

        if (CommonUtil.isNull(expireTime)) {
            return true;
        }

        Date statusEffectTime;
        Date statusExpireTime;
        try {
            statusEffectTime = getLocalTimeByUtcString(fields.getStatus().getEffectTime());
            statusExpireTime = getLocalTimeByUtcString(fields.getStatus().getExpireTime());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        if ((statusExpireTime.before(new Date())) || (statusExpireTime.before(statusEffectTime))) {
            System.out.println("Instance status has expired.");
            return false;
        }

        return true;
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
                System.out.println("Invalid time format: " + utcTimeString);
                throw new IllegalArgumentException("Invalid time format: " + utcTimeString);

            }
        }

        return date;
    }
}
