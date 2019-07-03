package eu.europeana.research.etranslation.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.europeana.research.etranslation.TranslationRequest;

public class EtranslationRequestResultPair {
	public static class EtranslationError {
		public EtranslationError(String errorCode, String errorMessage) {
			super();
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		}
		public String errorCode;
		public String errorMessage;
	}
	
	public EtranslationRequestResultPair(TranslationRequest tReq, Exception e) {
		this.request=tReq;
		this.exception=ExceptionUtils.getStackTrace(e);;
	}
	public EtranslationRequestResultPair(TranslationRequest tReq, String errorCode, String requestId) {
		this.request=tReq;
		this.requestId=requestId;			
		this.errorCode=errorCode;			
		translations=new HashMap<String, String>();
		errors=new HashMap<String, EtranslationRequestResultPair.EtranslationError>();
	}
	public EtranslationRequestResultPair(File fromFile) throws IOException {
        String jsonString = FileUtils.readFileToString(fromFile, "UTF8");
		JSONObject j = new JSONObject(new JSONTokener(jsonString));
        
        request=new TranslationRequest(j.getJSONObject("translationRequest"));
        requestId=j.optString("requestId");
        errorCode=j.optString("errorCode");
        exception=j.optString("exception");
        {
	        JSONArray jTrls = j.optJSONArray("translations");
	        if(jTrls==null) 
	        	translations=new HashMap<String, String>();
	        else {
	        	translations=new HashMap<String, String>(jTrls.length());
	    		for(int i=0; i<jTrls.length(); i++) {
	    			JSONObject t = jTrls.getJSONObject(i);
	    			translations.put(t.getString("language"), t.getString("translatedText"));
	    		}
	        }        	
        }
        {
	        JSONArray jTrls = j.optJSONArray("callbackErrors");
	        if(jTrls==null) 
	        	errors=new HashMap<String, EtranslationRequestResultPair.EtranslationError>();
	        else {
	        	errors=new HashMap<String, EtranslationError>(jTrls.length());
	        	for(int i=0; i<jTrls.length(); i++) {
	        		JSONObject t = jTrls.getJSONObject(i);
	        		errors.put(t.getString("language"), new EtranslationError(t.getString("errorCode"), t.getString("errorMessage")));
	        	}
	        }        	
        }        	
	}
	
	public TranslationRequest request;
	public String requestId;
	public String errorCode;
	public String exception;
	//from assynchronous response
	public Map<String, String> translations;
	public Map<String, EtranslationError> errors;
	
	public void save(File resultsFolder) throws IOException {
		JSONObject j=new JSONObject();
		j.putOpt("translationRequest", request.toJson());
		j.putOpt("requestId", requestId);
		j.putOpt("errorCode", errorCode);
		j.putOpt("exception", exception);
		if(!translations.isEmpty()) {
			JSONArray jTrls = new JSONArray();
			j.put("translations", jTrls);
			for(Entry<String, String> t: translations.entrySet()) {
				jTrls. put(new JSONObject()
						.put("language", t.getKey()).put("translatedText", t.getValue()));
			}
		}
		if(!errors.isEmpty()) {
			JSONArray jErrors = new JSONArray();
			j.put("callbackErrors", jErrors);
			for(Entry<String, EtranslationError> e: errors.entrySet()) {
				jErrors. put(new JSONObject()
						.put("language", e.getKey()).
								put("errorCode", e.getValue().errorCode).
								put("errorMessage", e.getValue().errorMessage));
			}
		}
		String filename="etranslation-";
		if(!StringUtils.isEmpty(requestId))
			filename+="request-"+requestId;
		else
			filename+="error-"+new Date().getTime();
		FileUtils.write(new File(resultsFolder, filename), j.toString(2), "UTF8");
	}
	
	public void addTranslations(ArrayList<File> responseFiles) {
		for(File tFile: responseFiles) {
			String lang=tFile.getName().substring(tFile.getName().indexOf('_')+1, tFile.getName().indexOf('.'));
			try {
				translations.put(lang.toLowerCase(), FileUtils.readFileToString(tFile, "UTF8"));
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	public void addErrors(ArrayList<File> responseFiles) {
		for(File callbackFile: responseFiles) {
			String lang=callbackFile.getName().substring(callbackFile.getName().indexOf('_')+1, callbackFile.getName().indexOf('.'));
			try {
				String errorString = FileUtils.readFileToString(callbackFile, "UTF8");
				EtranslationError e=new EtranslationError(errorString.substring(0, errorString.indexOf('\n')), errorString.substring(errorString.indexOf('\n')+1, errorString.length()));
				errors.put(lang.toLowerCase(), e);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	public boolean isRespondedFully(HashSet<String> unsupportedLanguages) {
		return request.getTargetLanguages().size()<=translations.size()+errors.size()+unsupportedLanguages.size() || !StringUtils.isEmpty(errorCode);
	}
}