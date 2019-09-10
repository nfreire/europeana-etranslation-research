package eu.europeana.research.etranslation;


import java.io.File;
import java.io.IOException;
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

public class ErrorServlet extends CallbackServlet {
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(ErrorServlet.class);
	
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String errorCode = req.getParameter("error-code");
			String errorMessage = req.getParameter("error-message");
			String targetLanguages = req.getParameter("target-languages");
//			String externalReference=req.getParameter("external-reference");
			String requestId=req.getParameter("request-id");
			String filename=requestId+"_"+targetLanguages+".txt";
			FileUtils.write(new File(translationsFolder, "errors/"+filename), errorCode+"\n"+errorMessage, UTF8, false);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CallbackServlet.sendResponse(resp, 500, "Internal error: " + e.getMessage());
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
}
