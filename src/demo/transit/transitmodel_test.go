package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new transit model.
// Each transit model indicates a style of transit passes.
// POST http://XXX/hmspass/v1/transit/model
func TestCreateTransitModel(t *testing.T) {
	t.Log("createTransitModel begin.")

	// Read an transit model from a JSON file.
	dataPath, _ := filepath.Abs("../data/TransitModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new transit model to HMS wallet server.
	urlSegment := "transit/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add transit model failed. %v", err)
	} else {
		t.Logf("Add transit model success, transit/model response: %v", resp)
	}
}

// Get a transit model by its model ID.
// Run the "createTransitModel" test before running this test.
// GET http://xxx/hmspass/v1/transit/model/{modelId}
func TestGetTransitModel(t *testing.T) {
	t.Log("getTransitModel begin.")

	// ID of the transit model you want to get.
	modelId := "TransitTestModel"

	// Get the transit model.
	urlSegment := fmt.Sprintf("transit/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get transit model failed. %v", err)
	} else {
		t.Logf("Get transit model success, transit/model response: %v", resp)
	}
}

// Get transit model belonging to a specific appId.
// Run the "createTransitModel" test before running this test.
// GET http://xxx/hmspass/v1/transit/model?session=XXX&pageSize=XXX
func TestGetTransitModelList(t *testing.T) {
	t.Log("getTransitModelList begin.")

	// Get a list of transit models.
	urlSegment := "transit/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite a transit model.
// Run the "createTransitModel" test before running this test.
// PUT http://xxx/hmspass/v1/transit/model/{modelId}
func TestFullUpdateTransitModel(t *testing.T) {
	t.Log("fullUpdateTransitModel begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
	dataPath, _ := filepath.Abs("../data/FullUpdateTransitModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var hwWalletObject model.HwWalletObject
	api.JSONByteToStruct(dataByte, &hwWalletObject)

	// Update the transit model.
	urlSegment := fmt.Sprintf("transit/model/%s", hwWalletObject.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Full update transit model failed. %v", err)
	} else {
		t.Logf("Full update transit model success, response: %v", resp)
	}
}

// Update a transit model.
// Run the "createTransitModel" test before running this test.
// PATCH http://xxx/hmspass/v1/transit/model/{modelId}
func TestPartialUpdateTransitModel(t *testing.T) {
	t.Log("partialUpdateTransitModel begin.")

	// ID of the transit model you want to update.
	modelId := "TransitTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateTransitModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the transit model.
	urlSegment := fmt.Sprintf("transit/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Partial update transit model failed. %v", err)
	} else {
		t.Logf("Partial update transit model success, response: %v", resp)
	}
}

// Add messages to a transit model.
// Run the "createTransitModel" test before running this test.
// POST http://xxx/hmspass/v1/transit/model/{modelId}/addMessage
func TestAddMessageToTransitModel(t *testing.T) {
	// ID of the transit model you want to update.
	modelId := "TransitTestModel"

	// Create a list of messages you want to add to a model. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
	// and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
	// messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	urlSegment := fmt.Sprintf("transit/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to transit model failed. %v", err)
	} else {
		t.Logf("Update message to transit model success, response: %v", resp)
	}
}
