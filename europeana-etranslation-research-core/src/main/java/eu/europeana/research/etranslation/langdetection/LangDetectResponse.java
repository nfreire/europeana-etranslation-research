package eu.europeana.research.etranslation.langdetection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.europeana.research.etranslation.langdetection.Result.DetectedLanguage;

public class LangDetectResponse {
	public class DetectedLanguage {
		String langCode;
		float confidence;
		public String getLangCode() {
			return langCode;
		}
		public void setLangCode(String langCode) {
			this.langCode = langCode;
		}
		public float getConfidence() {
			return confidence;
		}
		public void setConfidence(float confidence) {
			this.confidence = confidence;
		}
		public DetectedLanguage(String langCode, float confidence) {
			super();
			this.langCode = langCode;
			this.confidence = confidence;
		}
		@Override
		public String toString() {
			return "[langCode=" + langCode + ", confidence=" + confidence + "]";
		}
		
	}
	
	List<DetectedLanguage> detected=new ArrayList<DetectedLanguage>();

	public void add(String langCode, float confidence) {
		detected.add(new DetectedLanguage(langCode, confidence));
	}

	public List<DetectedLanguage> getDetected() {
		return Collections.unmodifiableList(detected);
	}

	public LangDetectResponse(String json) throws LanguageDetectionErrorCodeException {
		System.out.println(json);
			JSONObject j = new JSONObject(new JSONTokener(json));
			if(j.has("errorCode"))
				throw new LanguageDetectionErrorCodeException(j.getInt("errorCode"), j.get("errorMessage"));
			
			JSONArray results = j.getJSONArray("result");
//			targetLanguages=new ArrayList<String>(jTrgLangs.length());
			for(int i=0; i<results.length(); i++)  {
				JSONObject res = results.getJSONObject(i);
				DetectedLanguage l=new DetectedLanguage(res.getString("code").toLowerCase(), res.getFloat("probability")/100);
				detected.add(l);
			}
	}

	@Override
	public String toString() {
		return "LangDetectResponse [" + detected + "]";
	}
	
	
}
