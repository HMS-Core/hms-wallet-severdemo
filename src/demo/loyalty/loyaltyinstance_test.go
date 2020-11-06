package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)


// Add a loyalty instance to HMS wallet server.
// Run the "createLoyaltyModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/loyalty/instance
func TestAddLoyaltyInstance(t *testing.T) {
	t.Log("addLoyaltyInstance begin.")

	// Read a loyalty instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/LoyaltyInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new loyalty instance to HMS wallet server.
	urlSegment := "loyalty/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add loyalty instance failed. %v", err)
	} else {
		t.Logf("Add loyalty instance success, eventticket/instance response: %v", resp)
	}
}

// Get a loyalty instance by its instance ID.
// Run the "createLoyaltyInstance" test before running this test.
// GET http://xxx/hmspass/v1/loyalty/instance/{instanceId}
func TestGetLoyaltyInstance(t *testing.T) {
	t.Log("getLoyaltyInstance begin.")

	// ID of the loyalty instance you want to get.
	instanceId := "LoyaltyPass40001"

	// Get the loyalty instance.
	urlSegment := fmt.Sprintf("loyalty/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get loyalty instance failed. %v", err)
	} else {
		t.Logf("Get loyalty instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get loyalty instance belonging to a specific loyalty model.
// Run the "createLoyaltyInstance" test before running this test.
// GET http://xxx/hmspass/v1/loyalty/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetLoyaltyInstanceList(t *testing.T) {
	t.Log("getLoyaltyInstanceList begin.")

	// Model ID of loyalty instances you want to get.
	modelId := "LoyaltyTestModel"

	// Get a list of loyalty instances.
	urlSegment := "loyalty/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Overwrite a loyalty instance.
// Run the "createLoyaltyInstance" test before running this test.
// PUT http://xxx/hmspass/v1/loyalty/instance/{instanceId}
func TestFullUpdateLoyaltyInstance(t *testing.T) {
	t.Log("fullUpdateLoyaltyInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateLoyaltyInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	// Update the loyalty instance.
	urlSegment := fmt.Sprintf("loyalty/instance/%s", instance.SerialNumber)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update loyalty instance failed. %v", err)
	} else {
		t.Logf("Update loyalty instance success, response: %v", resp)
	}
}

// Update a loyalty instance.
// Run the "createLoyaltyInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}
func TestPartialUpdateLoyaltyInstance(t *testing.T) {
	t.Log("partialUpdateLoyaltyInstance begin.")

	// ID of the loyalty instance you want to update.
	instanceId := "LoyaltyPass40001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateLoyaltyInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the loyalty instance.
	urlSegment := fmt.Sprintf("loyalty/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update loyalty instance failed. %v", err)
	} else {
		t.Logf("Update loyalty instance success, response: %v", resp)
	}
}

// Add messages to a loyalty instance.
// Run the "createLoyaltyInstance" test before running this test.
// POST http://xxx/hmspass/v1/loyalty/instance/{instanceId}/addMessage
func TestAddMessageToLoyaltyInstance(t *testing.T) {
	t.Log("addMessageToLoyaltyInstance begin.")

	// ID of the loyalty instance you want to update.
	instanceId := "LoyaltyPass40001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the loyalty instance.
	urlSegment := fmt.Sprintf("loyalty/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to loyalty instance failed. %v", err)
	} else {
		t.Logf("Add message to loyalty instance success, response: %v", resp)
	}
}
