package eu.europeana.research.etranslation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import inescid.dataaggregation.dataset.GlobalCore;

public class CallbackServlet extends HttpServlet {
	protected static final Charset UTF8=Charset.forName("UTF-8");
	
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(CallbackServlet.class);
	
	protected File translationsFolder;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		Properties initParameters = getInitParameters();
		translationsFolder=new File(initParameters.getProperty("translations.folder"));
		if (!translationsFolder.exists())
			translationsFolder.mkdirs();
		File errorsFolder = new File(translationsFolder, "errors");
		if (!errorsFolder.exists())
			errorsFolder.mkdir();
//		String etranslationUsername = initParameters.getProperty("etranslation.username");
//		String etranslationUrl = initParameters.getProperty("etranslation.url");
//		File credentials = new File(initParameters.getProperty("etranslation.credentials"));
//		
//		HttpRequestService httpService=new HttpRequestService();
//		httpService.init(userName, credentials);
		
	}
	
//	@Override
//	public void destroy() {
//		super.destroy();
//	}

	private Properties getInitParameters() {
		ServletContext servletContext=getServletContext();
		Properties props=new Properties();
		Enumeration<?> initParameterNames = servletContext.getInitParameterNames();
		while (initParameterNames.hasMoreElements()) {
			Object pName = initParameterNames.nextElement();
			String initParameter = servletContext.getInitParameter(pName.toString());
			props.setProperty(pName.toString(), initParameter);
		}
		props.setProperty("etranslation.callback.webapp.root-folder", servletContext.getRealPath(""));
		return props;
	}
	
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String trgLang = req.getParameter("target-language");
			String translatedText = req.getParameter("translated-text");
//			String externalReference=req.getParameter("external-reference");
			String requestId=req.getParameter("request-id");
			String filename=requestId+"_"+trgLang+".txt";
			FileUtils.write(new File(translationsFolder, filename), translatedText, UTF8, false);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendResponse(resp, 500, "Internal error: " + e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}


	protected static void sendResponse(HttpServletResponse resp, int httpStatus, String body) throws IOException {
		log.info("Response HTTP status: "+ httpStatus);
		resp.setStatus(httpStatus);
		if (body != null && !body.isEmpty()) {
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.write(body.getBytes(GlobalCore.UTF8));
			resp.setContentType("text/html; charset=utf-8");
		}
	}
}
