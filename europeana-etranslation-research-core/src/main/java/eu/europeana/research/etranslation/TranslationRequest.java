package eu.europeana.research.etranslation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TranslationRequest {
	String text;
	String sourceLanguage;
	Collection<String> targetLanguages;
	
	public TranslationRequest(String text, String sourceLanguage, Collection<String> targetLanguages) {
		super();
		this.text = text;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguages = targetLanguages;
	}
	public TranslationRequest(String text, String sourceLanguage, String... targetLanguages) {
		super();
		this.text = text;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguages = Arrays.asList(targetLanguages);
	}
	public TranslationRequest(JSONObject asJson) {
		text=asJson.getString("text");
		sourceLanguage=asJson.getString("sourceLanguage");
		JSONArray jTrgLangs = asJson.getJSONArray("targetLanguages");
		targetLanguages=new ArrayList<String>(jTrgLangs.length());
		for(int i=0; i<jTrgLangs.length(); i++) 
			targetLanguages.add(jTrgLangs.getString(i));
	}
	
	public String getText() {
		return text;
	}
	public String getSourceLanguage() {
		return sourceLanguage;
	}
	public Collection<String> getTargetLanguages() {
		return targetLanguages;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}
	public void setTargetLanguages(String... targetLanguages) {
		this.targetLanguages =  Arrays.asList(targetLanguages);
	}
	
	public JSONObject toJson() {
		return new JSONObject().put("text", text)
				.put("sourceLanguage", sourceLanguage)
				.put("targetLanguages", new JSONArray(targetLanguages));
	}
}
