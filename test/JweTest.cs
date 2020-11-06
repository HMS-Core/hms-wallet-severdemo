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

using System;
using System.Text;
using hmswallet;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace JweTest {
    [TestClass]
    public class JweTest {
        // This is the app ID registered on Huawei AGC website.
        private string appId = "Replace with your App ID";

        // You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
        private string jweSignPrivateKey = "Replace with your private key";

        // Select from CHINA, ASIA, EUROPE, LAT, and RUSSIA according to your account location.
        private HmsSite site = HmsSite.CHINA;

        // Complete instance JSON file for JWE.
        private string instanceFilePath = "Replace with the instance JSON file to be created. For example: ../../../resource/EventTicketInstance.json";

        // Instance ID for thin JWE.
        private string instanceId = "Replace with the instance ID to be bond. For example: EventTicketPass10001";

        /**
         * Generate a JWE. This JWE contains full instance information. It's used to add a new instance to HMS wallet serve
         * and bind it to a user. You should generate a JWE with an instance that has not been added to HMS wallet server.
         * Run createModelTest before running this test.
         */
        [TestMethod]
        public void generateJWEToAddPassAndBindUser () {
            Console.WriteLine ("generateJWEToAddPassAndBindUser begin.\n");

            string payload = getJwePayload (instanceFilePath);

            string jwe = JweUtil.GenerateJwe (jweSignPrivateKey, payload);
            Console.WriteLine ("JWE String: " + jwe);

            StringBuilder rst = new StringBuilder ()
                .Append (getH5Domain (site))
                .Append (BIND_CARD_ADDRESS)
                .Append ("?content=")
                .Append (System.Web.HttpUtility.UrlEncode (jwe));
            Console.WriteLine ("JWE link for browser: " + rst.ToString ());
        }

        /**
         * Generate a thin JWE. This JWEs contains only instanceId information. It's used to bind an existing instance
         * to a user. You should generate a thin JWE with an instanceId that has already been added to HMS wallet server.
         * Run createModelTest and addInstanceTest before running this test.
         */
        [TestMethod]
        public void generateThinJWEToBindUser () {
            Console.WriteLine ("generateThinJWEToBindUser begin.\n");

            JObject payloadJsonObject = new JObject ();
            payloadJsonObject.Add ("instanceIds", JToken.FromObject (new [] { instanceId }));
            payloadJsonObject.Add ("iss", appId);
            string payload = payloadJsonObject.ToString ().Replace ("\n", "").Replace (" ", "").Replace ("\t", "").Replace ("\r", "");

            // Generate a thin JWE.
            string jwe = JweUtil.GenerateJwe (jweSignPrivateKey, payload);
            Console.WriteLine ("JWE String: " + jwe);

            StringBuilder rst = new StringBuilder ()
                .Append (getH5Domain (site))
                .Append (BIND_CARD_ADDRESS)
                .Append ("?content=")
                .Append (System.Web.HttpUtility.UrlEncode (jwe));
            Console.WriteLine ("JWE link to for browser: " + rst.ToString ());
        }

        private string getJwePayload (string filePath) {
            string newInstance = CommonUtil.ReadJsonFile (filePath);
            JObject newInstanceJObject = (JObject) JsonConvert.DeserializeObject (newInstance);
            newInstanceJObject.Add ("iss", appId);
            return newInstanceJObject.ToString ().Replace ("\n", "").Replace (" ", "").Replace ("\t", "").Replace ("\r", "");
        }

        private string getH5Domain (HmsSite site) {
            string header = "https://";
            switch (site) {
                case HmsSite.CHINA:
                    return header + CHINA_H5_DOMAIN;
                case HmsSite.ASIA:
                    return header + ASIA_H5_DOMAIN;
                case HmsSite.EUROPE:
                    return header + EUROPE_H5_DOMAIN;
                case HmsSite.LAT:
                    return header + LAT_H5_DOMAIN;
                case HmsSite.RUSSIA:
                    return header + RUSSIA_H5_DOMAIN;
                default:
                    return String.Empty;
            }
        }

        private readonly string CHINA_H5_DOMAIN = "walletpass-drcn.cloud.huawei.com";
        private readonly string ASIA_H5_DOMAIN = "walletpass-dra.cloud.huawei.com";
        private readonly string EUROPE_H5_DOMAIN = "walletpass-dre.cloud.huawei.com";
        private readonly string LAT_H5_DOMAIN = "walletpass-dra.cloud.huawei.com";
        private readonly string RUSSIA_H5_DOMAIN = "walletpass-drru.cloud.huawei.com";
        private readonly string BIND_CARD_ADDRESS = "/walletkit/consumer/pass/save";
    }
}