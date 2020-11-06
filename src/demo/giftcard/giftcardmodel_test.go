package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new gift card model.
// Each gift card model indicates a style of gift card passes.
// POST http://XXX/hmspass/v1/giftcard/model
func TestCreateGiftCardModel(t *testing.T) {
	t.Log("createGiftCardModel begin.")

	// Read a gift card model from a JSON file.
	dataPath, _ := filepath.Abs("../data/GiftCardModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new event ticket model to HMS wallet server.
	urlSegment := "giftcard/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add gift card model failed. %v", err)
	} else {
		t.Logf("Add gift card model success, giftcard/model response: %v", resp)
	}
}

// Get a gift card model by its model ID.
// Run the "createGiftCardModel" test before running this test.
// GET http://xxx/hmspass/v1/giftcard/model/{modelId}
func TestGetGiftCardModel(t *testing.T) {
	t.Log("getGiftCardModel begin.")

	// ID of the gift card model you want to get.
	modelId := "GiftCardTestModel"

	// Get the gift card model.
	urlSegment := fmt.Sprintf("giftcard/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get gift card model failed. %v", err)
	} else {
		t.Logf("Get gift card model success, giftcard/model response: %v", resp)
	}
}

// Get gift card models belonging to a specific appId.
// Run the "createGiftCardModel" test before running this test.
// GET http://xxx/hmspass/v1/giftcard/model?session=XXX&pageSize=XXX
func TestGetGiftCardModelList(t *testing.T) {
	t.Log("getGiftCardModelList begin.")

	// Get a list of gift card models.
	urlSegment := "giftcard/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite a gift card model.
// Run the "createGiftCardModel" test before running this test.
// PUT http://xxx/hmspass/v1/giftcard/model/{modelId}
func TestFullUpdateGiftCardModel(t *testing.T) {
	t.Log("fullUpdateGiftCardModel begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
	dataPath, _ := filepath.Abs("../data/FullUpdateGiftCardModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var hwWalletObject model.HwWalletObject
	api.JSONByteToStruct(dataByte, &hwWalletObject)

	// Update the gift card model.
	urlSegment := fmt.Sprintf("giftcard/model/%s", hwWalletObject.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Full update gift card model failed. %v", err)
	} else {
		t.Logf("Full update gift card model success, response: %v", resp)
	}
}

// Update a gift card model.
// Run the "createGiftCardModel" test before running this test.
// PATCH http://xxx/hmspass/v1/giftcard/model/{modelId}
func TestPartialUpdateGiftCardModel(t *testing.T) {
	t.Log("partialUpdateGiftCardModel begin.")

	// ID of the gift card model you want to update.
	modelId := "GiftCardTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateGiftCardModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the gift card model.
	urlSegment := fmt.Sprintf("giftcard/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Partial update gift card model failed. %v", err)
	} else {
		t.Logf("Partial update gift card model success, response: %v", resp)
	}
}

// Add messages to a gift card model.
// Run the "createGiftCardModel" test before running this test.
// POST http://xxx/hmspass/v1/giftcard/model/{modelId}/addMessage
func TestAddMessageToGiftCardModel(t *testing.T) {
	// ID of the gift card model you want to update.
	modelId := "GiftCardTestModel"

	// Create a list of messages you want to add to a model. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
	// and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
	// messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the gift card instance.
	urlSegment := fmt.Sprintf("giftcard/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to gift card model failed. %v", err)
	} else {
		t.Logf("Update message to gift card model success, response: %v", resp)
	}
}
