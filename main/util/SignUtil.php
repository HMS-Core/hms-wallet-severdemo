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
 * JWE utility class.
 *
 * @since 2020-05-25
 */
class SignUtil
{
    /**
     * Convert an array to a string in certain format.
     *
     * @param $array
     * @return string
     */
    public static function toSignString(array $array)
    {
        ksort($array);
        $result = "";

        foreach ($array as $key => $val) {
            $result .= $key . '=' . $val . '&';
        }

        return trim($result, "&");
    }

    /**
     * Verify a signature.
     *
     * @param $content
     * @param $publicKey
     * @param $sign
     * @return boolean
     */
    public static function verifySignature($content, $publicKey, $sign)
    {
        $rsa = new Crypt_RSA();
        if ($rsa->loadKey($publicKey) != TRUE) {
            throw new InvalidArgumentException("Load public key failed.");
        }
        $rsa->setHash("sha256");
        $rsa->setMGFHash("sha256");
        $rsa->setSignatureMode(CRYPT_RSA_SIGNATURE_PSS);

        return $rsa->verify($content, base64_decode($sign));
    }
}