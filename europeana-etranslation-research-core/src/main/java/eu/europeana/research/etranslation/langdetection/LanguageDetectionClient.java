package eu.europeana.research.etranslation.langdetection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.europeana.research.etranslation.util.AccessException;
import eu.europeana.research.etranslation.util.http.HttpRequest;
import eu.europeana.research.etranslation.util.http.HttpRequestService;
import eu.europeana.research.etranslation.util.http.UrlRequest;
import eu.europeana.research.etranslation.util.http.UrlRequest.HttpMethod;

public class LanguageDetectionClient {
	HttpRequestService httpService;
	String url;
	String application;
	String password;
	
	public LanguageDetectionClient() {
			try {
				Properties prop=readProperties();
				application = prop.getProperty("etranslation.username");
				
				File credentials = new File(prop.getProperty("etranslation.credentials"));
				password=FileUtils.readFileToString(credentials, "UTF-8");
				
				url = prop.getProperty("etranslation.language.detection.url");
				
				httpService=new HttpRequestService();
				httpService.init(application, credentials);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
	}
	
	public LangDetectResponse sendRequest(String text) throws InterruptedException, IOException, AccessException, LanguageDetectionErrorCodeException {
		try {
			UrlRequest urlRequestSettings = new UrlRequest(url, HttpMethod.POST);
			JSONObject requestJson = getRequestJson();
			System.out.println(requestJson.toString());
			HttpEntity multiPartEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//					.addTextBody("docfile", text, ContentType.TEXT_PLAIN)
					.addBinaryBody("docfile", text.getBytes(StandardCharsets.UTF_8), ContentType.TEXT_PLAIN, "text.txt")
					.addTextBody("metadata", requestJson.toString(), ContentType.APPLICATION_JSON).build();
			urlRequestSettings.setRequestContent(multiPartEntity);
			HttpRequest httpReq= new HttpRequest(urlRequestSettings);
			httpService.fetch(httpReq);
			String response = httpReq.getContent().asString();
			if(httpReq.getResponseStatusCode()!=200)
				throw new AccessException(url, httpReq.getResponseStatusCode(), response);
			return new LangDetectResponse(response);
		} catch (UnsupportedEncodingException e) { throw new RuntimeException(e.getMessage(), e); }
	}

	public static Properties readProperties() {
		Properties prop=new Properties();
		try {
			prop.load(LanguageDetectionClient.class.getClassLoader().getResourceAsStream("etranslation-language-detection-api.properties"));
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	private JSONObject getRequestJson() {
		JSONObject credentialsObj=new JSONObject().put("application", application).put("password", password);
		return new JSONObject().put("credentials", credentialsObj)
				.put("format", "txt")
				.put("verbose", "true");
	}
	
}