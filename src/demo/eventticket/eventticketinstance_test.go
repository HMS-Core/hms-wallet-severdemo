package eventticket

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)


// Add an event ticket instance to HMS wallet server.
// Run the "createEventTicketModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/eventticket/instance
func TestAddEventTicketInstance(t *testing.T) {
	t.Log("addEventTicketInstance begin.")

	// Read an event ticket instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/EventTicketInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new event ticket instance to HMS wallet server.
	urlSegment := "eventticket/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add event ticket instance failed. %v", err)
	} else {
		t.Logf("Add event ticket instance success, eventticket/instance response: %v", resp)
	}
}

// Get an event ticket instance by its instance ID.
// Run the "createEventTicketInstance" test before running this test.
// GET http://xxx/hmspass/v1/eventticket/instance/{instanceId}
func TestGetEventTicketInstance(t *testing.T) {
	t.Log("getEventTicketInstance begin.")

	// ID of the event ticket instance you want to get.
	instanceId := "EventTicketPass10001"

	// Get the event ticket instance.
	urlSegment := fmt.Sprintf("eventticket/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get event ticket instance failed. %v", err)
	} else {
		t.Logf("Get event ticket instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get event ticket instances belonging to a specific event ticket model.
// Run the "createEventTicketInstance" test before running this test.
// GET http://xxx/hmspass/v1/eventticket/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetEventTicketInstanceList(t *testing.T) {
	t.Log("getEventTicketInstanceList begin.")

	// Model ID of event ticket instances you want to get.
	modelId := "EventTicketTestModel"

	// Get the event ticket instance.
	urlSegment := "eventticket/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Update an event ticket instance.
// Run the "createEventTicketInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/eventticket/instance/{instanceId}
func TestFullUpdateEventTicketInstance(t *testing.T) {
	t.Log("fullUpdateEventTicketInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateEventTicketInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	urlSegment := fmt.Sprintf("eventticket/instance/%s", instance.SerialNumber)

	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update event ticket instance failed. %v", err)
	} else {
		t.Logf("Update event ticket instance success, response: %v", resp)
	}
}

// Update an event ticket instance.
// Run the "createEventTicketInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/eventticket/instance/{instanceId}
func TestPartialUpdateEventTicketInstance(t *testing.T) {
	t.Log("partialUpdateEventTicketInstance begin.")

	// ID of the event ticket instance you want to update.
	instanceId := "EventTicketPass10001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateEventTicketInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	urlSegment := fmt.Sprintf("eventticket/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update event ticket instance failed. %v", err)
	} else {
		t.Logf("Update event ticket instance success, response: %v", resp)
	}
}

// Add messages to an event ticket instance.
// Run the "createEventTicketInstance" test before running this test.
// POST http://xxx/hmspass/v1/eventticket/instance/{instanceId}/addMessage
func TestAddMessageToEventTicketInstance(t *testing.T) {
	t.Log("addMessageToEventTicketInstance begin.")

	// ID of the event ticket instance you want to update.
	instanceId := "EventTicketPass10001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the event ticket instance.
	urlSegment := fmt.Sprintf("eventticket/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to event ticket instance failed. %v", err)
	} else {
		t.Logf("Add message to event ticket instance success, response: %v", resp)
	}
}
