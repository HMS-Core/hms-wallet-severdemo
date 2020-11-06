package flight

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new flight model.
// Each flight model indicates a style of flight passes.
// POST http://XXX/hmspass/v1/flight/model
func TestCreateFlightModel(t *testing.T) {
	t.Log("createFlightModel begin.")

	// Read a flight model from a JSON file.
	dataPath, _ := filepath.Abs("../data/FlightModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new flight model to HMS wallet server.
	urlSegment := "flight/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add flight model failed. %v", err)
	} else {
		t.Logf("Add flight model success, flight/model response: %v", resp)
	}
}

// Get a flight model by its model ID.
// Run the "createFlightModel" test before running this test.
// GET http://xxx/hmspass/v1/flight/model/{modelId}
func TestGetFlightModel(t *testing.T) {
	t.Log("getFlightModel begin.")

	// ID of the flight model you want to get.
	modelId := "FlightTestModel"

	// Get the flight model.
	urlSegment := fmt.Sprintf("flight/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get flight model failed. %v", err)
	} else {
		t.Logf("Get flight model success, eventticket/model response: %v", resp)
	}
}

// Get flight models belonging to a specific appId.
// Run the "createFlightModel" test before running this test.
// GET http://xxx/hmspass/v1/flight/model?session=XXX&pageSize=XXX
func TestGetFlightModelList(t *testing.T) {
	t.Log("getFlightModelList begin.")

	// Get a list of flight models.
	urlSegment := "flight/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite a flight model.
// Run the "createFlightModel" test before running this test.
// PUT http://xxx/hmspass/v1/flight/model/{modelId}
func TestFullUpdateFlightModel(t *testing.T) {
	t.Log("fullUpdateFlightModel begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding model.
	dataPath, _ := filepath.Abs("../data/FullUpdateFlightModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var model model.HwWalletObject
	api.JSONByteToStruct(dataByte, &model)

	// Update the flight model.
	urlSegment := fmt.Sprintf("flight/model/%s", model.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Full update flight model failed. %v", err)
	} else {
		t.Logf("Full update flight model success, response: %v", resp)
	}
}
// Update a flight model.
// Run the "createFlightModel" test before running this test.
// PATCH http://xxx/hmspass/v1/flight/model/{modelId}
func TestPartialUpdateFlightModel(t *testing.T) {
	t.Log("partialUpdateFlightModel begin.")

	// ID of the flight model you want to update.
	modelId := "FlightTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateFlightModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Update the flight model.
	urlSegment := fmt.Sprintf("flight/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Partial update flight model failed. %v", err)
	} else {
		t.Logf("Partial update flight model success, response: %v", resp)
	}
}

// Add messages to a flight model.
// Run the "createFlightModel" test before running this test.
// POST http://xxx/hmspass/v1/flight/model/{modelId}/addMessage
func TestAddMessageToFlightModel(t *testing.T) {
	// ID of the flight model you want to update.
	modelId := "FlightTestModel"

	// Create a list of messages you want to add to a model. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One model contains at most 10 messages. If a model already have 10 messages
	// and you keep adding new messages, the oldest messages will be removed. You should not add more than 10
	// messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the flight model.
	urlSegment := fmt.Sprintf("flight/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to flight model failed. %v", err)
	} else {
		t.Logf("Update message to flight model success, response: %v", resp)
	}
}
