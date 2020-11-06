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
using System.IO;
using System.Net;
using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace hmswallet {
    public class OAuth {
        private static readonly String TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

        public static String GetToken (String clientId, String clientSecret) {
            HttpWebRequest request = (HttpWebRequest) WebRequest.Create (TOKEN_URL);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            StringBuilder sb = new StringBuilder ().Append ("grant_type=client_credentials&client_secret=").Append (clientSecret).Append ("&client_id=").Append (clientId);
            String reqContent = sb.ToString ();
            using (Stream reqStream = request.GetRequestStream ()) {
                reqStream.Write (Encoding.UTF8.GetBytes (reqContent), 0, Encoding.UTF8.GetBytes (reqContent).Length);
                HttpWebResponse httpWebResponse = (HttpWebResponse) request.GetResponse ();
                using (Stream responseStream = httpWebResponse.GetResponseStream ()) {
                    using (StreamReader streamReader = new StreamReader (responseStream, Encoding.GetEncoding ("utf-8"))) {
                        string respStr = streamReader.ReadToEnd ();
                        JObject tokenResponse = JsonConvert.DeserializeObject<JObject> (respStr);
                        return tokenResponse["access_token"].ToString ();
                    }
                }
            }
        }
    }
}