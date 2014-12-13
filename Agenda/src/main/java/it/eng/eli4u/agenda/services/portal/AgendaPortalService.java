package it.eng.eli4u.agenda.services.portal;

import java.util.Locale;

import javax.portlet.PortletRequest;

public interface AgendaPortalService {

	public Locale getLinguaPortale(PortletRequest request);

	public String getUserName(PortletRequest renderRequest);
	
	public String getFullPageUrl(PortletRequest renderRequest, String pageName);
}
