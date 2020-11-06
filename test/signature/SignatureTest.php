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

require_once '../../main/util/SignUtil.php';

final class SignatureTest extends PHPUnit\Framework\TestCase
{

    /**
     * If your server receive a callback notification request from HMS wallet server, you should verify the
     * signature in the request header with Huawei's fixed signature public key. The signature is signed with
     * "SHA256WithRSA/PSS" algorithm. This test shows an example of verifying a signature.
     *
     * @test
     */
    public function verifySignature()
    {
        echo "verifySignature begin.\n";

        // Assume you received a callback notification request with following request body parameters.
        $receivedBody = [
            "eventId" => "469283774166292993",
            "eventTime" => "2020-10-09T03:41:55.694Z",
            "passNumber" => "passNumber1234",
            "passTypeIdentifier" => "hwpass.com.xxx",
            "eventType" => "DELETE_CARD",
            "sceneType" => "THIRD_PARTY_DELETE_CARD",
            "noticeToken" => "1e4dda10e4590dcd66d1c14bfe1505424091f693996d2db885e54ad040723d7c",
            "pushToken" => "asdfghjkl"];

        // Convert the parameters into a string in certain format.
        $content = SignUtil::toSignString($receivedBody);

        // Assume this is the HMSSign you received in the request header.
        $receivedHMSSign =
            "g6Ylid2v13ibrGCDITYkms7rOxM9Qmpn2nTQy+MDneCvs8n2AznhdH1BOdZxAFEeNvIqaBejupJJNnHweDixxwQub34pt7Kv0wuW3LI0gtut5jsjEJuF9kfPj/f6W6ZfUgZB8R9j6jGMzqWoa7IRkXpIxpdJgral8aE+QwMG51hrzH8j/7EbPxpQgFyxuxiZimaeKDbgJ2yWIDtnaEVs+6NxLMhz+Vgo0vxEiyo+TEdcpkl0ahMA8XCXGs6lqlbl+G8imlU4+pMvM+IL9ygCbDWgwj6pmfrkDnD/tYVqElE9SIZ79+ShWLNwUgtWFfzo1ckMRWGSdMfwVd+f6boVIQ==";

        // Huawei's fixed signature public key.
        $signPubKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1+b2/q6KEJfvI65xJLXhPMT8YRUO618zsgaW4pNGZ+r/mwfFC1EOZbcBp7sV0IaxSWeMy0WNyJPSh/JltuiC1R93hfA0Kh3DlaRWaDgJz9VC1b+aPjUOx+uqndOEFiZcKGGnM60YPXfyo7xCDH76/WsWR0G4Ov6MoYQ76RAUT0t+G0oumYGgdLYwx5hJ1ywDKPXszj7A/mKHtWJKiylPIhUK2mLwKR8Y/+3dLNuNomvb7miVgeBFiriwGS1FolQMu433zEugAqRgsiasZAKfVK1BChPmiC812IMS1UPhz1wwpXzzkjQ1YQUGjnbHpooKobeCyctKKgF27F84egpzsQIDAQAB";

        // Verify signature.
        $isValid = SignUtil::verifySignature($content, $signPubKey, $receivedHMSSign);

        echo "String to be signed: " . $content . "\n";
        echo "Received HMSSign: " . $receivedHMSSign . "\n";
        echo "Is signature valid? " . ($isValid ? 'true' : 'false');

        static::assertTrue(true);
    }
}