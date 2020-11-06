package flight

import (
	"../../../src/api"
	"../../model"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"testing"
)

// Add a flight instance to HMS wallet server.
// Run the "createFlightModel" test before running this test.
// After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
// sending a JWE with complete instance information, without using this API. See JWE example tests.
// POST http://XXX/hmspass/v1/flight/instance
func TestAddFlightInstance(t *testing.T) {
	t.Log("addFlightInstance begin.")

	// Read a flight instance from a JSON file.
	dataPath, _ := filepath.Abs("../data/FlightInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Post the new flight instance to HMS wallet server.
	urlSegment := "flight/instance"
	resp, err := api.PostToWalletServer(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add flight instance failed. %v", err)
	} else {
		t.Logf("Add flight instance success, flight/instance response: %v", resp)
	}
}

// Get a flight instance by its instance ID.
// Run the "createFlightInstance" test before running this test.
// GET http://xxx/hmspass/v1/flight/instance/{instanceId}
func TestGetFlightInstance(t *testing.T) {
	t.Log("getFlightInstance begin.")

	// ID of the flight instance you want to get.
	instanceId := "FlightPass20001"

	// Get the flight instance.
	urlSegment := fmt.Sprintf("flight/instance/%s", instanceId)
	resp, err := api.GetHwWalletObjectById(urlSegment)
	if err != nil {
		t.Logf("Get flight instance failed. %v", err)
	} else {
		t.Logf("Get flight instance success, instanceId: %s, response: %v", instanceId, resp)
	}
}

// Get flight instance belonging to a specific flight model.
// Run the "createFlightInstance" test before running this test.
// GET http://xxx/hmspass/v1/flight/instance?modelId=XXX&session=XXX&pageSize=XXX
func TestGetFlightInstanceList(t *testing.T) {
	t.Log("getFlightInstanceList begin.")

	// Model ID of flight instances you want to get.
	modelId := "FlightTestModel"

	// Get a list of flight instances.
	urlSegment := "flight/instance"
	api.GetInstances(urlSegment, modelId, 5)
}

// Overwrite a flight instance.
// Run the "createFlightInstance" test before running this test.
// PUT http://xxx/hmspass/v1/flight/instance/{instanceId}
func TestFullUpdateFlightInstance(t *testing.T) {
	t.Log("fullUpdateFlightInstance begin.")

	// Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
	dataPath, _ := filepath.Abs("../data/FullUpdateFlightInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	var instance model.HwWalletObject
	api.JSONByteToStruct(dataByte, &instance)

	urlSegment := fmt.Sprintf("flight/instance/%s", instance.SerialNumber)

	resp, err := api.FullUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update flight instance failed. %v", err)
	} else {
		t.Logf("Update flight instance success, response: %v", resp)
	}
}

// Update a flight instance.
// Run the "createFlightInstance" test before running this test.
// PATCH http://xxx/hmspass/v1/flight/instance/{instanceId}
func TestPartialUpdateFlightInstance(t *testing.T) {
	t.Log("partialUpdateFlightInstance begin.")

	// ID of the flight instance you want to update.
	instanceId := "FlightPass20001"

	// Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
	dataPath, _ := filepath.Abs("../data/PartialUpdateFlightInstance.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	urlSegment := fmt.Sprintf("flight/instance/%s", instanceId)
	resp, err := api.PartialUpdateHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Update flight instance failed. %v", err)
	} else {
		t.Logf("Update flight instance success, response: %v", resp)
	}
}

// Add messages to a flight instance.
// Run the "createFlightInstance" test before running this test.
// POST http://xxx/hmspass/v1/flight/instance/{instanceId}/addMessage
func TestAddMessageToFlightInstance(t *testing.T) {
	t.Log("addMessageToFlightInstance begin.")

	// ID of the flight instance you want to update.
	instanceId := "FlightPass20001"

	// Create a list of messages you want to add to an instance. Each message contains key, value, and label.
	// The list should not contain multiple messages with the same key. You can update an existing message by adding
	// a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
	// messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
	// 10 messages at a time.

	// Read messages from a JSON file.
	dataPath, _ := filepath.Abs("../data/Messages.json")
	dataByte, _ := ioutil.ReadFile(dataPath)

	// Add messages to the event ticket instance.
	urlSegment := fmt.Sprintf("flight/instance/%s/addMessage", instanceId)
	resp, err := api.AddMessageToHwWalletObject(urlSegment, dataByte)
	if err != nil {
		t.Logf("Add message to flight instance failed. %v", err)
	} else {
		t.Logf("Add message to flight instance success, response: %v", resp)
	}
}
