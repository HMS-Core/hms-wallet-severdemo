package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)


// Add a gift card instance to HMS wallet server.
// Run the "createGiftCardModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/giftcard/instance
func TestAddGiftCardInstance(t *testing.T) {
	t.Log("addGiftCardInstance begin.")

	// Read a gift card instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/GiftCardInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new gift card instance to HMS wallet server.
	urlSegment := "giftcard/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add gift card instance failed. %v", err)
	} else {
		t.Logf("Add gift card instance success, giftcard/instance response: %v", resp)
	}
}

// Get a gift card instance by its instance ID.
// Run the "createGiftCardInstance" test before running this test.
// GET http://xxx/hmspass/v1/giftcard/instance/{instanceId}
func TestGetGiftCardInstance(t *testing.T) {
	t.Log("getGiftCardInstance begin.")

	// ID of the gift card instance you want to get.
	instanceId := "GiftCardPass30001"

	// Get the gift card instance.
	urlSegment := fmt.Sprintf("giftcard/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get gift card instance failed. %v", err)
	} else {
		t.Logf("Get gift card instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get gift card instance belonging to a specific gift card model.
// Run the "createGiftCardInstance" test before running this test.
// GET http://xxx/hmspass/v1/giftcard/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetGiftCardInstanceList(t *testing.T) {
	t.Log("getGiftCardInstanceList begin.")

	// Model ID of gift card instances you want to get.
	modelId := "GiftCardTestModel"

	// Get a list of gift card instances.
	urlSegment := "giftcard/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Overwrite a gift card instance.
// Run the "createGiftCardInstance" test before running this test.
// PUT http://xxx/hmspass/v1/giftcard/instance/{instanceId}
func TestFullUpdateGiftCardInstance(t *testing.T) {
	t.Log("fullUpdateGiftCardInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateGiftCardInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	// Update the gift card instance.
	urlSegment := fmt.Sprintf("giftcard/instance/%s", instance.SerialNumber)

	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update gift card instance failed. %v", err)
	} else {
		t.Logf("Update gift card instance success, response: %v", resp)
	}
}

// Update a gift card instance.
// Run the "createGiftCardInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/giftcard/instance/{instanceId}
func TestPartialUpdateGiftCardInstance(t *testing.T) {
	t.Log("partialUpdateGiftCardInstance begin.")

	// ID of the gift card instance you want to update.
	instanceId := "GiftCardPass30001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateGiftCardInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	urlSegment := fmt.Sprintf("giftcard/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update gift card instance failed. %v", err)
	} else {
		t.Logf("Update gift card instance success, response: %v", resp)
	}
}

// Add messages to a gift card instance.
// Run the "createGiftCardInstance" test before running this test.
// POST http://xxx/hmspass/v1/giftcard/instance/{instanceId}/addMessage
func TestAddMessageToGiftCardInstance(t *testing.T) {
	t.Log("addMessageToGiftCardInstance begin.")

	// ID of the gift card instance you want to update.
	instanceId := "GiftCardPass30001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the gift card  instance.
	urlSegment := fmt.Sprintf("giftcard/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to gift card  instance failed. %v", err)
	} else {
		t.Logf("Add message to gift card  instance success, response: %v", resp)
	}
}
