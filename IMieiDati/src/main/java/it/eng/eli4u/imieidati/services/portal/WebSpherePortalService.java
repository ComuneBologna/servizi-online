package it.eng.eli4u.imieidati.services.portal;

import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

public class WebSpherePortalService implements IIMieiDatiPortalService {

	@Override
	public String parametroPortale(ResourceRequest request, String codice) throws Exception {

		String value = null;

		if ("NAME".equals(codice)) {

			value = "Test";
		}
		else if ("SURNAME".equals(codice)) {

			value = "Test2";
		}
		else if ("EMAIL".equals(codice)) {

			value = "Test@websphere.com";
		}
		
		// SCREEN NAME
		else if ("SCREENNAME".equals(codice)) {
			value = "Test";
		}

		// CODICE FISCALE
		else if ("CODICE_FISCALE".equals(codice)) {
			value = "Test_Fiscal_Code";
		}

		// Launch Exception if is null
		if (value == null) {
			throw new Exception("Parametro non trovato.");
		}
	
		return value;
	}

	@Override
	public String linguaPortale(ResourceRequest request) {

		return "it";
	}

	@Override
	public void setSessionAttributes(RenderRequest request) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserName(PortletSession portletSession) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAdministrator() {
		// TODO Auto-generated method stub
		return false;
	}
}
