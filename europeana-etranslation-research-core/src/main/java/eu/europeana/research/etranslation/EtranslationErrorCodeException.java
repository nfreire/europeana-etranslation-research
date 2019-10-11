package eu.europeana.research.etranslation;

public class EtranslationErrorCodeException extends Exception {
	String errorCode;
	
	public EtranslationErrorCodeException(String errorCode) {
		super();
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		return "eTranslation error code: "+errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
}
