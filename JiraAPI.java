package hate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
		String issue = "PB-10190";
		String ownerAuth = "Basic " + apiKey;
		// ------------------------------------------------------------------------------------------------------------------

		removeLabels(url, issue, ownerAuth);
		
	}
	
	/* Usage:
	 * Returns the response JSON as a string.
	 * HttpClient httpClient = HttpClientBuilder.create().build();
	 * [CALL getIssue() HERE]
	 */
	private static String getIssue(String url, String issue, String ownerAuth) throws Exception{

		HttpClient httpClient = HttpClientBuilder.create().build();
	
		HttpGet httpRequest = new HttpGet(url + "issue/" + issue + "/");
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");
		
		HttpResponse response = httpClient.execute(httpRequest);		
		
		String responseJson = "Response creation.";
		try {
			responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(Jsoner.prettyPrint(responseJson));
		} catch (Exception e) {
			responseJson = "No response.";
			System.out.println(responseJson);
		}
		
		return responseJson;
	}
	
	/*
	 * Note that this method calls getIssue() first.
	 */
	private static ArrayList<String> getLabels(String url, String issue, String ownerAuth) throws Exception{
		
		String response = getIssue(url, issue, ownerAuth);
	
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
	 * See Usage for getLabels	 */
	private static String getName(String url, String issue, String ownerAuth) throws Exception{
		
		String response = getIssue(url, issue, ownerAuth);
	
		JSONObject responseJson =  new JSONObject(response);
		JSONObject jsonFields = responseJson.getJSONObject("fields");
		String summary = jsonFields.getString("summary");
		
		System.out.println("ISSUE NAME: \""+ summary + "\"");

		return summary;
	}
	
	/* Usage:
	 * See Usage for getLabels	 */
	private static String getIssueLinks(String url, String issue, String ownerAuth) throws Exception{

		String response = getIssue(url, issue, ownerAuth);
		
		JSONObject responseJson =  new JSONObject(response);
		JSONObject jsonFields = responseJson.getJSONObject("fields");
		JSONArray jsonIL = jsonFields.getJSONArray("issuelinks");
		JSONObject jsonOI = jsonIL.getJSONObject(0);
		JSONObject jsonOI2 = jsonOI.getJSONObject("outwardIssue");
		JSONObject jsonFields2 = jsonOI2.getJSONObject("fields");
		String summary = jsonFields2.getString("summary");
		
		System.out.println("ISSUE LINKS: \""+ summary + "\"");

		return summary;
	}
	
	/* Usage:
	 * PUT method
	 * Headers - Content-Type = application/json, Authorization = Basic [apiKey]
	 */
	private static String updateName(String url, String issue, String ownerAuth, String newName) throws Exception {
	
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPut httpRequest = new HttpPut(url + "issue/" + issue + "/");
		/* 
		 * { 
		 * 		"fields": 	{
		 * 			"summary": "[new summary]"
		 * 			} 
		 * }
		 */
		String packagePayloadString = "{ \r\n" + 
				"	\"fields\": 	{\r\n" + 
				"		\"summary\": \"" + newName + "\"\r\n" + 
				"	} \r\n" + 
				"}";
		StringEntity packagePayload = new StringEntity(packagePayloadString);
		httpRequest.setEntity(packagePayload);
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");
		
		HttpResponse response = httpClient.execute(httpRequest);	
		
		String responseJson = "Response creation.";
		try {
			responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(Jsoner.prettyPrint(responseJson));
		} catch (Exception e) {
			responseJson = "No response.";
			System.out.println(responseJson);
		}
		
		return responseJson;
	}
	
	/* Usage:
	 * PUT method
	 * Headers - Content-Type = application/json, Authorization = Basic [apiKey]
	 */
	private static String addLabels(String url, String issue, String ownerAuth) throws Exception {
		
		HttpClient httpClient = HttpClientBuilder.create().build();
	
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
		
		HttpResponse response = httpClient.execute(httpRequest);	
		
		String responseJson = "Response creation.";
		try {
			responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(Jsoner.prettyPrint(responseJson));
		} catch (Exception e) {
			responseJson = "No response.";
			System.out.println(responseJson);
		}
		
		return responseJson;
	}
	
	/* Usage:
	 * See Usage for addLabels()	 */
	private static String removeLabels(String url, String issue, String ownerAuth) throws Exception {

		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpPut httpRequest = new HttpPut(url + "issue/" + issue + "/");
		/* 
		 * This payload removes labels to the jira ticket. 
		 * { "remove": "label" }
		 * If you try to remove a label that doesn't exist, nothing happens.
		 */
		String packagePayloadString = "{\r\n" + 
				"	\"update\": {\r\n" + 
				"			\"labels\": [\r\n" + 
//				"				{ \"remove\": \"TESTING-JAVA\" },\r\n" + 
				"				{ \"remove\": \"newlabel\" }\r\n" + 
				"			]\r\n" + 
				"	}\r\n" + 
				"}";
		StringEntity packagePayload = new StringEntity(packagePayloadString);
		httpRequest.setEntity(packagePayload);
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");

		HttpResponse response = httpClient.execute(httpRequest);	
		
		String responseJson = "Response creation.";
		try {
			responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");
			System.out.println(Jsoner.prettyPrint(responseJson));
		} catch (Exception e) {
			responseJson = "No response.";
			System.out.println(responseJson);
		}
		
		return responseJson;
		
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

