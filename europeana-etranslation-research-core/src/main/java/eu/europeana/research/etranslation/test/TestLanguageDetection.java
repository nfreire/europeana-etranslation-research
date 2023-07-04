package eu.europeana.research.etranslation.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import eu.europeana.research.etranslation.EtranslationClient;
import eu.europeana.research.etranslation.TranslationRequest;
import eu.europeana.research.etranslation.langdetection.LangDetectResponse;
import eu.europeana.research.etranslation.langdetection.LanguageDetectionClient;
import eu.europeana.research.etranslation.util.AccessException;

public class TestLanguageDetection {
	
	

	public static void main(String[] args) {
		runTestOnQuerySampleRequestSending();
		
	}
	
	public static void runTestOnQuerySampleRequestSending() {
		try {
			
			LanguageDetectionClient et=new LanguageDetectionClient();
			
			
			String[] requests=new String[] {
				"Și nu-i nici Împăratul\nSă-mi cânte lumea ce va vrea\nMi-e draga una șî-i a mea\nDecât să mă despart de ea\nMai bine aprind tot satul.\n de la George Brici S.M.S. Bellona\n8. Florea Văleanului\nI.\nFoie verde fir de nalbă\nRăsari lună mai degrabă\nSă se vadă prin livadă\nSă cosesc pelin și earbă\nSă-i dau mândri să desfacă\nDesfă mândră ce-ai făcut\nCă ți-oi da cu un zlot mai mult.\nNu poci bade să-ți desfac\nCă din urmă ți-am luat\nȘi-n grădină am îngropat\nEu nu-ți-am făcut să per\nCă ți-am făcut să mă iei\nEa mă bade ia mă dragă\nCă de nu ma iei-s beteagă\nEa mă bade fătul meu\nDe nu mor de dorul tău.\nde la P. Țrunra" //bs
				, "Good morning, sir. Let's try this new service, please."
			};
			for(String reqParams: requests) {
				try {
					LangDetectResponse response = et.sendRequest(reqParams);
					System.out.println(response);
				} catch (IOException | AccessException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}