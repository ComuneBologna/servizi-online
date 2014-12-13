package it.eng.fascicolo.intornoame.service;

import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class LiferayPortalService implements IntornoAMePortalService {

	protected String getUserName(RenderRequest renderRequest) throws Exception {
		PortletSession portletSession = renderRequest.getPortletSession();

		String nomeUtente = null;
		
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		if (strPersonaCobo != null){
			nomeUtente = extractNomeUtente(strPersonaCobo);
		}
		return nomeUtente;
	}

	private String extractNomeUtente(String infoPersona) {
		String nomeUtente = null;
		/** TODO - Uncomment standard code
		ObjectMapper mapper = new ObjectMapper ();
		PersonaCoboView personaCobo = mapper.readValue(strPersonaCobo, PersonaCoboView.class);
		if (personaCobo != null){
			nomeUtente = personaCobo.getIdAccount();
		}
		*/
		if (infoPersona != null) {
			String pattern = "\"cf\":\"";
			int pos = infoPersona.indexOf(pattern);
			if (pos >= 0) {
				int start = pos + pattern.length();
				int end = infoPersona.indexOf("\"", start);
				if (start >= 0 && end > start) {
					nomeUtente = infoPersona.substring(start, end);
				}
			}
		}
		
		return nomeUtente;
	}

	@Override
	public void setSessionAttributes(RenderRequest request) throws Exception {
		PortletSession session = request.getPortletSession();

		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		System.out.println(themeDisplay == null ? "TD IS NULL!!!!" : "TD is not null");

		HttpServletRequest convertReq = PortalUtil.getHttpServletRequest(request);
		HttpServletRequest originalReq = PortalUtil.getOriginalServletRequest(convertReq);

		originalReq.getSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);
		request.getPortletSession().setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		// locale
		User user = UserLocalServiceUtil.getUserById(themeDisplay.getUser().getUserId());
		//String username = user.getUserId() + "";
		String username = getUserName(request);
		String locale = user.getLocale().getLanguage().toLowerCase();
		session.setAttribute("username", username, PortletSession.APPLICATION_SCOPE);
		originalReq.getSession().setAttribute("username", username);
		request.getPortletSession().setAttribute("username", username);
		
		session.setAttribute("locale", locale, PortletSession.APPLICATION_SCOPE);
		session.setAttribute("companyId", user.getCompanyId(), PortletSession.APPLICATION_SCOPE);
		session.setAttribute("userId", user.getUserId(), PortletSession.APPLICATION_SCOPE);
	}

	@Override
	public String parametroPortale(HttpServletRequest request, String codice)
			throws Exception {

		String value = null;

		ThemeDisplay td = (ThemeDisplay) request.getSession().getAttribute(
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
				value = (String)request.getSession().getAttribute("username");
			}
		}

		// Launch Exception if is null
		if (value == null) {
			throw new Exception("Parametro non trovato.");
		}

		return value;
	}

	@Override
	public String linguaPortale(HttpServletRequest request) {

		String locale = "en";
		ThemeDisplay td = (ThemeDisplay) request.getSession().getAttribute(
				WebKeys.THEME_DISPLAY);
		if (td != null) {
			locale = td.getLocale().getLanguage();
		}
		return locale;
	}


}
