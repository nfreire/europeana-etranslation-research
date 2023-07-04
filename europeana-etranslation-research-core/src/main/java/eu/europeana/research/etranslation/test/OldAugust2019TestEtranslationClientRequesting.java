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
import eu.europeana.research.etranslation.util.AccessException;

public class OldAugust2019TestEtranslationClientRequesting {

	
	public static void main(String[] args) {
//		runTestOnQuerySampleRequestSending();
		runTestRequestSending();
	}
	
	public static void runTestOnQuerySampleRequestSending() {
		try {
			File resultsFolder=new File("src/data/europeana-portal-query-sample_requests");
			if(!resultsFolder.exists())
				resultsFolder.mkdirs();
			
			EtranslationClient et=new EtranslationClient();
			
			CSVParser parser=new CSVParser(new FileReader(new File("src/data/europeana-portal-query-sample.csv")), CSVFormat.DEFAULT.withFirstRecordAsHeader());
			Iterator<CSVRecord> qIte = parser.iterator();
			HashSet<String> usedLanguages=new HashSet<String>();
			ArrayList<CSVRecord> queriesCsvs=new ArrayList<CSVRecord>();
			while(qIte.hasNext()) {
				CSVRecord q = qIte.next();
				queriesCsvs.add(q);
				if(!StringUtils.isEmpty(q.get(2)))
					usedLanguages.add(q.get(2));
			}
			parser.close();
			qIte = queriesCsvs.iterator();
			while(qIte.hasNext()) {
				CSVRecord q = qIte.next();
				if(!StringUtils.isEmpty(q.get(2)) && !StringUtils.isEmpty(q.get(0))) {
					HashSet<String> trgLangs=new HashSet<String>(usedLanguages);
					trgLangs.remove(q.get(2));
					TranslationRequest tReq = new TranslationRequest(q.get(0), q.get(2), trgLangs);
					EtranslationRequestResultPair etResp;
					try {
						String response = et.sendRequest(tReq);
						etResp = response.startsWith("-") ? 
								new EtranslationRequestResultPair(tReq, response, null)
								: new EtranslationRequestResultPair(tReq, null, response);
					} catch (IOException | AccessException e) {
						etResp = new EtranslationRequestResultPair(tReq, e);
					}
					etResp.save(resultsFolder);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runTestRequestSending() {
		try {
			EtranslationClient et=new EtranslationClient();
			String response = et.sendRequest(new TranslationRequest("Good morning, sir. Let's try this new service, please.", "EN", "FR", "DE"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}