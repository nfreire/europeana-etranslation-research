package eu.europeana.research.etranslation.langdetection;

public class LanguageDetectionErrorCodeException extends Exception {
	int errorCode;
	
	public LanguageDetectionErrorCodeException(int i, Object object) {
		super(i+" - "+object);
		this.errorCode = i;
	}

	public int getErrorCode() {
		return errorCode;
	}
	
}
