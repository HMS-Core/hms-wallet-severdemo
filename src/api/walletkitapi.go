package api

import (
	"../model"
	"bytes"
	"fmt"
	"log"
	"net/http"
	"path/filepath"
	"strings"
)

const authorizationHead = "Bearer "

var (
	WalletConfig *Configer
)

func init() {
	if WalletConfig == nil {
		var err error
		absPath, _ := filepath.Abs("../../release.config.properties")
		WalletConfig, err = NewConfiger(absPath)
		if err != nil {
			log.Fatal("Load config failed.")
		}
	}
}

// PostToWalletServer post request to wallet server
func PostToWalletServer(urlSegment string, body []byte) (string, error) {
	// Construct the http header.
	header := constructHttpHeader()

	// Construct the http URL.
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	requestUrl := baseUrl + urlSegment
	return doRequest(http.MethodPost, requestUrl, header, bytes.NewReader(body))
}

// GetHwWalletObjectById get hw wallet object by Id
func GetHwWalletObjectById(urlSegment string) (string, error) {
	header := constructHttpHeader()

	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	requestUrl := baseUrl + urlSegment
	return doRequest(http.MethodGet, requestUrl, header, nil)
}

// GetModels get models
func GetModels(urlSegment string, pageSize int) {
	header := constructHttpHeader()
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	url := baseUrl + urlSegment
	if pageSize == 0 {
		resp, err := doRequest(http.MethodGet, url, header, nil)
		if err != nil {
			fmt.Printf("Get models failed. %v", err)
			return
		}

		var batchQueryResp model.BatchQueryResp
		err = JSONToStruct(resp, &batchQueryResp)
		fmt.Printf("Total models count: %v \n", batchQueryResp.PageInfo.PageSize)
		fmt.Printf("Models list: %v \n", batchQueryResp.Data)
	}

	// pageSize large than 0
	url = fmt.Sprintf("%s?pageSize=%d", url, pageSize)
	var requestUrl string

	var session string
	var count int
	var modelList [][]model.HwWalletObject

	for {
		if strings.TrimSpace(session) != "" {
			requestUrl = fmt.Sprintf("%s&session=%s", url, session)
		} else {
			requestUrl = url
		}

		resp, err := doRequest(http.MethodGet, requestUrl, header, nil)
		if err != nil {
			log.Printf("Get models failed. %v", err)
			return
		}

		var batchQueryResp model.BatchQueryResp
		err = JSONToStruct(resp, &batchQueryResp)

		if batchQueryResp.Data == nil || len(batchQueryResp.Data) == 0 {
			fmt.Println("Total model count: 0")
			return
		}

		count = count + len(batchQueryResp.Data)
		modelList = append(modelList, batchQueryResp.Data)

		nextSession := batchQueryResp.PageInfo.NextSession
		if len(nextSession) == 0 {
			fmt.Printf("Total models count: %v \n", count)
			modelListJsonString, _ := StructToJSON(modelList)
			fmt.Printf("Models list: %v \n", modelListJsonString)
			return
		}
		session = nextSession
	}
}

// GetInstances get instances
func GetInstances(urlSegment string, modelId string, pageSize int) {
	header := constructHttpHeader()
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	url := baseUrl + urlSegment
	// pageSize = 0
	if pageSize == 0 {
		resp, err := doRequest(http.MethodGet, fmt.Sprintf("%s?modelId=%s", url, modelId), header, nil)
		if err != nil {
			fmt.Printf("Get instances failed. %v", err)
			return
		}

		if err != nil {
			fmt.Printf("Get instances failed. %v", err)
			return
		}

		var batchQueryResp model.BatchQueryResp
		err = JSONToStruct(resp, &batchQueryResp)
		fmt.Printf("Total instance count: %v \n", batchQueryResp.PageInfo.PageSize)
		fmt.Printf("Instances list: %v \n", batchQueryResp.Data)
		return
	}

	// pageSize large than 0
	url = fmt.Sprintf("%s?modelId=%s&pageSize=%d", url, modelId, pageSize)
	var requestUrl string
	var session string
	var count int
	var instanceList [][]model.HwWalletObject
	for {
		if strings.TrimSpace(session) != "" {
			requestUrl = fmt.Sprintf("%s&session=%s", url, session)
		} else {
			requestUrl = url
		}

		resp, err := doRequest(http.MethodGet, requestUrl, header, nil)
		if err != nil {
			fmt.Printf("Get instances failed. %v", err)
			return
		}

		var batchQueryResp model.BatchQueryResp
		err = JSONToStruct(resp, &batchQueryResp)

		if batchQueryResp.Data == nil || len(batchQueryResp.Data) == 0 {
			fmt.Println("Total instances count: 0")
			return
		}

		count = count + len(batchQueryResp.Data)
		instanceList = append(instanceList, batchQueryResp.Data)

		nextSession := batchQueryResp.PageInfo.NextSession
		if len(nextSession) == 0 {
			fmt.Printf("Total instances count: %v \n", count)
			instanceListJsonString, _ := StructToJSON(instanceList)
			fmt.Printf("Instances list: %v \n", instanceListJsonString)
			return
		}
		session = nextSession
	}
}

// FullUpdateHwWalletObject full update hw wallet object
func FullUpdateHwWalletObject(urlSegment string, body []byte) (string, error) {
	header := constructHttpHeader()
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	requestUrl := baseUrl + urlSegment

	return doRequest(http.MethodPut, requestUrl, header, bytes.NewReader(body))
}

// PartialUpdateHwWalletObject partial update hw wallet object
func PartialUpdateHwWalletObject(urlSegment string, body []byte) (string, error) {
	header := constructHttpHeader()
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	requestUrl := baseUrl + urlSegment

	return doRequest(http.MethodPatch, requestUrl, header, bytes.NewReader(body))
}

// AddMessageToHwWalletObject add message to hw wallet object
func AddMessageToHwWalletObject(urlSegment string, body []byte) (string, error) {
	header := constructHttpHeader()
	baseUrl, _ := WalletConfig.Get("walletServerBaseUrl")
	requestUrl := baseUrl + urlSegment

	return doRequest(http.MethodPost, requestUrl, header, bytes.NewReader(body))
}

func constructHttpHeader() map[string][]string {
	clientId, _ := WalletConfig.Get("gw.appid")
	clientSecret, _ := WalletConfig.Get("gw.appid.secret")
	accessToken := GetToken(clientId, clientSecret)

	header := make(map[string][]string, 3)
	header["Content-Type"] = []string{"application/json; charset=UTF-8"}
	header["Accept"] = []string{"application/json; charset=UTF-8"}
	header["Authorization"] = []string{authorizationHead + accessToken}
	return header
}
