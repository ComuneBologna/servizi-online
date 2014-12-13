package it.eng.eli4u.imieidati.services;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

public interface IIMieiDatiPortalService {

	public String parametroPortale(ResourceRequest request, String codice) throws Exception;
	
	public String linguaPortale(ResourceRequest request);

	public void setSessionAttributes(RenderRequest request) throws Exception;	
	
	public String getUserName(PortletSession portletSession) throws Exception;
	
	public boolean isAdministrator();
	
}
