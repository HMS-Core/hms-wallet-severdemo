package signature

import (
	"../../../src/api"
	"bytes"
	"encoding/base64"
	"sort"
	"testing"
)

func TestVerifySign(t *testing.T) {

	// Assume you received a callback notification request with following request body parameters.
	receiveBody := make(map[string]string)
	receiveBody["eventId"] = "469283774166292993"
	receiveBody["eventTime"] = "2020-10-09T03:41:55.694Z"
	receiveBody["passNumber"] = "passNumber1234"
	receiveBody["passTypeIdentifier"] = "hwpass.com.xxx"
	receiveBody["eventType"] = "DELETE_CARD"
	receiveBody["sceneType"] = "THIRD_PARTY_DELETE_CARD"
	receiveBody["noticeToken"] = "1e4dda10e4590dcd66d1c14bfe1505424091f693996d2db885e54ad040723d7c"
	receiveBody["pushToken"] = "asdfghjkl"

	// Convert the parameters into a string in certain format.
	content := toSignString(receiveBody)
	receivedHMSSign := "g6Ylid2v13ibrGCDITYkms7rOxM9Qmpn2nTQy+MDneCvs8n2AznhdH1BOdZxAFEeNvIqaBejupJJNnHweDixxwQub34pt7Kv0wuW3LI0gtut5jsjEJuF9kfPj/f6W6ZfUgZB8R9j6jGMzqWoa7IRkXpIxpdJgral8aE+QwMG51hrzH8j/7EbPxpQgFyxuxiZimaeKDbgJ2yWIDtnaEVs+6NxLMhz+Vgo0vxEiyo+TEdcpkl0ahMA8XCXGs6lqlbl+G8imlU4+pMvM+IL9ygCbDWgwj6pmfrkDnD/tYVqElE9SIZ79+ShWLNwUgtWFfzo1ckMRWGSdMfwVd+f6boVIQ=="
	signPubKey := `
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1+b2/q6KEJfvI65xJLXhPMT8YRUO618zsgaW4pNGZ+r/mwfFC1EOZbcBp7sV0IaxSWeMy0WNyJPSh/JltuiC1R93hfA0Kh3DlaRWaDgJz9VC1b+aPjUOx+uqndOEFiZcKGGnM60YPXfyo7xCDH76/WsWR0G4Ov6MoYQ76RAUT0t+G0oumYGgdLYwx5hJ1ywDKPXszj7A/mKHtWJKiylPIhUK2mLwKR8Y/+3dLNuNomvb7miVgeBFiriwGS1FolQMu433zEugAqRgsiasZAKfVK1BChPmiC812IMS1UPhz1wwpXzzkjQ1YQUGjnbHpooKobeCyctKKgF27F84egpzsQIDAQAB
-----END PUBLIC KEY-----
`
	decodeSign, _ := base64.StdEncoding.DecodeString(receivedHMSSign)
	isValid := api.VerifySignPss([]byte(content), decodeSign, []byte(signPubKey))
	t.Log("String to be signed: " + content)
	t.Log("Received HMSSign: " + receivedHMSSign)
	t.Logf("Is signature valid? %v", isValid)
}

func toSignString(body map[string]string) string {
	var keys []string
	for k := range body {
		keys = append(keys, k)
	}
	sort.Strings(keys)

	var signBuffer bytes.Buffer
	for _, key := range keys {
		if body[key] != "" {
			signBuffer.WriteString(key)
			signBuffer.WriteString("=")
			signBuffer.WriteString(body[key])
			signBuffer.WriteString("&")
		}
	}

	signString := signBuffer.String()
	signString = signString[0 : len(signString)-1]
	return signString
}
