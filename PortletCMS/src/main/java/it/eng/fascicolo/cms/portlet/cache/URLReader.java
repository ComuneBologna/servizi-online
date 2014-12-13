package it.eng.fascicolo.cms.portlet.cache;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


public class URLReader implements IURLReader {
	
	Logger logger = LoggerFactory.getLogger(URLReader.class);
	
	@Override
	public Document readURLContent(String urlStr) throws Exception {

		StopWatch stopw = new Slf4JStopWatch("URLReader", logger);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(urlStr);
		
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		stopw.start();
		
		InputStream stream =  urlConnection.getInputStream();
		Document doc = db.parse(stream);
		
		stopw.stop();
		
		return doc;
	}
	
}
