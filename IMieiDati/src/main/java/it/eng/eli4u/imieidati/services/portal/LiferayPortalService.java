package it.eng.eli4u.imieidati.services.portal;

import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;
import it.eng.fascicolo.commons.profilazione.bo.LivelloAutenticazione;
import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class LiferayPortalService implements IIMieiDatiPortalService {
	Logger logger = LoggerFactory.getLogger(LiferayPortalService.class);
	
	@Override
	public String getUserName(PortletSession portletSession) throws Exception {

		String nomeUtente = null;
		
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		if (strPersonaCobo != null){
			nomeUtente = extractNomeUtente(strPersonaCobo);
		}
		return nomeUtente;
	}

	private String extractNomeUtente(String infoPersona) {
		String nomeUtente = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper ();
			PersonaCoboView personaCobo = mapper.readValue(infoPersona, PersonaCoboView.class);
			if (personaCobo != null){
				if(LivelloAutenticazione.STRONG.toString().equals(personaCobo.getLivelloAutenticazione()))
						nomeUtente = personaCobo.getCf();
				else
					logger.error("La portlet ha ricevuto richiesta da un utente non STRONG. profilo cobo: {}",personaCobo);
			}
		} catch (Exception ex) {
			logger.error("Unable to read CODICE_FISCALE from PersonaCoboView");
			logger.error(ex.getLocalizedMessage(), ex);
		}
		
		return nomeUtente;
	}

	@Override
	public void setSessionAttributes(RenderRequest request) throws Exception {
		PortletSession session = request.getPortletSession();

		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		HttpServletRequest convertReq = PortalUtil.getHttpServletRequest(request);
		HttpServletRequest originalReq = PortalUtil.getOriginalServletRequest(convertReq);

		originalReq.getSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		request.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		request.setAttribute(IMieiDatiConstants.PORTLET_NOME_REQUEST_ATTRIBUTE, themeDisplay.getPortletDisplay().getPortletName());
		// locale
		User user = UserLocalServiceUtil.getUserById(themeDisplay.getUser().getUserId());
		//String username = user.getUserId() + "";
		PortletSession portletSession = request.getPortletSession();
		String username = getUserName(portletSession);
		logger.info("User name: " + username);
		String locale = user.getLocale().getLanguage().toLowerCase();
		session.setAttribute("imieidati_username", username, PortletSession.APPLICATION_SCOPE);
		originalReq.getSession().setAttribute("imieidati_username", username);
		request.getPortletSession().setAttribute("imieidati_username", username);
		
		session.setAttribute("imieidati_locale", locale, PortletSession.APPLICATION_SCOPE);
		session.setAttribute("imieidati_companyId", user.getCompanyId(), PortletSession.APPLICATION_SCOPE);
		session.setAttribute("imieidati_userId", user.getUserId(), PortletSession.APPLICATION_SCOPE);
		
	}

	@Override
	public String parametroPortale(ResourceRequest request, String codice)
			throws Exception {

		String value = null;

		ThemeDisplay td = (ThemeDisplay) request.getPortletSession().getAttribute(
				WebKeys.THEME_DISPLAY);

		if (td != null) {
			User user = td.getUser();

			if ("NAME".equals(codice)) {
				value = user.getFirstName();
			} else if ("SURNAME".equals(codice)) {
				value = user.getLastName();
			} else if ("EMAIL".equals(codice)) {
				value = user.getEmailAddress();
			}

			// SCREEN NAME
			else if ("SCREENNAME".equals(codice)) {
				value = user.getScreenName();
			}

			// SCREEN NAME
			else if ("CODICE_FISCALE".equals(codice)) {
				//value = user.getScreenName();
				value = (String)request.getPortletSession().getAttribute("imieidati_username");
				logger.info("Parametro CODICE_FISCALE: " + value);
			}
		}

		// Launch Exception if is null
		if (value == null) {
			throw new Exception("Parametro non trovato.");
		}

		return value;
	}

	@Override
	public String linguaPortale(ResourceRequest request) {

		String locale = "en";
		ThemeDisplay td = (ThemeDisplay) request.getPortletSession().getAttribute(
				WebKeys.THEME_DISPLAY);
		if (td != null) {
			locale = td.getLocale().getLanguage();
		}
		return locale;
	}

	@Override
	public boolean isAdministrator() {
		boolean isAdmin = PermissionThreadLocal.getPermissionChecker().isOmniadmin();
		logger.debug("isAdmin: {}",isAdmin);
		return isAdmin;
	}

}
