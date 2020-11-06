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

namespace ModelTest {
    [TestClass]
    public class ModelTest {
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
         * Create a pass model.
         * Each pass model indicates a style of passes.
         * POST http://xxx/hmspass/v1/xxx/model
         */
        [TestMethod]
        public void createModelTest () {
            System.Console.WriteLine ("createModelTest begin.");

            JObject respModel = serverApiService.createModel (appId, appSecret, cardType, site);

            System.Console.WriteLine ("Posted model: " + respModel.ToString ());
        }

        /**
         * Get a pass model by its model ID.
         * Run createModelTest before running this test.
         * GET http://xxx/hmspass/v1/xxx/model/{modelId}
         */
        [TestMethod]
        public void getModelTest () {
            System.Console.WriteLine ("getModelTest begin.");

            string modelId = "EventTicketTestModel";

            JObject respModel = serverApiService.querySingleModel (appId, appSecret, modelId, cardType, site);

            System.Console.WriteLine ("get model: " + respModel.ToString ());
        }

        /**
         * Get pass models belonging to a specific appId.
         * Run createModelTest before running this test.
         * GET http://xxx/hmspass/v1/xxx/model?session=xxx&pageSize=xxx
         */
        [TestMethod]
        public void getModelListTest () {
            System.Console.WriteLine ("getModelListTest begin.");

            List<JObject> respInstance = serverApiService.queryBatchModel (appId, appSecret, cardType, site, 2);
        }

        /**
         * Overwrite a pass model.
         * Run createModelTest before running this test.
         * PUT http://xxx/hmspass/v1/xxx/model/{modelId}
         */
        [TestMethod]
        public void fullUpdateModelTest () {
            System.Console.WriteLine ("fullUpdateModelTest begin.");

            JObject respModel = serverApiService.modifyEntireModel (appId, appSecret, cardType, site);

            System.Console.WriteLine ("full update model: " + respModel.ToString ());
        }

        /**
         * Update a pass model.
         * Run createModelTest before running this test.
         * PATCH http://xxx/hmspass/v1/xxx/model/{modelId}
         */
        [TestMethod]
        public void partialUpdateModelTest () {
            System.Console.WriteLine ("partialUpdateModelTest begin.");

            string modelId = "EventTicketTestModel";

            JObject respModel = serverApiService.modifyPartialModel (appId, appSecret, modelId, cardType, site);

            System.Console.WriteLine ("partial update model: " + respModel.ToString ());
        }

        /**
         * Add messages to a pass model.
         * Run createModelTest before running this test.
         * POST http://xxx/hmspass/v1/xxx/model/{modelId}/addMessage
         */
        [TestMethod]
        public void addMessageToModelTest () {
            System.Console.WriteLine ("addMessageToModelTest begin.");

            string modelId = "EventTicketTestModel";

            JObject respModel = serverApiService.addModelMessage (appId, appSecret, modelId, cardType, site);

            System.Console.WriteLine ("add message model: " + respModel.ToString ());
        }
    }
}