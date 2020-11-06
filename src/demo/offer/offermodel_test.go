package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new offer model.
// Each offer model indicates a style of offer passes.
// POST http://XXX/hmspass/v1/offer/model
func TestCreateOfferModel(t *testing.T) {
	t.Log("createOfferModel begin.")

	// Read an offer model from a JSON file.
	dataPath, _ := filepath.Abs("../data/OfferModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new offer model to HMS wallet server.
	urlSegment := "offer/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add offer model failed. %v", err)
	} else {
		t.Logf("Add offer model success, offer/model response: %v", resp)
	}
}

// Get an offer model by its model ID.
// Run the "createOfferModel" test before running this test.
// GET http://xxx/hmspass/v1/offer/model/{modelId}
func TestGetOfferModel(t *testing.T) {
	t.Log("getOfferModel begin.")

	// ID of the event ticket model you want to get.
	modelId := "OfferTestModel"

	// Get the event ticket model.
	urlSegment := fmt.Sprintf("offer/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get offer model failed. %v", err)
	} else {
		t.Logf("Get offer model success, offer/model response: %v", resp)
	}
}

// Get offer models belonging to a specific appId.
// Run the "createOfferModel" test before running this test.
// GET http://xxx/hmspass/v1/offer/model?session=XXX&pageSize=XXX
func TestGetOfferModelList(t *testing.T) {
	t.Log("getOfferModelList begin.")

	// Get a list of offer models.
	urlSegment := "offer/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite an offer model.
// Run the "createOfferModel" test before running this test.
// PUT http://xxx/hmspass/v1/offer/model/{modelId}
func TestFullUpdateOfferModel(t *testing.T) {
	t.Log("fullUpdateOfferModel begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
	dataPath, _ := filepath.Abs("../data/FullUpdateOfferModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var hwWalletObject model.HwWalletObject
	api.JSONByteToStruct(dataByte, &hwWalletObject)

	// Update the offer model.
	urlSegment := fmt.Sprintf("offer/model/%s", hwWalletObject.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add offer model failed. %v", err)
	} else {
		t.Logf("Add offer model success, offer/model response: %v", resp)
	}
}

// Update an offer model.
// Run the "createOfferModel" test before running this test.
// PATCH http://xxx/hmspass/v1/offer/model/{modelId}
func TestPartialUpdateOfferModel(t *testing.T) {
	t.Log("partialUpdateOfferModel begin.")

	// ID of the offer model you want to update.
	modelId := "OfferTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateOfferModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the offer model.
	urlSegment := fmt.Sprintf("offer/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add offer model failed. %v", err)
	} else {
		t.Logf("Add offer model success, offer/model response: %v", resp)
	}
}

// Add messages to an offer model.
// Run the "createOfferModel" test before running this test.
// POST http://xxx/hmspass/v1/offer/model/{modelId}/addMessage
func TestAddMessageToOfferModel(t *testing.T) {
	// ID of the offer model you want to update.
	modelId := "OfferTestModel"

	// Create a list of messages you want to add to a model. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
	// and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
	// messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the offer model.
	urlSegment := fmt.Sprintf("offer/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to offer model failed. %v", err)
	} else {
		t.Logf("Update message to offer model success, response: %v", resp)
	}
}
