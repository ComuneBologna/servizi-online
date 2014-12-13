package it.eng.fascicolo.intornoame.portlet.cache.impl;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import it.eng.fascicolo.intornoame.portlet.cache.IntornoAMeCache;

public class IntornoAMeCacheCoboImpl implements IntornoAMeCache {
	Logger logger = LoggerFactory.getLogger(IntornoAMeCacheCoboImpl.class);
	
	@Cacheable(value = "intornoAMeFrontCache", key = "T(java.lang.String).valueOf(#urlStringDrupal)")
	public String getFrontPageText(String urlStringDrupal){
		String bodyStr = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			URL url = new URL(urlStringDrupal);
			InputStream stream = url.openStream();
			Document doc = db.parse(stream);
			NodeList nl = doc.getElementsByTagName("html");
			bodyStr = parseDrupalResponse(nl.item(0).getTextContent());
			stream.close();
		} catch (Exception e) {
			logger.error("Errore nel reperire i contenuti da DRUPAL",e);
		}
		return bodyStr;
	}
	
	private String parseDrupalResponse(String in){
		if(in!=null){
			String out = in.replaceAll("tabindex=\"\\s*\"", "tabindex=\"0\"");
			return out;
		}
		else return "";
	}
}
