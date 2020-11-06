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

require_once '../../vendor/autoload.php';

/**
 * HwWalletObject utility class.
 *
 * @since 2019-12-4
 */
class HwWalletObjectUtil
{
    /**
     * Validate a model.
     *
     * @param array $model the pass model.
     */
    public static function validateModel(array $model)
    {
        if ($model != null) {
            static::checkRequiredParams($model, "passTypeIdentifier", 64);
            static::checkRequiredParams($model, "passStyleIdentifier", 64);
            static::checkRequiredParams($model, "organizationName", 64);
            static::checkRequiredParams($model, "passVersion", 64);
        } else {
            throw new InvalidArgumentException("The model is empty");
        }
    }

    /**
     * Validate an instance.
     *
     * @param array $instance the instance.
     */
    public static function validateInstance(array $instance)
    {
        if ($instance != null) {
            static::checkRequiredParams($instance, "passTypeIdentifier", 64);
            static::checkRequiredParams($instance, "passStyleIdentifier", 64);
            static::checkRequiredParams($instance, "organizationPassId", 64);
            static::checkRequiredParams($instance, "serialNumber", 64);
            static::validateStatusDate($instance);
        } else {
            throw new InvalidArgumentException("The instance is empty.");
        }
    }

    /**
     * Check a required attribute.
     *
     * @param array $jsonObject the object to be checked.
     * @param string $attrName the attribute's name.
     * @param int $maxLen the attribute's maximum length.
     */
    private static function checkRequiredParams(array $jsonObject, string $attrName, int $maxLen)
    {
        if (!array_key_exists($attrName, $jsonObject) || !(is_string($jsonObject[$attrName]))) {
            throw new InvalidArgumentException($attrName . " is missing.");
        }
        if (strlen($jsonObject[$attrName]) > $maxLen) {
            throw new InvalidArgumentException($attrName . " exceeds maximum length.");
        }
    }

    /**
     * Check if the Status of an instance is legal.
     *
     * @param array $instance the instance.
     */
    private static function validateStatusDate(array $instance)
    {
        if (!array_key_exists("fields", $instance) || !array_key_exists("status", $instance["fields"])) {
            return;
        }
        $status = $instance["fields"]["status"];

        if (array_key_exists("state", $status)) {
            $stateTypeList = array('active', 'inactive', 'completed', 'expired');
            if (!in_array(strtolower($status['state']), $stateTypeList)) {
                throw new InvalidArgumentException("state is invalid.");
            }
        }

        if ((array_key_exists("effectTime", $status) && !array_key_exists("expireTime", $status))
            || (!array_key_exists("effectTime", $status) && array_key_exists("expireTime", $status))) {
            throw new InvalidArgumentException("effectTime and expireTime should be both exist or not exist.");
        }

        if (!array_key_exists("expireTime", $status)) {
            return;
        }

        try {
            $effectTime = new DateTime($status['effectTime']);
            $expireTime = new DateTime($status['expireTime']);
            $currentTime = new DateTime();
        } catch (Exception $e) {
            throw new InvalidArgumentException("Invalid time format. Error: " . $e->getMessage());
        }

        if (($expireTime < $currentTime) || ($expireTime < $effectTime)) {
            throw new InvalidArgumentException("expireTime must be later than effectTime and current time.");
        }
    }
}