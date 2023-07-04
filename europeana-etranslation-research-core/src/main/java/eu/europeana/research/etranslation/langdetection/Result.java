package eu.europeana.research.etranslation.langdetection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Result {
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
	}
	
	List<DetectedLanguage> detected=new ArrayList<Result.DetectedLanguage>();

	public void add(String langCode, float confidence) {
		detected.add(new DetectedLanguage(langCode, confidence));
	}

	public List<DetectedLanguage> getDetected() {
		return Collections.unmodifiableList(detected);
	}
	
	
}