package api

import "encoding/json"

// JSONToStruct unmarshal json string to v struct
func JSONToStruct(jsonString string, v interface{}) error {
	return json.Unmarshal([]byte(jsonString), v)
}

// JSONByteToStruct unmarshal json byte array to v struct
func JSONByteToStruct(jsonByte []byte, v interface{}) error {
	return json.Unmarshal(jsonByte, v)
}

// StructToJSON marshal v struct to json string
func StructToJSON(v interface{}) (string, error) {
	bytes, err := json.Marshal(v)
	return string(bytes), err
}
