package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Create a new event ticket model.
// Each event ticket model indicates a style of event ticket passes..
// POST http://XXX/hmspass/v1/eventticket/model
func TestCreateEventTicketModel(t *testing.T) {
	t.Log("createEventTicketModel begin.")

	// Read an event ticket model from a JSON file.
	dataPath, _ := filepath.Abs("../data/EventTicketModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new event ticket model to HMS wallet server.
	urlSegment := "eventticket/model"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add event ticket model failed. %v", err)
	} else {
		t.Logf("Add event ticket model success, eventticket/model response: %v", resp)
	}
}

// Get an event ticket model by its model ID.
// Run the "createEventTicketModel" test before running this test.
// GET http://xxx/hmspass/v1/eventticket/model/{modelId}
func TestGetEventTicketModel(t *testing.T) {
	t.Log("getEventTicketModel begin.")

	// ID of the event ticket model you want to get.
	modelId := "EventTicketTestModel"

	// Get the event ticket model.
	urlSegment := fmt.Sprintf("eventticket/model/%s", modelId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get event ticket model failed. %v", err)
	} else {
		t.Logf("Get event ticket model success, eventticket/model response: %v", resp)
	}
}

// Get event ticket models belonging to a specific appId.
// Run the "createEventTicketModel" test before running this test.
// GET http://xxx/hmspass/v1/eventticket/model?session=XXX&pageSize=XXX
func TestGetEventTicketModelList(t *testing.T) {
	t.Log("getEventTicketModelList begin.")

	// Get a list of event ticket models.
	urlSegment := "eventticket/model"
	api.GetModels(urlSegment, 5)
}

// Overwrite an event ticket model.
// Run the "createEventTicketModel" test before running this test.
// PUT http://xxx/hmspass/v1/eventticket/model/{modelId}
func TestFullUpdateEventTicketModel(t *testing.T) {
	t.Log("fullUpdateEventTicketModel begin.")

	// Read an event ticket model from a JSON file.
	dataPath, _ := filepath.Abs("../data/FullUpdateEventTicketModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var hwWalletObject model.HwWalletObject
	api.JSONByteToStruct(dataByte, &hwWalletObject)

	// Post the new event ticket model to HMS wallet server.
	urlSegment := fmt.Sprintf("eventticket/model/%s", hwWalletObject.PassStyleIdentifier)
	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Full update event ticket model failed. %v", err)
	} else {
		t.Logf("Full update ticket model success, response: %v", resp)
	}
}

// Update an event ticket model.
// Run the "createEventTicketModel" test before running this test.
// PATCH http://xxx/hmspass/v1/eventticket/model/{modelId}
func TestPartialUpdateEventTicketModel(t *testing.T) {
	t.Log("partialUpdateEventTicketModel begin.")

	// ID of the event ticket model you want to update.
	modelId := "EventTicketTestModel"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding model.
	dataPath, _ := filepath.Abs("../data/PartialUpdateEventTicketModel.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new event ticket model to HMS wallet server.
	urlSegment := fmt.Sprintf("eventticket/model/%s", modelId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Partial update event ticket model failed. %v", err)
	} else {
		t.Logf("Partial update event ticket model success, response: %v", resp)
	}
}

// Add messages to an event ticket model.
// Run the "createEventTicketModel" test before running this test.
// POST http://xxx/hmspass/v1/eventticket/model/{modelId}/addMessage
func TestAddMessageToEventTicketModel(t *testing.T) {
	// ID of the event ticket model you want to update.
	modelId := "EventTicketTestModel"

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the event ticket instance.
	urlSegment := fmt.Sprintf("eventticket/model/%s/addMessage", modelId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update message to event ticket model failed. %v", err)
	} else {
		t.Logf("Update message to event ticket model success, response: %v", resp)
	}
}
