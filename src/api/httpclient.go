package api

import (
	"crypto/tls"
	"errors"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"sync"
	"time"
)

var (
	clientPoll = sync.Pool{
		New: func() interface{} {
			tr := &http.Transport{
				TLSClientConfig: &tls.Config{
					InsecureSkipVerify: true,
				},
				MaxConnsPerHost:   1,
				DisableKeepAlives: true,
			}
			client := &http.Client{
				Transport: tr,
				Timeout:   10 * time.Second,
			}
			return client
		},
	}
)

func doRequest(method string, urlStr string, header map[string][]string, body io.Reader) (string, error) {
	req, err := http.NewRequest(method, urlStr, body)
	if err != nil {
		return "", err
	}

	req.Header = header
	client := clientPoll.Get().(*http.Client)
	resp, err := client.Do(req)
	clientPoll.Put(client)
	if resp != nil {
		defer resp.Body.Close()
	}

	if err != nil {
		return "", err
	}

	respBody, err := ioutil.ReadAll(resp.Body)
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return string(respBody), errors.New(fmt.Sprintf("Request error, statusCode: %v, responseBody: %s", resp.StatusCode, string(respBody)))
	}
	return string(respBody), err
}
