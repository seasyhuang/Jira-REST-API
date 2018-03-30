package hate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class JiraAPI {
	
	public JiraAPI() {
	}

	@Test
	@Parameters ({
			"username",
			"password",
			"domain"
	})
	public void testAPI(String username, String password, String domain) throws Exception {
		
		String apiKey = getAPIKey(username, password);
		
		String url = "https://jira." + domain +".com/rest/api/2/";
		String ownerAuth = "Basic " + apiKey;

		// Create HTTP Client
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		// ------------------------------------------------------------------------------------------------------------------
		// Helper methods used here:
		HttpGet httpRequest = getIssue(url, "PB-9655", ownerAuth);
//		HttpPut httpRequest = addLabels(url, "PB-9655", ownerAuth);
//		HttpPut httpRequest = removeLabels(url, "PB-9655", ownerAuth);

		// Execute your request and catch response
		String responseJson = "Response creation.";
		
		try {
			HttpResponse response = httpClient.execute(httpRequest);
			responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(Jsoner.prettyPrint(responseJson));
		} catch (Exception e) {
			responseJson = "No response.";
			System.out.println(responseJson);
		}
		
		getLabels(responseJson);
		
	}
	
	/* Usage:
	 * 
	 * HttpClient httpClient = HttpClientBuilder.create().build();
	 * [CALL getIssue() HERE]
	 */
	private static HttpGet getIssue(String url, String issue, String ownerAuth){
	
		HttpGet httpRequest = new HttpGet(url + "issue/" + issue + "/");
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");
		
		return httpRequest;
		
	}
	
	/* Usage:
	 * -- Must be used after getIssue() is called.
	 * getIssue()
	 * try {...} catch {...}
	 * [CALL getLabels() HERE]
	 */
	private static ArrayList<String> getLabels(String response){
	
		JSONObject responseJson =  new JSONObject(response);
		JSONObject jsonFields = responseJson.getJSONObject("fields");
		JSONArray jsonLabels = jsonFields.getJSONArray("labels");
		
		// This converts the JSONArray of labels to a String ArrayList
		ArrayList<String> labels = new ArrayList<String>();
		for(int i = 0; i < jsonLabels.length(); i++){
			labels.add(jsonLabels.getString(i));
		}

		// Printing out the ArrayList
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : labels) {
			i++;
		    sb.append(s);
		    if (i != labels.size()) { sb.append(", "); }
		}
		System.out.println("LABELS: "+ sb.toString());

		return labels;
	}
	
	
	/* Usage:
	 * 
	 * HttpClient httpClient = HttpClientBuilder.create().build();
	 * [CALL addLabels() HERE]
	 * httpRequest.setHeader("Authorization", ownerAuth);
	 * httpRequest.setHeader("Content-type", "application/json");
	 * 
	 * Headers - Content-Type = application/json, Authorization = Basic [apiKey]
	 * This method generates the body.
	 */
	private static HttpPut addLabels(String url, String issue, String ownerAuth) throws UnsupportedEncodingException {
	
		HttpPut httpRequest = new HttpPut(url + "issue/" + issue + "/");
		/* 
		 * This payload adds labels to the jira ticket. 
		 * { "add": "label" }
		 */
		String packagePayloadString = "{\r\n" + 
				"	\"update\": {\r\n" + 
				"			\"labels\": [\r\n" + 
				"				{ \"add\": \"TESTING-JAVA\" },\r\n" + 
				"				{ \"add\": \"is_cool2\" }\r\n" + 
				"			]\r\n" + 
				"	}\r\n" + 
				"}";
		StringEntity packagePayload = new StringEntity(packagePayloadString);
		httpRequest.setEntity(packagePayload);
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");
		
		return httpRequest;
		
	}
	
	/* Usage:
	 * See Usage for addLabels()
	 */
	
	private static HttpPut removeLabels(String url, String issue, String ownerAuth) throws UnsupportedEncodingException {
		
		HttpPut httpRequest = new HttpPut(url + "issue/" + issue + "/");
		/* 
		 * This payload removes labels to the jira ticket. 
		 * { "remove": "label" }
		 * If you try to remove a label that doesn't exist, nothing happens.
		 */
		String packagePayloadString = "{\r\n" + 
				"	\"update\": {\r\n" + 
				"			\"labels\": [\r\n" + 
				"				{ \"remove\": \"TESTING-JAVA\" },\r\n" + 
				"				{ \"remove\": \"is_cool2\" }\r\n" + 
				"			]\r\n" + 
				"	}\r\n" + 
				"}";
		StringEntity packagePayload = new StringEntity(packagePayloadString);
		httpRequest.setEntity(packagePayload);
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");
		
		return httpRequest;
		
	}
	
	/*
	 * This method generates the API key for Jira by 
	 * base64 encoding your username and password.
	 */
	
	private static String getAPIKey(String username, String password) {
		String plainCreds = username + ":" + password;
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCreds.getBytes());
		String key = new String(base64CredsBytes);
		return key;
	}
	
}

