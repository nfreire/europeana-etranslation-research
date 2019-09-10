package eu.europeana.research.etranslation;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import eu.europeana.research.etranslation.util.AccessException;
import eu.europeana.research.etranslation.util.http.HttpRequest;
import eu.europeana.research.etranslation.util.http.HttpRequestService;
import eu.europeana.research.etranslation.util.http.UrlRequest;
import eu.europeana.research.etranslation.util.http.UrlRequest.HttpMethod;

public class EtranslationClient {
	HttpRequestService httpService;
	String url;
	String callbackUrl;
	String errorUrl;
	String userName;
	
	public EtranslationClient() {
			Properties prop=readProperties();
			userName = prop.getProperty("etranslation.username");
			File credentials = new File(prop.getProperty("etranslation.credentials"));

			url = prop.getProperty("etranslation.url");
			callbackUrl = prop.getProperty("etranslation.europeana.callback.url");
			errorUrl = prop.getProperty("etranslation.europeana.error.url");
			
			httpService=new HttpRequestService();
			httpService.init(userName, credentials);
	}
	
	public String sendRequest(TranslationRequest req) throws InterruptedException, IOException, AccessException {
		try {
			EtranslationApiRequest eTransReq=new EtranslationApiRequest(this, userName, req);
			UrlRequest urlRequestSettings = new UrlRequest(url, HttpMethod.POST);
			HttpEntity entity = new ByteArrayEntity(eTransReq.getJsonString().getBytes("UTF-8"), ContentType.APPLICATION_JSON);
			urlRequestSettings.setRequestContent(entity);
			HttpRequest httpReq= new HttpRequest(urlRequestSettings);
			httpService.fetch(httpReq);
			if(httpReq.getResponseStatusCode()!=200)
				throw new AccessException(url, httpReq.getResponseStatusCode(), httpReq.getContent().asString());
			return httpReq.getContent().asString();
		} catch (UnsupportedEncodingException e) { throw new RuntimeException(e.getMessage(), e); }
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public static Properties readProperties() {
		Properties prop=new Properties();
		try {
			prop.load(EtranslationClient.class.getClassLoader().getResourceAsStream("etranslation-api.properties"));
			return prop;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}