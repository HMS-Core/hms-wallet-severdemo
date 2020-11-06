package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new loyalty model.
// Each loyalty model indicates a style of loyalty passes.
// POST http://XXX/hmspass/v1/loyalty/model
func TestCreateLoyaltyModel(t *testing.T) {
	t.Log("createLoyaltyModel begin.")

	// Read an loyalty model from a JSON file.
	dataPath, _ := filepath.Abs("../data/LoyaltyModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new loyalty model to HMS wallet server.
	urlSegment := "loyalty/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add loyalty model failed. %v", err)
	} else {
		t.Logf("Add loyalty model success, eventticket/model response: %v", resp)
	}
}

// Get a loyalty model by its model ID.
// Run the "createLoyaltyModel" test before running this test.
// GET http://xxx/hmspass/v1/loyalty/model/{modelId}
func TestGetLoyaltyModel(t *testing.T) {
	t.Log("getLoyaltyModel begin.")

	// ID of the loyalty model you want to get.
	modelId := "LoyaltyTestModel"

	// Get the loyalty model.
	urlSegment := fmt.Sprintf("loyalty/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get loyalty model failed. %v", err)
	} else {
		t.Logf("Get loyalty model success, loyalty/model response: %v", resp)
	}
}

// Get loyalty models belonging to a specific appId.
// Run the "createLoyaltyModel" test before running this test.
// GET http://xxx/hmspass/v1/loyalty/model?session=XXX&pageSize=XXX
func TestGetLoyaltyModelList(t *testing.T) {
	t.Log("getLoyaltyModelList begin.")

	// Get a list of loyalty models.
	urlSegment := "loyalty/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite a loyalty model.
// Run the "createLoyaltyModel" test before running this test.
// PUT http://xxx/hmspass/v1/loyalty/model/{modelId}
func TestFullUpdateLoyaltyModel(t *testing.T) {
	t.Log("fullUpdateLoyaltyModel begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
	dataPath, _ := filepath.Abs("../data/FullUpdateLoyaltyModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var hwWalletObject model.HwWalletObject
	api.JSONByteToStruct(dataByte, &hwWalletObject)

	// Update the loyalty model.
	urlSegment := fmt.Sprintf("loyalty/model/%s", hwWalletObject.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Full update loyalty model failed. %v", err)
	} else {
		t.Logf("Full update loyalty model success, response: %v", resp)
	}
}

// Update a loyalty model.
// Run the "createLoyaltyModel" test before running this test.
// PATCH http://xxx/hmspass/v1/loyalty/model/{modelId}
func TestPartialUpdateLoyaltyModel(t *testing.T) {
	t.Log("partialUpdateLoyaltyModel begin.")

	// ID of the loyalty model you want to update.
	modelId := "LoyaltyTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateLoyaltyModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the loyalty model.
	urlSegment := fmt.Sprintf("loyalty/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Partial update loyalty model failed. %v", err)
	} else {
		t.Logf("Partial update loyalty model success, response: %v", resp)
	}
}

// Add messages to a loyalty model.
// Run the "createLoyaltyModel" test before running this test.
// POST http://xxx/hmspass/v1/loyalty/model/{modelId}/addMessage
func TestAddMessageToLoyaltyModel(t *testing.T) {
	// ID of the loyalty model you want to update.
	modelId := "LoyaltyTestModel"

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the loyalty instance.
	urlSegment := fmt.Sprintf("loyalty/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to loyalty model failed. %v", err)
	} else {
		t.Logf("Update message to loyalty model success, response: %v", resp)
	}
}
