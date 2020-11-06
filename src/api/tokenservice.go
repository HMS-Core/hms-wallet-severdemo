package api

import (
	"fmt"
	"log"
	"net/http"
	"strings"
)

// AccessTokenResponse request token response struct
type AccessTokenResponse struct {
	AccessToken string `json:"access_token"`
	ExpiresIn   int64  `json:"expires_in"`
	TokenType   string `json:"token_type"`
}

// GetToken get access token
func GetToken(clientId, clientSecret string) string {
	return requestToken(clientId, clientSecret)
}

func requestToken(clientId, clientSecret string) string {
	header := make(map[string][]string, 3)
	header["Content-Type"] = []string{"application/x-www-form-urlencoded; charset=UTF-8"}

	body := fmt.Sprintf("grant_type=client_credentials&client_id=%s&client_secret=%s", clientId, clientSecret)
	tokenUrl, _ := WalletConfig.Get("gw.tokenUrl")
	resp, err := doRequest(http.MethodPost, tokenUrl, header, strings.NewReader(body))
	if err != nil || resp == "" {
		log.Fatal("Request accessToken failed. ")
		return ""
	}

	var accessTokenResp AccessTokenResponse
	err = JSONToStruct(resp, &accessTokenResp)
	if err != nil {
		log.Fatal("Convert resp error.", resp)
		return ""
	}

	return accessTokenResp.AccessToken
}
