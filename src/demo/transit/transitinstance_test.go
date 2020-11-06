package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)


// Add a transit instance to HMS wallet server.
// Run the "createTransitModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/transit/instance
func TestAddTransitInstance(t *testing.T) {
	t.Log("addTransitInstance begin.")

	// Read a transit instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/TransitInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new transit instance to HMS wallet server.
	urlSegment := "transit/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add transit instance failed. %v", err)
	} else {
		t.Logf("Add transit instance success, transit/instance response: %v", resp)
	}
}

// Get a transit instance by its instance ID.
// Run the "createTransitInstance" test before running this test.
// GET http://xxx/hmspass/v1/transit/instance/{instanceId}
func TestGetTransitInstance(t *testing.T) {
	t.Log("getTransitInstance begin.")

	// ID of the transit instance you want to get.
	instanceId := "TransitPass60001"

	// Get the transit instance.
	urlSegment := fmt.Sprintf("transit/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get transit instance failed. %v", err)
	} else {
		t.Logf("Get transit instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get transit instance belonging to a specific transit model.
// Run the "createTransitInstance" test before running this test.
// GET http://xxx/hmspass/v1/transit/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetTransitInstanceList(t *testing.T) {
	t.Log("getTransitInstanceList begin.")

	// Model ID of offer instances you want to get.
	modelId := "TransitTestModel"

	// Get a list of transit instances.
	urlSegment := "transit/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Overwrite a transit instance.
// Run the "createTransitInstance" test before running this test
// PUT http://xxx/hmspass/v1/transit/instance/{instanceId}
func TestFullUpdateTransitInstance(t *testing.T) {
	t.Log("fullUpdateTransitInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateTransitInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	urlSegment := fmt.Sprintf("transit/instance/%s", instance.SerialNumber)

	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update transit instance failed. %v", err)
	} else {
		t.Logf("Update transit instance success, response: %v", resp)
	}
}

// Update a transit instance.
// Run the "createTransitInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/transit/instance/{instanceId}
func TestPartialUpdateTransitInstance(t *testing.T) {
	t.Log("partialUpdateTransitInstance begin.")

	// ID of the transit instance you want to update.
	instanceId := "TransitPass60001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateTransitInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the transit instance.
	urlSegment := fmt.Sprintf("transit/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update transit instance failed. %v", err)
	} else {
		t.Logf("Update transit instance success, response: %v", resp)
	}
}

// Add messages to a transit instance.
// Run the "createTransitInstance" test before running this test.
// POST http://xxx/hmspass/v1/transit/instance/{instanceId}/addMessage
func TestAddMessageToTransitInstance(t *testing.T) {
	t.Log("addMessageToTransitInstance begin.")

	// ID of the transit instance you want to update.
	instanceId := "TransitPass60001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the transit instance.
	urlSegment := fmt.Sprintf("transit/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to transit instance failed. %v", err)
	} else {
		t.Logf("Add message to transit instance success, response: %v", resp)
	}
}
