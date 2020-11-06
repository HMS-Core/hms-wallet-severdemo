package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)


// Add an offer instance to HMS wallet server.
// Run the "createOfferModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/offer/instance
func TestAddOfferInstance(t *testing.T) {
	t.Log("addOfferInstance begin.")

	// Read an offer instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/OfferInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new offer instance to HMS wallet server.
	urlSegment := "offer/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add offer instance failed. %v", err)
	} else {
		t.Logf("Add offer instance success, offer/instance response: %v", resp)
	}
}

// Get an offer instance by its instance ID.
// Run the "createOfferInstance" test before running this test.
// GET http://xxx/hmspass/v1/offer/instance/{instanceId}
func TestGetOfferInstance(t *testing.T) {
	t.Log("getOfferInstance begin.")

	// ID of the offer instance you want to get.
	instanceId := "OfferPass50001"

	// Get the offer instance.
	urlSegment := fmt.Sprintf("offer/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get offer instance failed. %v", err)
	} else {
		t.Logf("Get offer instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get offer instance belonging to a specific offer model.
// Run the "createOfferInstance" test before running this test.
// GET http://xxx/hmspass/v1/offer/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetOfferInstanceList(t *testing.T) {
	t.Log("getOfferInstanceList begin.")

	// Model ID of offer instances you want to get.
	modelId := "OfferTestModel"

	// Get a list of offer instances.
	urlSegment := "offer/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Overwrite an offer instance.
// Run the "createOfferInstance" test before running this test.
// PUT http://xxx/hmspass/v1/offer/instance/{instanceId}
func TestFullUpdateOfferInstance(t *testing.T) {
	t.Log("fullUpdateOfferInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateOfferInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	urlSegment := fmt.Sprintf("offer/instance/%s", instance.SerialNumber)

	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update offer instance failed. %v", err)
	} else {
		t.Logf("Update offer instance success, response: %v", resp)
	}
}

// Update an offer instance.
// Run the "createOfferInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/offer/instance/{instanceId}
func TestPartialUpdateOfferInstance(t *testing.T) {
	t.Log("partialUpdateOfferInstance begin.")

	// ID of the offer instance you want to update.
	instanceId := "OfferPass50001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateOfferInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the offer instance.
	urlSegment := fmt.Sprintf("offer/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update offer instance failed. %v", err)
	} else {
		t.Logf("Update offer instance success, response: %v", resp)
	}
}

// Add messages to an offer instance.
// Run the "createOfferInstance" test before running this test.
// POST http://xxx/hmspass/v1/offer/instance/{instanceId}/addMessage
func TestAddMessageToOfferInstance(t *testing.T) {
	t.Log("addMessageToOfferInstance begin.")

	// ID of the offer instance you want to update.
	instanceId := "OfferPass50001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the offer instance.
	urlSegment := fmt.Sprintf("offer/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to offer instance failed. %v", err)
	} else {
		t.Logf("Add message to offer instance success, response: %v", resp)
	}
}
