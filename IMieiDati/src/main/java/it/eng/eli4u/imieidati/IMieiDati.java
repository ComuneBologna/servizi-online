package it.eng.eli4u.imieidati;

import it.eng.eli4u.application.context.SpringApplicationContext;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;
import it.eng.eli4u.service.command.IServiceCommand;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMieiDati extends GenericPortlet {
	Logger logger = LoggerFactory.getLogger(IMieiDati.class);
	
	/**
	 * Implementa la modalità VIEW: se il parametro sectionsToHide non è stato memorizzato nelle preferences si mostra la prima frase,
	 * altrimenti si mostra il valore del parametro sectionsToHide
	 */
	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

		try {
			/*sicuramente uno ed uno solo portal service*/
			IIMieiDatiPortalService datiPortalService = SpringApplicationContext.getContext().getBeansOfType(IIMieiDatiPortalService.class).values().iterator().next();
			datiPortalService.setSessionAttributes(renderRequest);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/view.jsp");
		dispatcher.include(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		String command = req.getParameter("cmd");
		String ret = null;
		try {
			String clazz = "it.eng.eli4u.service.command." + command + "Command";
			Class<?> forName =  Class.forName(clazz);
			IServiceCommand serviceCommand = (IServiceCommand)SpringApplicationContext.getContext().getBean(forName); 
			ret = serviceCommand.execute(req);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			//ErrorMessage errorMessage = new ErrorMessage(IMieiDatiConstants.CODICE_ERR_GENERICO, "" + e.getMessage());
			//ret = new Gson().toJson(errorMessage);
			ret = IMieiDatiConstants.getDefaultErrorMessage();

		}
		resp.getWriter().write(ret);
	}

	/**
	 * Implementa la modalit� EDIT: mostra una JSP con una textbox in cui inserire il proprio nome.
	 * Pigiando il bottone GO parte l'azione sulla form
	 */
	@Override
	protected void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/edit.jsp");
		dispatcher.include(request, response);
	}

	/**
	 * � chiamato quando si inoltra una richiesta di azione sulla portlet.
	 * si legge il parametro sectionsToHide e si memorizza il suo valore nelle preferences.
	 * Per rendere attiva la memorizzazione delle preferences � necessario invocare il metodo store.
	 * Infine si passa alla modalit� VIEW
	 * 
	 */
	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {

		// Get all params from ActionRequest
		Map<String, String[]> params = actionRequest.getParameterMap();
		String sectionsToHideString = null;
		String portletName = null;
		for (Object key : params.keySet()) {

			// get all params about sectionToHide
			if (key.toString().startsWith("sectionsToHide_")) {

				// get value of key
				String[] values = params.get(key);
				String value = values[0];

				// get portletname
				String keyString = key.toString();
				keyString = keyString.substring(keyString.indexOf("%") + 1);
				keyString = keyString.substring(0, keyString.indexOf("%"));

				portletName = keyString;

				// Section is hidden if value is 1
				if (value.equals("1")) {
					String[] splittedKey = key.toString().split("_");
					String id = splittedKey[splittedKey.length - 1];
					value = id;

					if (sectionsToHideString == null) {
						sectionsToHideString = value;
					} else {
						sectionsToHideString = sectionsToHideString.concat(";" + value);
					}
				}

			}

		}

		// if string with all sections to hide is not null, save the string in session
		if (sectionsToHideString != null) {
			actionRequest.getPreferences().setValue("sectionsToHide_" + portletName, sectionsToHideString);
			PortletSession sessione = actionRequest.getPortletSession();

			// si memorizzano nella sessione le info
			sessione.setAttribute("prova.scope.portlet", "prova x me solo portlet (" + sectionsToHideString + ")", PortletSession.PORTLET_SCOPE);
		}

		else {
			actionRequest.getPreferences().setValue("sectionsToHide_" + portletName, "");
			PortletSession sessione = actionRequest.getPortletSession();

			// si memorizzano nella sessione le info
			sessione.setAttribute("prova.scope.portlet", "prova x me solo portlet (" + "" + ")", PortletSession.PORTLET_SCOPE);
		}

		// REMEMBER TO SAVE!!
		actionRequest.getPreferences().store();

		// si passa alla modalit� VIEW
		actionResponse.setPortletMode(PortletMode.VIEW);
	}

}