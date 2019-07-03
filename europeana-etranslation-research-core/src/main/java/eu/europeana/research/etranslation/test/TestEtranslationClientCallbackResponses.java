package eu.europeana.research.etranslation.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import eu.europeana.research.etranslation.EtranslationClient;
import eu.europeana.research.etranslation.TranslationRequest;
import inescid.dataaggregation.crawl.http.HttpRequestService;
import inescid.util.AccessException;
import inescid.util.MapOfInts;

public class TestEtranslationClientCallbackResponses {
	
	static class TestReport {
		int totalRequests=0;
		boolean fullyRespoded=false;
		MapOfInts<String> errorsOnRequest=new MapOfInts<String>();
		MapOfInts<String> errorsOnCallback=new MapOfInts<String>();
		HashSet<String> unsupportedLanguages=new HashSet<String>();
		HashSet<String> supportedLanguages=new HashSet<String>();
		
		@Override
		public String toString() {
			StringBuilder sb=new StringBuilder();
			sb.append("Total requests: ").append(totalRequests).append("\n");
			sb.append("errorsOnRequest:\n");
			for(String k: errorsOnRequest.keySet())
				sb.append(k).append(" - ").append(errorsOnRequest.get(k)).append("\n");
			sb.append("Unsupported languages:\n");
			ArrayList<String> unsupportedLanguagesList=new ArrayList<String>(unsupportedLanguages);
			Collections.sort(unsupportedLanguagesList);
			for(String k: unsupportedLanguagesList)
				sb.append(k).append("\n");
			sb.append("Supported languages:\n");
			for(String k: getSupportedLanguagesList())
				sb.append(k).append("\n");
			sb.append("errorsOnCallback:\n");
			for(String k: errorsOnCallback.keySet())
				sb.append(k).append(" - ").append(errorsOnCallback.get(k)).append("\n");
			if(fullyRespoded)
				sb.append("All requests are responded\n");
			else
				sb.append("Some requests are unresponded");
			return sb.toString();
		}

		public List<String> getSupportedLanguagesList() {
			ArrayList<String> supportedLanguagesList=new ArrayList<String>(supportedLanguages);
			Collections.sort(supportedLanguagesList);
			return supportedLanguagesList;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Parameters: String requestsFolder");
		String callbackFolder=EtranslationClient.readProperties().getProperty("translations.folder");
		
		System.out.println("requests folder: "+args[0]);
		System.out.println("callback folder: "+callbackFolder);
		runTestOnQuerySampleAsyncResponses(args[0], callbackFolder);
	}
	
	public static void runTestOnQuerySampleAsyncResponses(String requestsFolder, String callbackFolderPath) {
		try {
			TestReport report=new TestReport();
			HashMap<String, ArrayList<File>> respondedRequests=new HashMap<>();
			HashMap<String, ArrayList<File>> respondedRequestsErrors=new HashMap<>();
			File callbackFolder = new File(callbackFolderPath);
			for(File reqFile : callbackFolder.listFiles()) {
				Pattern requestIdPrefixPattern=Pattern.compile("(\\d+)_.*\\.txt");
				Matcher matcher = requestIdPrefixPattern.matcher(reqFile.getName());
				if(matcher.matches()) {
					ArrayList<File> responses = respondedRequests.get(matcher.group(1));
					if(responses==null) {
						responses=new ArrayList<File>();
						respondedRequests.put(matcher.group(1), responses);
					}
					responses.add(reqFile);	
				}
			}
			for(File reqFile : new File(callbackFolder, "errors").listFiles()) {
				Pattern requestIdPrefixPattern=Pattern.compile("(\\d+)_.*\\.txt");
				Matcher matcher = requestIdPrefixPattern.matcher(reqFile.getName());
				if(matcher.matches()) {
					ArrayList<File> responses = respondedRequestsErrors.get(matcher.group(1));
					if(responses==null) {
						responses=new ArrayList<File>();
						respondedRequestsErrors.put(matcher.group(1), responses);
					}
					responses.add(reqFile);	
				}
			}
			
			File[] reqFiles = new File(requestsFolder).listFiles();
			for(File reqFile : reqFiles) {
				EtranslationRequestResultPair req=new EtranslationRequestResultPair(reqFile);
				String requestId=req.requestId;
				if(StringUtils.isEmpty(requestId)) {
					if(req.errorCode.equals("-20001"))
						report.unsupportedLanguages.add(req.request.getSourceLanguage());
				} else if(respondedRequests.containsKey(requestId) || respondedRequestsErrors.containsKey(requestId)) {
					report.supportedLanguages.add(req.request.getSourceLanguage());
				}	
			}
			List<String> supportedLanguages=report.getSupportedLanguagesList();
			
			StringBuilder csvText=new StringBuilder();
			CSVPrinter csvOut=new CSVPrinter(csvText, CSVFormat.DEFAULT);
			csvOut.print("Search term");
			csvOut.print("Source language");
			for(String lang: supportedLanguages)
				csvOut.print("to: "+lang);
			csvOut.println();

			int completedRequests=0;
			report.totalRequests=reqFiles.length;
			for(File reqFile : reqFiles) {
				EtranslationRequestResultPair req=new EtranslationRequestResultPair(reqFile);
				String requestId=req.requestId;
				if(StringUtils.isEmpty(requestId)) {
					report.errorsOnRequest.incrementTo(req.errorCode);
				} else if(respondedRequests.containsKey(requestId)) {
					report.supportedLanguages.add(req.request.getSourceLanguage());
					req.addTranslations(respondedRequests.get(requestId));
					req.save(reqFile.getParentFile());
				} else if(respondedRequestsErrors.containsKey(requestId)) {
					report.supportedLanguages.add(req.request.getSourceLanguage());
					for(String lang: req.errors.keySet()) 
						report.errorsOnCallback.incrementTo(req.errors.get(lang).errorCode);
					req.addErrors(respondedRequests.get(requestId));
					req.save(reqFile.getParentFile());
				}	
				if(req.isRespondedFully(report.unsupportedLanguages) ) {
					completedRequests++;
					if(respondedRequests.containsKey(requestId)) {
						csvOut.print(req.request.getText());
						csvOut.print(req.request.getSourceLanguage());
						for(String lang: supportedLanguages)
							csvOut.print(req.translations.get(lang));
						csvOut.println();
					}
				}
//				String trgLang = req.getParameter("target-language");
//				String translatedText = req.getParameter("translated-text");
//				String externalReference=req.getParameter("external-reference");
////				req.getParameter("request-id");
//				String filename=externalReference+"_"+trgLang+".txt";
//				FileUtils.write(new File(translationsFolder, filename), translatedText, UTF8, false);
			}
			report.fullyRespoded=(completedRequests==reqFiles.length);
			System.out.println(report);
			
			File csvFileOut = new File(new File(requestsFolder).getParentFile(), "etranslation-results.csv");
			FileUtils.write(csvFileOut, csvText.toString(), "UTF8");
			
			System.out.println("Results written in:\n"+csvFileOut.getAbsolutePath());			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	
}