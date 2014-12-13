package it.eng.fascicolo.cms.portlet.cache;

import org.w3c.dom.Document;

public interface IURLReader {
	
	public Document readURLContent(String urlStr) throws Exception;

}
