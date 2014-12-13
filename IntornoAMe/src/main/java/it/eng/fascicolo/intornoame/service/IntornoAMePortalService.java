package it.eng.fascicolo.intornoame.service;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

public interface IntornoAMePortalService {
	
	public String parametroPortale(HttpServletRequest request, String codice) throws Exception;
	public String linguaPortale(HttpServletRequest request);
	public void setSessionAttributes(RenderRequest request) throws Exception;	
	
}
