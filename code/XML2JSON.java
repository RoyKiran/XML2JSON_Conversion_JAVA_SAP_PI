package com.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.json.XML;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class XML2JSON extends AbstractTransformation {

	private List<String> array_nodes = new ArrayList<String>();
	private List<String> hide_keys = new ArrayList<String>();
	private List<String> delete_entry = new ArrayList<String>();
	private int last_level_to_keep = 0;
	private boolean num_to_string = false;

	public XML2JSON() {

	}

	public XML2JSON(List<String> array_nodes, List<String> hide_keys,
			List<String> delete_entry, int last_level_to_keep, 
			boolean num_to_string) {
		/*
		 * Declare constructor to define the class-level variables Input: 
		 * 1) array_nodes -> List to store Keys that should be JSONArray 
		 * 2) hide_keys -> List to store Keys that should be Hidden 
		 * 3) delete_entry -> List to store Keys, whose records are to be deleted from the structure 
		 * 4) last_level_to_keep -> integer to store maximum level of JSON Structure to be retained
		 * 5) num_to_string -> boolean variable to set if numbers are to be converted in string format		 
		 */

		this.array_nodes = array_nodes;
		this.hide_keys = hide_keys;
		this.delete_entry = delete_entry;
		this.last_level_to_keep = last_level_to_keep;
		this.num_to_string = num_to_string;
	}

	public static void main(String[] args) {
		/*
		 * This method is for testing purpose only:
		 */

		try {

			// Set the input stream to read the XML Data
			InputStream input = new FileInputStream(new File("test4.xml"));

			// Set the output stream to store the JSON Data
			OutputStream output = new FileOutputStream(new File("out4.json"));

			// Set the Input Parameters
			// Test Input 1
			/*String[] array_nodes = { "ns0:root","node3" };			
			String[] hide_keys = { "node2" };
			String[] delete_entry = { "xmlns:ns0" };
			int last_level_to_keep = 2;
			*/
      
			// Test Input 2			
			/*String[] array_nodes = {" "};		
			String[] hide_keys = { "" };
			String[] delete_entry = { "xmlns:ns1" };
			int last_level_to_keep = 1;
			*/
			
			// Test Input 3	
			String[] array_nodes = {"Node3"};			
			String[] hide_keys = { "" };
			String[] delete_entry = { "xmlns:ns2" };
			int last_level_to_keep = 3;
			boolean num_to_string = true;
			
			// Instance of XML2JSON created and parameters passed to contructor
			XML2JSON obj = new XML2JSON(Arrays.asList(array_nodes), Arrays
					.asList(hide_keys), Arrays.asList(delete_entry),
					last_level_to_keep, num_to_string);

			// Instance of XML2JSON created and parameters passed to contructor
			obj.readStreamContent(input, output);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamTransformationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void transform(TransformationInput transformationInput,
			TransformationOutput transformationOutput)
			throws StreamTransformationException {

		/*
		 * This is the default method called by SAP PI Java mapping Input :
		 * InputStream and OutputStream objects
		 */

		// An info message is added to trace by calling the getTrace() method of
		// AbstractTransformation class
		getTrace().addInfo("JAVA Mapping to Convert XML to JSON Initiated");

		// Input Payload is obtained from transformationInput
		InputStream inputStream = transformationInput.getInputPayload()
				.getInputStream();

		// Input Parameters is obtained from transformationInput
		String array_nodes = transformationInput.getInputParameters()
				.getString("ARRAY_NODES");
		String hide_keys = transformationInput.getInputParameters().getString(
				"HIDE_KEYS");
		String delete_entry = transformationInput.getInputParameters()
				.getString("DELETE_ENTRY");
		int last_level_to_keep = transformationInput.getInputParameters()
				.getInt("LAST_LEVEL_TO_KEEP");
		boolean num_to_string = Boolean.parseBoolean(transformationInput.getInputParameters()
		.getString("NUM_TO_STRING"));

		// Display parameter value on trace
		getTrace().addInfo("Input Parameters listed below: \n");
		getTrace().addInfo("ARRAY_NODES : " + array_nodes);
		getTrace().addInfo("HIDE_KEYS: " + hide_keys);
		getTrace().addInfo("DELETE_ENTRY: " + delete_entry);
		getTrace().addInfo("LAST_LEVEL_TO_KEEP: " + last_level_to_keep);
		getTrace().addInfo("NUM_TO_STRING: " + num_to_string);
		
		// Output Payload Stream is obtained to send the data
		OutputStream outputStream = transformationOutput.getOutputPayload()
				.getOutputStream();
		
		// Convert Message Content type in Msg Header to application/json
		transformationOutput.getOutputHeader().setContentType("application/json");		

		// Instance of XML2JSON created and parameters passed to contructor
		XML2JSON obj = new XML2JSON(Arrays.asList(array_nodes.split(",")),
				Arrays.asList(hide_keys.split(",")), Arrays.asList(delete_entry
						.split(",")), last_level_to_keep, num_to_string);

		// Call readStreamContent() to Handle the input and output stream content
		try {
			obj.readStreamContent(inputStream, outputStream);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readStreamContent(InputStream inputStream,
			OutputStream outputStream) throws StreamTransformationException, UnsupportedEncodingException, IOException {

		/*
		 * This method Input : InputStream and OutputStream objects
		 */

		try {
			// Declare a String variable to store output json format
			String jsonPrettyPrintString = "";

			// If data is present in InputStream, it is stored in byte object
			byte[] buf = new byte[inputStream.available()];

			// Inputstream reads the data in byte format
			inputStream.read(buf);

			// A debug message is added to display the input XML
			if (getTrace() != null) {
				getTrace().addDebugMessage(
					"Input XML:\n" + new String(buf, "utf-8") + "\n ------");
			} else { // This section is added for normal JAVA Program run
				System.out.println(new String(buf, "utf-8"));
			}

			// Convert XML to JSON
			JSONObject xmlJsonObj = XML.toJSONObject(new String(buf, "utf-8"));			

			// Call handleJSONData() to parse the JSON Structure to Delete a
			// record or convert it to an array
			if (array_nodes.size() > 0 || delete_entry.size() > 0)
				xmlJsonObj = handleJSONData(xmlJsonObj);

			// Convert the Output JSONObject to String
			jsonPrettyPrintString = xmlJsonObj.toString(2);

			// Remove the levels if required
			if (last_level_to_keep > 0) {
				jsonPrettyPrintString = (String) deleteLevel(xmlJsonObj, 0);
			} 

			// Hide Key names if required
			for (String text : hide_keys) {
				jsonPrettyPrintString = jsonPrettyPrintString.replaceAll("\""+ text + "\":", "");
			}  

			// Print the Final JSON structure in Trace
			if (getTrace() != null) {
				getTrace().addDebugMessage("Output JSON:\n" + jsonPrettyPrintString + "\n ------");
			} else {
				System.out.println(jsonPrettyPrintString);
			}

		
			// Convert the Output JSON Structure to bytes
			if (jsonPrettyPrintString == null || jsonPrettyPrintString.length() < 1) {
				byte[] bytes = "{}".toString().getBytes("UTF-8");

				// Write output bytes to the output stream
				outputStream.write(bytes);
			}

			else {
				byte[] bytes = jsonPrettyPrintString.toString().getBytes("UTF-8");

				// Write output bytes to the output stream
				outputStream.write(bytes);
			}

		} catch (Exception e) {
			// Handle all exceptions
			if (getTrace() != null) {
				getTrace().addDebugMessage("Exception while writing OutputPayload: IOException", e);
				outputStream.write("{}".toString().getBytes("UTF-8"));
				throw new StreamTransformationException(e.toString());
				
			} else
				e.printStackTrace();
		}
	}

	public JSONObject handleJSONData(JSONObject jsonObj) {
		/*
		 * Parse the JSON Structure to Delete a record or convert it to an array. 
		 * Also convert the number to string format if required.
		 * Input: JSONObject -> Json Sub structure to be updated Output:
		 * JSONObject -> Updated Json Sub structure with deleted records and
		 * arrays.
		 */

		try {
			// Create an array of keyset to loop further
			String arr[] = new String[jsonObj.keySet().size()];
			int k = 0;
			for (String key : jsonObj.keySet())
				arr[k++] = key;

			// Loop through all the keys in a JSONObject
			for (String key : arr) {

				// If there are records to be deleted, remove them and move to next key
				if (delete_entry.contains(key)) {					
					jsonObj.remove(key);
					continue;
				}

				// If there are records to be converted to Array, convert it.
				if (array_nodes.contains(key)) {
					jsonObj = forceToJSONArray(jsonObj, key);
				}

				// If the sub node is a JSONArray or JSONObject, step inside the Object
				if (jsonObj.get(key) instanceof JSONArray) {
					JSONArray sjao = jsonObj.getJSONArray(key);
					for (int i = 0; i < sjao.length(); i++) {
						sjao.put(i, handleJSONData(sjao.getJSONObject(i)));
					}
					jsonObj.put(key, sjao);
				} else if (jsonObj.get(key) instanceof JSONObject) {
					jsonObj.put(key,handleJSONData(jsonObj.getJSONObject(key)));
				} else {
					// Convert number to String if num_to_string is set
					if (num_to_string){
						Object val = jsonObj.get(key);
						if(val instanceof Integer || val instanceof Float || val instanceof Double 
						   || val instanceof Long || val instanceof Short)
							jsonObj.put(key,jsonObj.get(key).toString());
				}
			}
		} catch (Exception e) {
			// Handle all exceptions
			if (getTrace() != null) {
				getTrace().addDebugMessage("Exception while Updating Payload: ", e);
			} else
				e.printStackTrace();
		}

		return jsonObj;
	}

	public static JSONObject forceToJSONArray(JSONObject jsonObj, String key)
			throws org.json.JSONException {
		/*
		 * Force Convert a record to JSON Array Input: 1) JSONObject -> JSON Sub
		 * structure to be updated 2) key -> Key whose value is to be converted
		 * to JSONArray Output: JSONObject -> Updated Json Sub structure with
		 * deleted records and arrays.
		 */

		// Get the key value from JSONObject using opt() and not get(), as it
		// can also return null value.
		Object obj = jsonObj.opt(key);

		// If the obj doesn't exist inside my the JsonObject structure, create it empty
		if (obj == null) {
			jsonObj.put(key, new JSONArray());
		}
		// if exist but is a JSONObject, force it to JSONArray
		else if (obj instanceof JSONObject) {
			JSONArray jsonArray = new JSONArray();
			jsonArray.put((JSONObject) obj);
			jsonObj.put(key, jsonArray);
		}
		// if exist but is a primitive entry, force it to a "primitive" JSONArray
		else if (obj instanceof String || obj instanceof Integer || obj instanceof Float 
			 || obj instanceof Double || obj instanceof Long || obj instanceof Boolean ) {
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(obj);
			jsonObj.put(key, jsonArray);
		}
		return jsonObj;
	}

	public Object deleteLevel(JSONObject jsonObj, int current_level) {
		/*
		 * Based on the last level to be maintained, this method recursively
		 * deletes all previous levels. Input: 1) JSONObject -> Stores the json
		 * structure to be edited 2) Index -> Stores the level count to remove
		 * and is incremented as we step deeper in the levels Output: Object ->
		 * Currently returns String or JSONObject.
		 */

		// Read the first key in the JSONObject Structure
		String node = new ArrayList<String>(jsonObj.keySet()).get(0);

		// Check if last level as per input is reached and return the output string
		if (current_level == last_level_to_keep - 1) {
			// If instance is JSONObject, remove the key and return the output string
			if (jsonObj.get(node) instanceof JSONObject) {
				return jsonObj.getJSONObject(node).toString(2);
			}
			// If instance is JSONArray, remove the key and return the output string
			else if (jsonObj.get(node) instanceof JSONArray) {
				return jsonObj.getJSONArray(node).toString(2);
			}

			// If both above cases fail, invalid level was provided and return empty string
			return "{}";
		}

		// If its not the last level step in deeper one level and pass Object and incremented level index
		else if (jsonObj.get(node) instanceof JSONObject) {
			return deleteLevel(jsonObj.getJSONObject(node), ++current_level);
		} else if (jsonObj.get(node) instanceof JSONArray) {
			return deleteLevel(jsonObj.getJSONArray(node).getJSONObject(0), ++current_level);
		}

		// If the object is no more an array or json object, invalid level was
		// provided and return empty string
		return "{}";
	}

}
