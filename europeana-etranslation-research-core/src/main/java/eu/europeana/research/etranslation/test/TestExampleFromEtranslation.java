package eu.europeana.research.etranslation.test;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


public class TestExampleFromEtranslation {
	
		private static String createTranslationRequest(String sourceLanguage) {
			return new JSONObject().put("priority", 1).put("externalReference", "123")
					.put("callerInformation", new JSONObject().put("application", "Europeana_IR_20190225").put("username", "toto"))
					.put("textToTranslate", "this is a test").put("sourceLanguage", sourceLanguage)
					.put("targetLanguages", new JSONArray().put(0, "FR")).put("domain", "SPD").toString();
		}
	
		public static void runTest() {
			try {
				String userName = "Europeana_IR_20190225";
	//	String userName = "nuno.freire@europeana.eu";
				File credentials = new File("C:\\Users\\nfrei\\.credentials\\etranslation");
				String password = FileUtils.readFileToString(credentials, "UTF-8");
	
				String url = "https://webgate.ec.europa.eu/etranslation/si/translate";
				DefaultHttpClient client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(userName, password));
				HttpPost post = new HttpPost(url);
				post.setEntity(new StringEntity(createTranslationRequest("EN"),
						ContentType.APPLICATION_JSON.getMimeType(), "UTF-8"));
				HttpClientParams.setRedirecting(post.getParams(), false);
				HttpResponse response = client.execute(post);
				System.out.println(response.getStatusLine().getStatusCode());
				System.out.println( IOUtils.toString(response.getEntity().getContent() , "UTF8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public static void main(String[] args) {
		TestExampleFromEtranslation.runTest();
	}
	
}
