package eu.europeana.research.etranslation;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class EtranslationApiRequest extends TranslationRequest {
	String externalReference;
	String application;
	EtranslationClient etranslationClient;
	
	public EtranslationApiRequest(EtranslationClient etranslationClient, String application, String text, String sourceLanguage, Collection<String> targetLanguages) {
		super(text, sourceLanguage, targetLanguages);
		this.application = application;
		this.etranslationClient = etranslationClient;;
	}
	public EtranslationApiRequest(EtranslationClient etranslationClient, String application, String text, String sourceLanguage, String... targetLanguages) {
		super(text, sourceLanguage, targetLanguages);
		this.application = application;
		this.etranslationClient = etranslationClient;;
	}

	public EtranslationApiRequest(EtranslationClient etranslationClient, String application, TranslationRequest req) {
		this(etranslationClient, application, req.getText(), req.getSourceLanguage(), req.getTargetLanguages());
	}
	
	public EtranslationApiRequest(EtranslationClient etranslationClient, String application, EtranslationApiRequest req) {
		this(etranslationClient, application, req.getText(), req.getSourceLanguage(), req.getTargetLanguages());
		this.setExternalReference(req.getExternalReference());
	}
	
	public JSONObject getJson() {
		JSONObject j=new JSONObject().put("priority", 1)
		.put("callerInformation", new JSONObject()
				.put("application", application).put("username", application));
		if(! StringUtils.isEmpty(externalReference))
			j.put("externalReference", externalReference);
		else
			j.put("externalReference", generateExternalReference());
		j.put("textToTranslate", text).put("sourceLanguage", sourceLanguage);
//		new JSONArray(targetLanguages)
		
		j.put("targetLanguages", new JSONArray(targetLanguages));
		
		//the domain is optional since for Europeana the default is applicable
//			.put("domain", "SPD");
		
		j.put("requesterCallback", etranslationClient.getCallbackUrl());
		j.put("errorCallback", etranslationClient.getErrorUrl());
		
		//destination is optional for text snippet requequest
		//		j.put("destinations",  new JSONObject().put("emailDestinations", new JSONArray().put("nuno.freire@europeana.eu")));
//"destinations" : {
//"httpDestinations" : ["http://my-client-server/my-client-context/"],
//"ftpDestinations" : ["ftp://user:password@ftp_server_name:2021/mycallback/"],
//"sftpDestinations" : ["sftp://user:password@sftp_server_name:2021/mycallback/"],
//"emailDestinations" : ["toto@ec.europa.eu"], j)
		
		return j;
	}
	
	public String getJsonString() {
		return getJson().toString();
	}

	public String getExternalReference() {
		return externalReference;
	}
	
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	
	static Long lastExternalReferenceTime=new Date().getTime();
	private synchronized String generateExternalReference() {
		long time = 0;
		synchronized (lastExternalReferenceTime) {
			do {
				time = new Date().getTime();
			} while(time==lastExternalReferenceTime);
			lastExternalReferenceTime=time;
		}
		String extRef=String.valueOf(time);
		for(String l: targetLanguages) {
			extRef+="-"+l;
		}
		return extRef;
	}
}
