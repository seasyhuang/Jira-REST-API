package hate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
			"password"
	})
	public void testAPI(String username, String password) throws Exception {
		
		String apiKey = getAPIKey(username, password);
		
		String url = "https://jira.vasco.com/rest/api/2/";
		String ownerAuth = "Basic " + apiKey;

		// Create HTTP Client
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		// ------------------------------------------------------------------------------------------------------------------
		// Helper methods used here:
//		HttpPut httpRequest = addLabels(url);
		HttpPut httpRequest = removeLabels(url);
		
		// Specifying the headers for the request
		httpRequest.setHeader("Authorization", ownerAuth);
		httpRequest.setHeader("Content-type", "application/json");

		// Execute your request and catch response
		String httpPostRequestPackageCreateResponseJson = "Response creation.";
		
		try {
			HttpResponse httpPostRequestPackageCreateResponse = httpClient.execute(httpRequest);
			httpPostRequestPackageCreateResponseJson = EntityUtils.toString(httpPostRequestPackageCreateResponse.getEntity(), "UTF-8");
			
		} catch (Exception e) {
			httpPostRequestPackageCreateResponseJson = "No response.";
		}
		
//		JsonObject createPackageResponseJsonObject = Jsoner.deserialize(httpPostRequestPackageCreateResponseJson, new JsonObject());
		
		System.out.println(Jsoner.prettyPrint(httpPostRequestPackageCreateResponseJson));
		
//		String packageId = (String) createPackageResponseJsonObject.get("id");
//		
//		System.out.println("Package id is: " + packageId);
		
	}
	
	/*
	 * Usage:
	 * 
	 * HttpClient httpClient = HttpClientBuilder.create().build();
	 * [CALL addLabels() HERE]
	 * httpRequest.setHeader("Authorization", ownerAuth);
	 * httpRequest.setHeader("Content-type", "application/json");
	 * 
	 * Headers - Content-Type = application/json, Authorization = Basic [apiKey]
	 * This method generates the body.
	 */
	private static HttpPut addLabels(String url) throws UnsupportedEncodingException {
	
		HttpPut httpRequest = new HttpPut(url + "issue/PB-9655/");
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
		
		return httpRequest;
		
	}
	
	/*
	 * Usage:
	 * See Usage for addLabels()
	 */
	
	private static HttpPut removeLabels(String url) throws UnsupportedEncodingException {
		
		HttpPut httpRequest = new HttpPut(url + "issue/PB-9655/");
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
		
		return httpRequest;
		
	}
	
	private static String getAPIKey(String username, String password) {
		String plainCreds = username + ":" + password;
		byte[] base64CredsBytes = Base64.getEncoder().encode(plainCreds.getBytes());
		String key = new String(base64CredsBytes);
		return key;
	}
	
}

