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

using System.Collections.Generic;
using hmswallet;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json.Linq;

namespace InstanceTest {
    [TestClass]
    public class InstanceTest {
        // This is the App ID registered on Huawei AGC website.
        private string appId = "Replace with your App ID";

        // This is your App Secret shown on Huawei AGC website.
        private static readonly string appSecret = "Replace with your App Secrete";

        // Select from EventTicket, Flight, GiftCard, Loyalty, Offer, Transit according to your service ID (passTypeIdentifier).
        private CardType cardType = CardType.EventTicket;

        // Select from CHINA, ASIA, EUROPE, LAT, and RUSSIA according to your account location.
        private HmsSite site = HmsSite.CHINA;

        private ServerApiService serverApiService = new ServerApiService ();

        /**
         * Add a pass instance to HMS wallet server.
         * Run createModelTest before running this test.
         * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
         * sending a JWE with complete instance information, without using this API. See JWE example tests.
         * POST http://xxx/hmspass/v1/xxx/instance
         */
        [TestMethod]
        public void addInstanceTest () {
            System.Console.WriteLine ("addInstanceTest begin.");

            JObject respModel = serverApiService.createInstance (appId, appSecret, cardType, site);

            System.Console.WriteLine ("Posted instance: " + respModel.ToString ());
        }

        /**
         * Get a pass instance by its instance ID.
         * Run addInstanceTest test before running this test.
         * GET http://xxx/hmspass/v1/xxx/instance/{instanceId}
         */
        [TestMethod]
        public void getInstanceTest () {
            System.Console.WriteLine ("getInstanceTest begin.");

            string instanceId = "EventTicketPass10001";

            JObject respInstance = serverApiService.querySingleInstance (appId, appSecret, instanceId, cardType, site);

            System.Console.WriteLine ("Get instance: " + respInstance.ToString ());
        }

        /**
         * Get pass instances belonging to a specific pass model.
         * Run addInstanceTest test before running this test.
         * GET http://xxx/hmspass/v1/xxx/instance?modelId=xxx&session=xxx&pageSize=xxx
         */
        [TestMethod]
        public void getInstanceListTest () {
            System.Console.WriteLine ("getInstanceListTest begin.");

            List<JObject> respInstance = serverApiService.queryBatchInstance (appId, appSecret, cardType, site, 2);
        }

        /**
         * Overwrite a pass instance.
         * Run addInstanceTest test before running this test.
         * PUT http://xxx/hmspass/v1/xxx/instance/{instanceId}
         */
        [TestMethod]
        public void fullUpdateInstanceTest () {
            System.Console.WriteLine ("fullUpdateInstanceTest begin.");

            JObject respInstance = serverApiService.modifyEntireInstance (appId, appSecret, cardType, site);

            System.Console.WriteLine ("full update instance: " + respInstance.ToString ());
        }

        /**
         * Update a pass instance.
         * Run addInstanceTest test before running this test.
         * PATCH http://xxx/hmspass/v1/xxx/instance/{instanceId}
         */
        [TestMethod]
        public void partialUpdateInstanceTest () {
            System.Console.WriteLine ("partialUpdateInstanceTest begin.");

            string instanceId = "EventTicketPass10001";

            JObject respInstance = serverApiService.modifyPartialInstance (appId, appSecret, instanceId, cardType, site);

            System.Console.WriteLine ("part update instance: " + respInstance.ToString ());
        }

        /**
         * Add messages to a pass instance.
         * Run addInstanceTest test before running this test.
         * POST http://xxx/hmspass/v1/xxx/instance/{instanceId}/addMessage
         */
        [TestMethod]
        public void addMessageToInstanceTest () {
            System.Console.WriteLine ("addMessageToInstanceTest begin.");

            string instanceId = "EventTicketPass10001";

            JObject respInstance = serverApiService.addInstanceMessage (appId, appSecret, instanceId, cardType, site);

            System.Console.WriteLine ("add message instance: " + respInstance.ToString ());
        }

        /**
         * Update linked offers of a loyalty instance.
         * This test is only available for Loyalty instances.
         * PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}/linkedoffers
         */
        [TestMethod]
        public void updateLinkedOffersToLoyaltyInstanceTest () {
            System.Console.WriteLine ("updateLinkedOffersToLoyaltyInstanceTest begin.");

            string instanceId = "LoyaltyPass40001";

            JObject respInstance = serverApiService.updateLinkedOffersToLoyaltyInstance (appId, appSecret, instanceId, CardType.Loyalty, site);

            System.Console.WriteLine ("update link offer: " + respInstance.ToString ());
        }
    }
}