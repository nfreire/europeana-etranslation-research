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

public class TestEtranslationClientRequest {
	
	

	public static void main(String[] args) {
		runTestOnQuerySampleRequestSending();
		
	}
	
	public static void runTestOnQuerySampleRequestSending() {
		try {
			File resultsFolder=new File("src/data/test_requests");
			if(!resultsFolder.exists())
				resultsFolder.mkdirs();
			
			EtranslationClient et=new EtranslationClient();
			
			
			String[][] requests=new String[][] {
				new String[] {"bs", "EN", 
				"Și nu-i nici Împăratul\nSă-mi cânte lumea ce va vrea\nMi-e draga una șî-i a mea\nDecât să mă despart de ea\nMai bine aprind tot satul.\n de la George Brici S.M.S. Bellona\n8. Florea Văleanului\nI.\nFoie verde fir de nalbă\nRăsari lună mai degrabă\nSă se vadă prin livadă\nSă cosesc pelin și earbă\nSă-i dau mândri să desfacă\nDesfă mândră ce-ai făcut\nCă ți-oi da cu un zlot mai mult.\nNu poci bade să-ți desfac\nCă din urmă ți-am luat\nȘi-n grădină am îngropat\nEu nu-ți-am făcut să per\nCă ți-am făcut să mă iei\nEa mă bade ia mă dragă\nCă de nu ma iei-s beteagă\nEa mă bade fătul meu\nDe nu mor de dorul tău.\nde la P. Țrunra"}
//				, new String[] {"hu", "EN", 
//				"Oktatás\\n 1. A katonai igazolványi lap a nem lényleges állományu katonának katonai viszonya kimutatására szolgál mind-\\naddig, míg más igazolási okmánynyal el nem láttatik, vagy a katonai kötelékből ki nem lép. A katonai igazolványi lap\\nminden jelentkezés alkalmával felmutatandó s utazási okmány gyanánt nem szolgálhat, miért is minden katona utazás\\nesetében a szükséges utazási okmányt esetleg megszerezni köteles.\\n A nem tényleges állománybeli katonának a katonai egyenruhát vagy annak egyes darabjait (mint pl. a tábori\\nsapkát) viselni nem szabad. Ha a nem tényleges viszonyból való áthelyeztetése alkalmával katonai egyenruha adatik neki,\\nazt csak a tartózkodási helyére való megérkezéseig és valamely bevonulás alkalmával szabad használnia.\\n 2. A nem tényleges állományu katonák minden polgári viszonyaikra nézve, valamint büntető és rendőri ügyeikben\\nis a polgári biróságok és hatóságok alatt állanak; s csak a védkötelezttségi viszyonukból kifolyó, törvényen alapuló\\ns a nyilvántartás szempontjából szükséges megszoritások tekintetében vannak az illetékes nyivlántartási hatóságoknak\\nalárendelve.\\n 3. Katonai szolgálati ügyeket tárgyazó mindennemü beadványok (kérvények, panaszok) - amennyiben az ellenőrzési\\nszemlén még elő nem adattok volna, - az illetékes járási tisztviselőhöz intézendők.\\n Ilynemü beadványok azonban a tartózkodási hely közigazgatási járási tisztviselőjénél is benyujthatók.\\n 4. A nem tényleges állományu katona legkésőbb 14 nappal a tényleges szolgálatból, katonai kiképzésből vagy fegyvergyakorlatból történt kilépése után, a nem tényleges viyzonban megmaradó ujoncz vagy póttartalékos pedig legkésőbb\\ntizennégy nappal az állományba vétel utána tartózkodási hely községelöljárójánál jelentkezni tartozik.\\n 5. A nem tényleges állományu katona köteles tartózkodási helyének minden változását eltávozása előtt a község\\nelöljárójánál, az ujabb tartózkodási helyre történt megérkezését pedig ezen utóbbi hely községi elöljárójának - nyolcz\\nnapon belül - bejelenteni.\\n Hasonlólag a tartózkodási helyen előforduló minden lakásváltoztatás is legfeljebb 8 nappal az átköltözés megtörténte\\nután a községelöljárónak bejelentendő.\\n 6. Ha valamely nem tényleges állományu katona oly belföldi vagy külföldi utra kél, mely 14 napnál továbbterjedő\\ntávollétet von maga után, köteles ugy az elutazást, mint a visszatérést is a községelöljárónak bejelenteni.\\n Ha az utazás megkezdése idejében nem volt előrelátható, hogy a távollét 14 napnál tovább fog tartani, a jelentés utólag és pedig legkésőbb 14 nappal az elutazás után teendő meg.\\n 7. Az utazás megkezdésére vagy a külföldön való tartózkodásra vonatkozó miden [sic!] jelentés alkalmával köteles az\\nillető azon harmadik személyt bejelenteni, kinek közvetitése two illegible words/két olvashatatlan szó neki szóló parancsok kézbesithetők lesznek.\\n Ezen közvetitő személynek meg nem bizhatóságából netán bekövetkező hátrányos következményeket azonban maga az illető köteles viselni.\\n 8. Ha a nem tényleges állományu katona útközben valamely helyen 14 napig vagy ennél tovább akar tartózkodni,\\nköteles megérkezését, valamint elutazását is az illető hely községelöljárójánál bejelenteni.\\n 9. A Boszniába vagy Herczegovinába utazó vagy ott tartózkodó nem tényleges állományu katonák azon járási\\nhatóságnál kötelesek jelentkezni, amelynek területén tartózkodási helyük fekszik. A Limterületen tartózkodók pedig az\\nesetleg tartózkodási helyükön lévő vagy ahhoz legközelebb eső katonai állomás-parancsnokságnál jelentkeznek. Ha ezen\\njelentkezés az állomás-parancsnokságnál nem volna teljesithető, az illető a 10. pont értelmében tartozik eljárni.\\n 10. A külföldön tartózkodó vagy utazó nem tényleges állományu katona az előző pontok alatt felsorolt jelentkezéseket\\na tartózkodási helyén esetleg létező cs. és kir. képviseleti hatóságnál köteles megtenni, különben pedig illetékes\\njárási tisztviselőjét vagy közvetlenül, vagy az otthon lévő közvetitő személy utján tartózkodási helyének minden változásáról\\nértesiteni, hogy az esetleges parancsok neki kézvesithetők legyenek.\\n Azon nem tényleges állományu katona, aki belföldi községi kötelékhez nem tartozik, ezen jelentéseket az illetékes\\nhadkiegészitési kerületi parancsonkságnál teszi meg.\\n 11. A megérkezésre, tartózkodásra vagy elutazásra vonatkozó jelentések szóbelileg teendők meg; irásban csak\\nakkor, ha a szóbeli jelentkezés nem volna eszközölhető.\\n 12. Annak igazolására, hogy valamely jelentés megtétetett, ezen körülmény az illető hatóság által a katonai\\nigazolványi lapba bejegyzendő.\\n 13. A jelentkezési és nyilvántartási szabályok áthágása mint kihágás, a tartózkodási hely közigazgatási járási tisztviselője által büntettetik.\\n 14. A tartósan szabadságolt sorhadi szolgálatköteleseknek, kivéve azokat, kik mint tanitóképezdei növendékek,\\nvagy családi tekintetből a béke idejére szabadásgoltattak, továbbá azokat, akik a sorhadi szolgálati kötelezettségnek három\\nutolsó hónapjában vannak, állománycsapatjuk engedélye nélkül megnősülni nem szabad. Az erre vonatkozó, kellően felszerelt\\nfolyamodvány a 3. pontban megjelölt helyen nyujtando be. Ha a nősülési engedély megadatik, az egybekelés megtörténte\\naz esketési bizonyitvány bemutatása mellett bejelentendő.\\n 15. A nem tényleges állományu tartalékosok, póttartalékosok és a tengervédhez tartozók, akármelyik korosztályban állanak is, ha nősülni akarnak, védkötelezettségük szempontjából megszoritásnak alávetve nincsenek. Az ily katonáknak tehát\\nnősülhetés végett az állománycsapat engedélyére nincsen szükségük.\\n 16. A nem tényleges állományu katonák valamely katonai szolgálattételre behivójegygyel, általános mozgositás esetén pedig nyilvános hirdetmények utján hivatnak be."}
//				, 
//				new String[] {"en", "FR", 
//				"Hello, how are you doing?"}
			};
			for(String[] reqParams: requests) {
				TranslationRequest tReq = new TranslationRequest(reqParams[2], reqParams[0], reqParams[1]);
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