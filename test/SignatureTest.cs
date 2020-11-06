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
using System.Text;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json.Linq;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Org.BouncyCastle.Security;

namespace SignatureTest {
    [TestClass]
    public class SignatureTest {

        /**
         * If your server receive a callback notification request from HMS wallet server, you should verify the
         * signature in the request header with Huawei's fixed signature public key. The signature is signed with
         * "SHA256WithRSA/PSS" algorithm. This test shows an example of verifying a signature.
         */
        [TestMethod]
        public void verifySignature () {

            System.Console.WriteLine ("verifySignature begin.");

            JObject receivedBody = new JObject ();
            receivedBody.Add ("eventId", "469283774166292993");
            receivedBody.Add ("eventTime", "2020-10-09T03:41:55.694Z");
            receivedBody.Add ("passNumber", "passNumber1234");
            receivedBody.Add ("passTypeIdentifier", "hwpass.com.xxx");
            receivedBody.Add ("eventType", "DELETE_CARD");
            receivedBody.Add ("sceneType", "THIRD_PARTY_DELETE_CARD");
            receivedBody.Add ("noticeToken", "1e4dda10e4590dcd66d1c14bfe1505424091f693996d2db885e54ad040723d7c");
            receivedBody.Add ("pushToken", "asdfghjkl");

            string content = "eventId=469283774166292993&eventTime=2020-10-09T03:41:55.694Z&eventType=DELETE_CARD&noticeToken=1e4dda10e4590dcd66d1c14bfe1505424091f693996d2db885e54ad040723d7c&passNumber=passNumber1234&passTypeIdentifier=hwpass.com.xxx&pushToken=asdfghjkl&sceneType=THIRD_PARTY_DELETE_CARD";

            // Assume this is the HMSSign you received in the request header.
            string receivedHMSSign =
                "g6Ylid2v13ibrGCDITYkms7rOxM9Qmpn2nTQy+MDneCvs8n2AznhdH1BOdZxAFEeNvIqaBejupJJNnHweDixxwQub34pt7Kv0wuW3LI0gtut5jsjEJuF9kfPj/f6W6ZfUgZB8R9j6jGMzqWoa7IRkXpIxpdJgral8aE+QwMG51hrzH8j/7EbPxpQgFyxuxiZimaeKDbgJ2yWIDtnaEVs+6NxLMhz+Vgo0vxEiyo+TEdcpkl0ahMA8XCXGs6lqlbl+G8imlU4+pMvM+IL9ygCbDWgwj6pmfrkDnD/tYVqElE9SIZ79+ShWLNwUgtWFfzo1ckMRWGSdMfwVd+f6boVIQ==";

            // Huawei's fixed signature public key.
            string signPubKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1+b2/q6KEJfvI65xJLXhPMT8YRUO618zsgaW4pNGZ+r/mwfFC1EOZbcBp7sV0IaxSWeMy0WNyJPSh/JltuiC1R93hfA0Kh3DlaRWaDgJz9VC1b+aPjUOx+uqndOEFiZcKGGnM60YPXfyo7xCDH76/WsWR0G4Ov6MoYQ76RAUT0t+G0oumYGgdLYwx5hJ1ywDKPXszj7A/mKHtWJKiylPIhUK2mLwKR8Y/+3dLNuNomvb7miVgeBFiriwGS1FolQMu433zEugAqRgsiasZAKfVK1BChPmiC812IMS1UPhz1wwpXzzkjQ1YQUGjnbHpooKobeCyctKKgF27F84egpzsQIDAQAB";

            StringBuilder sb = new StringBuilder ()
                .Append ("-----BEGIN PUBLIC KEY-----")
                .Append (System.Environment.NewLine)
                .Append (signPubKey)
                .Append (System.Environment.NewLine)
                .Append ("-----END PUBLIC KEY-----");
            signPubKey = sb.ToString ();

            // Verify signature.
            bool isValid = verifySignature (content, signPubKey, receivedHMSSign);

            System.Console.WriteLine ("String to be signed: " + content);
            System.Console.WriteLine ("Received HMSSign: " + receivedHMSSign);
            System.Console.WriteLine ("Is signature valid? " + isValid);
        }

        private bool verifySignature (string content, string publicKey, string sign) {
            byte[] plain = Encoding.UTF8.GetBytes (content);

            PemReader pemReader = new PemReader (new StringReader (publicKey));
            RsaKeyParameters rsaKey = (RsaKeyParameters) pemReader.ReadObject ();
            ISigner signer = SignerUtilities.GetSigner ("SHA256withRSA/PSS");
            signer.Init (false, rsaKey);
            signer.BlockUpdate (plain, 0, plain.Length);
            return signer.VerifySignature (Convert.FromBase64String (sign));

        }

    }
}