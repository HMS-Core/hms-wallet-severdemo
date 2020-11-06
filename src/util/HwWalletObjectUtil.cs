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
using System.Globalization;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace hmswallet {
    public class HwWalletObjectUtil {

        private static string timeFormat = "yyyy-MM-dd'T'HH:mm:ss.fff";
        public static void validateModel (JObject model) {
            if (model != null) {
                checkRequiredParams (model, "passTypeIdentifier", 64);
                checkRequiredParams (model, "passStyleIdentifier", 64);
                checkRequiredParams (model, "organizationName", 64);
                checkRequiredParams (model, "passVersion", 64);
            } else {
                throw new System.ArgumentException ("The model is empty");
            }
        }

        public static void validateInstance (JObject instance) {
            if (instance != null) {
                checkRequiredParams (instance, "passTypeIdentifier", 64);
                checkRequiredParams (instance, "passStyleIdentifier", 64);
                checkRequiredParams (instance, "organizationPassId", 64);
                checkRequiredParams (instance, "serialNumber", 64);
                validateStatusDate (instance);
            } else {
                throw new System.ArgumentException ("The instance is empty");
            }
        }

        private static void checkRequiredParams (JObject jObject, string attrName, int maxLen) {
            if ((jObject.Property (attrName) == null)) {
                throw new System.ArgumentException (string.Format ("Parameter {0} is missing.", attrName));
            }

            if (jObject.GetValue (attrName).ToString ().Length > maxLen) {
                throw new System.ArgumentException (string.Format ("Parameter {0} exceeds maximum length.", attrName));
            }
        }

        private static void validateStatusDate (JObject instance) {
            JObject fields = (JObject) instance.GetValue ("fields");
            if (fields == null || fields.GetValue ("status") == null) {
                return;
            }

            JObject status = (JObject) fields.GetValue ("status");
            string state = status.GetValue ("state").ToString ();

            string effectTime = status.Value<DateTime> ("effectTime").ToString (timeFormat);
            string expireTime = status.Value<DateTime> ("expireTime").ToString (timeFormat);

            if (state != null) {
                string[] stateTypeList = { "active", "inactive", "completed", "expired" };
                if (!((IList<string>) stateTypeList).Contains (state)) {
                    throw new System.ArgumentException ("state is invalid");
                }
            }

            if ((effectTime == null && expireTime != null) || (effectTime != null && expireTime == null)) {
                throw new System.ArgumentException ("effectTime and expireTime should be both exist or not exist");
            }

            if (expireTime == null) {
                return;
            }

            DateTime statusEffectTime;
            DateTime statusExpireTime;

            statusEffectTime = getLocalTimeByUtcString (effectTime);
            statusExpireTime = getLocalTimeByUtcString (expireTime);

            if ((statusExpireTime.CompareTo (DateTime.Now) < 0) || (statusExpireTime.CompareTo (statusEffectTime)) < 0) {
                throw new System.ArgumentException ("expireTime must be later than effectTime and current time.");
            }

        }

        /**
         * Convert UTC to local time.
         *
         * @param utcTimeString the UTC string.
         * @return the local time in Date type.
         */
        private static DateTime getLocalTimeByUtcString (String utcTimeString) {
            DateTime datetime = DateTime.ParseExact (utcTimeString, timeFormat, CultureInfo.InvariantCulture);
            return datetime;
        }

    }
}