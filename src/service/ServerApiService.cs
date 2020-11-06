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
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace hmswallet {
    public enum Command {
        CREATE_MODEL,
        QUERY_SINGAL_MODEL,
        QUERY_BATCH_MODEL,
        MODIFY_ENTIRE_MODEL,
        MODIFY_PARTICAL_MODEL,
        ADD_MODEL_MESSAGE,
        CREATE_INSTANCE,
        QUERY_SINGAL_INSTANCE,
        QUERY_BATCH_INSTANCE,
        MODIFY_ENTIRE_INSTANCE,
        MODIFY_PARTICAL_INSTANCE,
        ADD_INSTANCE_MESSAGE,
        UPDATE_LINKED_OFFERS
    }

    public enum HmsSite {
        CHINA,
        ASIA,
        EUROPE,
        LAT,
        RUSSIA
    }

    public enum CardType {
        EventTicket,
        Flight,
        GiftCard,
        Loyalty,
        Offer,
        Transit
    }

    public class ServerApiService {
        private readonly string CHINA_SERVER_DOMAIN = "wallet-passentrust-drcn.cloud.huawei.com.cn";
        private readonly string ASIA_SERVER_DOMAIN = "wallet-passentrust-dra.cloud.huawei.asia";
        private readonly string EUROPE_SERVER_DOMAIN = "wallet-passentrust-dre.cloud.huawei.eu";
        private readonly string LAT_SERVER_DOMAIN = "wallet-passentrust-dra.cloud.huawei.lat";
        private readonly string RUSSIA_SERVER_DOMAIN = "wallet-passentrust-drru.cloud.huawei.ru";

        public string getServerDomain (HmsSite site) {
            string header = "https://";
            switch (site) {
                case HmsSite.CHINA:
                    return header + CHINA_SERVER_DOMAIN;
                case HmsSite.ASIA:
                    return header + ASIA_SERVER_DOMAIN;
                case HmsSite.EUROPE:
                    return header + EUROPE_SERVER_DOMAIN;
                case HmsSite.LAT:
                    return header + LAT_SERVER_DOMAIN;
                case HmsSite.RUSSIA:
                    return header + RUSSIA_SERVER_DOMAIN;
                default:
                    return String.Empty;
            }
        }

        public JObject createModel (string appId, string appSecret, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.CREATE_MODEL, cardType, site));
            string url = sb.ToString ();
            string reqMsg = genCreateModelReq (cardType);
            HwWalletObjectUtil.validateModel (JsonConvert.DeserializeObject<JObject> (reqMsg));

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "POST", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject modifyEntireModel (string appId, string appSecret, CardType cardType, HmsSite site) {
            string reqMsg = genModifyEntireModelReq (cardType);
            JObject model = JsonConvert.DeserializeObject<JObject> (reqMsg);
            HwWalletObjectUtil.validateModel (model);
            string modelId = model["passStyleIdentifier"].ToString ();
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.MODIFY_ENTIRE_MODEL, cardType, site))
                .Append (modelId);
            string url = sb.ToString ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "PUT", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject modifyPartialModel (string appId, string appSecret, string modelId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.MODIFY_PARTICAL_MODEL, cardType, site))
                .Append (modelId);
            string url = sb.ToString ();
            string reqMsg = genModifyParticalModelReq (cardType);

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "PATCH", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject querySingleModel (string appId, string appSecret, string modelId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.QUERY_SINGAL_MODEL, cardType, site))
                .Append (modelId);
            string url = sb.ToString ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "GET", null, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public List<JObject> queryBatchModel (string appId, string appSecret, CardType cardType, HmsSite site, Nullable<int> pageSize) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.QUERY_BATCH_MODEL, cardType, site));
            string accessToken = OAuth.GetToken (appId, appSecret);
            string url = sb.ToString ();
            if (pageSize == null) {
                try {
                    string respStr = callWalletServer (url, "GET", null, accessToken);
                    JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                    if (rstObj == null) {
                        throw new System.ArgumentException ("The result is empty");
                    }
                    return rstObj["data"].ToObject<List<JObject>> ();
                } catch (WebException e) {
                    StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                    Console.WriteLine (sr.ReadToEnd ());
                    return null;
                }
            }
            List<JObject> modelList = new List<JObject> ();
            url = url + "?pageSize=" + pageSize;
            string session = "";
            while (true) {
                string finalUrl;
                if (session != null) {
                    finalUrl = url + "&session=" + session;
                } else {
                    finalUrl = url;
                }
                string respStr = callWalletServer (finalUrl, "GET", null, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                if (rstObj == null) {
                    throw new System.ArgumentException ("The result is empty");
                }
                modelList.AddRange (rstObj["data"].ToObject<List<JObject>> ());
                if (rstObj["pageInfo"] == null) {
                    throw new System.ArgumentException ("Get models failed. Get null pageInfo.");
                }
                JObject pageInfo = rstObj["pageInfo"].ToObject<JObject> ();
                if (pageInfo["nextSession"] == null) {
                    return modelList;
                }
                session = pageInfo["nextSession"].ToString ();
            }
        }

        public JObject addModelMessage (string appId, string appSecret, string modelId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.ADD_MODEL_MESSAGE, cardType, site))
                .Append (modelId)
                .Append ("/addMessage");
            string url = sb.ToString ();
            string reqMsg = genAddModelMessageReq ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "POST", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject createInstance (string appId, string appSecret, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.CREATE_INSTANCE, cardType, site));
            string url = sb.ToString ();
            string reqMsg = genCreateInstanceReq (cardType);
            HwWalletObjectUtil.validateInstance (JsonConvert.DeserializeObject<JObject> (reqMsg));

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "POST", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject modifyEntireInstance (string appId, string appSecret, CardType cardType, HmsSite site) {
            string reqMsg = genModifyEntireInstanceReq (cardType);
            JObject instance = JsonConvert.DeserializeObject<JObject> (reqMsg);
            HwWalletObjectUtil.validateInstance (instance);
            string instanceId = instance["serialNumber"].ToString ();
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.MODIFY_ENTIRE_INSTANCE, cardType, site))
                .Append (instanceId);
            string url = sb.ToString ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "PUT", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject modifyPartialInstance (string appId, string appSecret, string instanceId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.MODIFY_PARTICAL_INSTANCE, cardType, site))
                .Append (instanceId);
            string url = sb.ToString ();
            string reqMsg = genModifyParticalInstanceReq (cardType);

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "PATCH", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject querySingleInstance (string appId, string appSecret, string instanceId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.QUERY_SINGAL_INSTANCE, cardType, site))
                .Append (instanceId);
            string url = sb.ToString ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string respStr = callWalletServer (url, "GET", null, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public List<JObject> queryBatchInstance (string appId, string appSecret, CardType cardType, HmsSite site, Nullable<int> pageSize) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.QUERY_BATCH_INSTANCE, cardType, site));

            string reqMsg = genCreateInstanceReq (cardType);
            JObject instance = JsonConvert.DeserializeObject<JObject> (reqMsg);
            string modelId = instance["passStyleIdentifier"].ToString ();

            string accessToken = OAuth.GetToken (appId, appSecret);
            string url;
            if (pageSize == null) {
                try {
                    url = sb.Append ("?modelId=").Append (modelId).ToString ();
                    string respStr = callWalletServer (url, "GET", null, accessToken);
                    JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                    if (rstObj == null) {
                        throw new System.ArgumentException ("The result is empty");
                    }
                    return rstObj["data"].ToObject<List<JObject>> ();
                } catch (WebException e) {
                    StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                    Console.WriteLine (sr.ReadToEnd ());
                    return null;
                }
            }
            List<JObject> instanceList = new List<JObject> ();
            url = sb.Append ("?modelId=").Append (modelId).Append ("&pageSize=").Append (pageSize).ToString ();
            string session = "";
            while (true) {
                string finalUrl;
                if (session != null) {
                    finalUrl = url + "&session=" + session;
                } else {
                    finalUrl = url;
                }

                string respStr = callWalletServer (finalUrl, "GET", null, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                if (rstObj == null) {
                    throw new System.ArgumentException ("The result is empty");
                }
                instanceList.AddRange (rstObj["data"].ToObject<List<JObject>> ());
                if (rstObj["pageInfo"] == null) {
                    throw new System.ArgumentException ("Get instances failed. Get null pageInfo.");
                }
                JObject pageInfo = rstObj["pageInfo"].ToObject<JObject> ();
                if (pageInfo["nextSession"] == null) {
                    return instanceList;
                }
                session = pageInfo["nextSession"].ToString ();
            }
        }

        public JObject addInstanceMessage (string appId, string appSecret, string instanceId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.ADD_INSTANCE_MESSAGE, cardType, site))
                .Append (instanceId)
                .Append ("/addMessage");
            string url = sb.ToString ();

            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string reqMsg = genAddInstanceMessageReq ();
                string respStr = callWalletServer (url, "POST", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public JObject updateLinkedOffersToLoyaltyInstance (string appId, string appSecret, String instanceId, CardType cardType, HmsSite site) {
            StringBuilder sb = new StringBuilder ()
                .Append (getServerDomain (site))
                .Append (getServiceAddress (Command.UPDATE_LINKED_OFFERS, cardType, site))
                .Append (instanceId)
                .Append ("/linkedoffers");
            string url = sb.ToString ();
            try {
                string accessToken = OAuth.GetToken (appId, appSecret);
                string reqMsg = genLinkedOfferInstanceReq ();
                string respStr = callWalletServer (url, "PATCH", reqMsg, accessToken);
                JObject rstObj = JsonConvert.DeserializeObject<JObject> (respStr);
                return rstObj;
            } catch (WebException e) {
                StreamReader sr = new StreamReader (e.Response.GetResponseStream ());
                Console.WriteLine (sr.ReadToEnd ());
                return null;
            }
        }

        public string getServiceAddress (Command command, CardType cardType, HmsSite site) {
            string cardTypeValue = cardType.ToString ().ToLower ();

            switch (command) {
                case Command.CREATE_MODEL:
                    return string.Format ("/hmspass/v1/{0}/model", cardTypeValue);
                case Command.QUERY_SINGAL_MODEL:
                    return string.Format ("/hmspass/v1/{0}/model/", cardTypeValue);
                case Command.QUERY_BATCH_MODEL:
                    return string.Format ("/hmspass/v1/{0}/model", cardTypeValue);
                case Command.MODIFY_ENTIRE_MODEL:
                    return string.Format ("/hmspass/v1/{0}/model/", cardTypeValue);
                case Command.MODIFY_PARTICAL_MODEL:
                    return string.Format ("/hmspass/v1/{0}/model/", cardTypeValue);
                case Command.ADD_MODEL_MESSAGE:
                    return string.Format ("/hmspass/v1/{0}/model/", cardTypeValue);
                case Command.CREATE_INSTANCE:
                    return string.Format ("/hmspass/v1/{0}/instance", cardTypeValue);
                case Command.QUERY_SINGAL_INSTANCE:
                    return string.Format ("/hmspass/v1/{0}/instance/", cardTypeValue);
                case Command.QUERY_BATCH_INSTANCE:
                    return string.Format ("/hmspass/v1/{0}/instance", cardTypeValue);
                case Command.MODIFY_ENTIRE_INSTANCE:
                    return string.Format ("/hmspass/v1/{0}/instance/", cardTypeValue);
                case Command.MODIFY_PARTICAL_INSTANCE:
                    return string.Format ("/hmspass/v1/{0}/instance/", cardTypeValue);
                case Command.ADD_INSTANCE_MESSAGE:
                    return string.Format ("/hmspass/v1/{0}/instance/", cardTypeValue);
                case Command.UPDATE_LINKED_OFFERS:
                    return string.Format ("/hmspass/v1/{0}/instance/", cardTypeValue);
                default:
                    return String.Empty;
            }
        }

        private string callWalletServer (string url, string httpMethod, string reqMsg, string accessToken) {
            // Begin to build request
            HttpWebRequest request = (HttpWebRequest) WebRequest.Create (url);
            request.Method = httpMethod;
            request.PreAuthenticate = true;
            request.ContentType = "application/json";
            request.Headers.Add ("Authorization", "Bearer " + accessToken);
            // End to build request

            // Begin to send Request and process response
            if (reqMsg != null) {
                Stream reqStream = request.GetRequestStream ();
                reqStream.Write (Encoding.UTF8.GetBytes (reqMsg), 0, Encoding.UTF8.GetBytes (reqMsg).Length);
            }
            HttpWebResponse httpWebResponse = (HttpWebResponse) request.GetResponse ();
            if (!httpWebResponse.StatusCode.Equals (HttpStatusCode.OK)) {
                // To process exception
                return string.Empty;
            }
            using (Stream responseStream = httpWebResponse.GetResponseStream ()) {
                using (StreamReader streamReader = new StreamReader (responseStream, Encoding.GetEncoding ("utf-8"))) {
                    string respStr = streamReader.ReadToEnd ();
                    Console.WriteLine ("Response content: " + respStr);
                    // Add service process 
                    return respStr;
                }
            }
            // End to send Request and process response
        }

        protected string genCreateModelReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/{0}Model.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genModifyEntireModelReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/FullUpdate{0}Model.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genModifyParticalModelReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/PartialUpdate{0}Model.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genAddModelMessageReq () {
            string reqMsgFilePath = string.Format ("../../../resource/Messages.json");
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genCreateInstanceReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/{0}Instance.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genModifyEntireInstanceReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/FullUpdate{0}Instance.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genModifyParticalInstanceReq (CardType cardType) {
            string reqMsgFilePath = string.Format ("../../../resource/PartialUpdate{0}Instance.json", cardType);
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genAddInstanceMessageReq () {
            string reqMsgFilePath = string.Format ("../../../resource/Messages.json");
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }

        protected string genLinkedOfferInstanceReq () {
            string reqMsgFilePath = string.Format ("../../../resource/LinkedOfferInstanceIds.json");
            string reqMsg = CommonUtil.ReadJsonFile (reqMsgFilePath);
            return reqMsg;
        }
    }
}