package jwe

import (
	"../../api"
	"fmt"
	"io/ioutil"
	"log"
	"net/url"
	"path/filepath"
	"testing"
)

var (
	WalletConfig *api.Configer
)

func init() {
	if WalletConfig == nil {
		var err error
		absPath, _ := filepath.Abs("../../release.config.properties")
		WalletConfig, err = api.NewConfiger(absPath)
		if err != nil {
			log.Fatal("Load config failed.")
		}
	}
}

func TestGenerateThinJWEToBindUser(t *testing.T) {
	t.Log("generateThinJWEToBindUser begin.")
	// This is the app ID registered on Huawei AGC website.
	appId, _ := WalletConfig.Get("gw.appid")

	payloadMap := make(map[string]interface{})
	payloadMap["iss"] = appId
	payloadMap["instanceIds"] = []string{"Replace with the instance ID to be bond. For example: EventTicketPass10001"}

	payload, _ := api.StructToJSON(payloadMap)
	// You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
	// Notice: privateKey format like "-----BEGIN RSA PRIVATE KEY-----
	// xxxxxx
	// -----END RSA PRIVATE KEY-----"
	// For example: jweSignPrivateKey := `
	// -----BEGIN RSA PRIVATE KEY-----
	// xxxxxx
	// -----END RSA PRIVATE KEY-----
	// `
	jweSignPrivateKey := "Replace with your private key."

	// Generate a thin JWE.
	jwe := api.GenerateJwe(jweSignPrivateKey, payload)
	t.Logf("jwe string: %s", jwe)

	// Replace {walletkit_website_url} with one of the following strings according to your account location.
	// walletpass-drcn.cloud.huawei.com for China
	// walletpass-drru.cloud.huawei.com for Russia
	// walletpass-dra.cloud.huawei.com for Asia, Africa, and Latin America
	// walletpass-dre.cloud.huawei.com for Europe
	t.Log("JWE link for browser: " + "https://{walletkit_website_url}/walletkit/consumer/pass/save?jwt=" + url.QueryEscape(jwe))
}

func TestGenerateJWEToAddInstanceAndBindUser(t *testing.T) {
	t.Log("generateJWEToAddPassAndBindUser begin.")

	// This is the app ID registered on Huawei AGC website.
	appId, _ := WalletConfig.Get("gw.appid")

	payloadMap := make(map[string]interface{})
	// Read an event ticket instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/EventTicketInstance.json")
	dataByte, err := ioutil.ReadFile(dataPath)
	if err != nil {
		fmt.Errorf("err: %v", err)
	}
	api.JSONToStruct(string(dataByte), &payloadMap)
	payloadMap["iss"] = appId
	payload, _ := api.StructToJSON(payloadMap)

	// You generated a pair of RSA keys while applying for services on AGC. Use that private key here.
	// Notice: privateKey format like "-----BEGIN RSA PRIVATE KEY-----
	// xxxxxx
	// -----END RSA PRIVATE KEY-----"
	jweSignPrivateKey := "Replace with your private key."

	// Generate a thin JWE.
	jwe := api.GenerateJwe(jweSignPrivateKey, payload)
	t.Logf("jwe string: %s", jwe)

	// Replace {walletkit_website_url} with one of the following strings according to your account location.
	// walletpass-drcn.cloud.huawei.com for China
	// walletpass-drru.cloud.huawei.com for Russia
	// walletpass-dra.cloud.huawei.com for Asia, Africa, and Latin America
	// walletpass-dre.cloud.huawei.com for Europe
	t.Log("JWE link for browser: " + "https://{walletkit_website_url}/walletkit/consumer/pass/save?jwt=" + url.QueryEscape(jwe))

}
