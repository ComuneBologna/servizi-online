package it.eng.eli4u.imieidati;

import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;

import java.io.IOException;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class IMieiDatiGestione extends GenericPortlet {
	
	private final Logger logger = LoggerFactory.getLogger(IMieiDatiGestione.class);	

	@Autowired
	IIMieiDatiPortalService datiPortalService;

	/**
	 * Implementa la modalità VIEW:
	 * 
	 */
	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

		try {

			datiPortalService.setSessionAttributes(renderRequest);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/viewGestione.jsp");
		dispatcher.include(renderRequest, renderResponse);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
