package it.eng.eli4u.agenda.services.portal;

import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class LiferayPortalService implements AgendaPortalService {
	Logger logger = LoggerFactory.getLogger(LiferayPortalService.class);

	public String getUserName(PortletRequest renderRequest) {
		PortletSession portletSession = renderRequest.getPortletSession();
		String nomeUtente = null;
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		if (strPersonaCobo != null) {
			try {
				nomeUtente = extractNomeUtente(strPersonaCobo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return nomeUtente;
	}

	private String extractNomeUtente(String infoPersona) throws JsonParseException, JsonMappingException, IOException {
		String nomeUtente = null;

		ObjectMapper mapper = new ObjectMapper();
		PersonaCoboView personaCobo = mapper.readValue(infoPersona, PersonaCoboView.class);
		if (personaCobo != null) {
			nomeUtente = personaCobo.getIdAccount();
		}

		return nomeUtente;
	}

	@Override
	public Locale getLinguaPortale(PortletRequest request) {

//		String locale = "en";
//		ThemeDisplay td = (ThemeDisplay) PortalUtil.getHttpServletRequest(request).getSession().getAttribute(WebKeys.THEME_DISPLAY);
//		if (td != null) {
//			locale = td.getLocale().getLanguage();
//		}
		return PortalUtil.getHttpServletRequest(request).getLocale();
	}

	@Override
	public String getFullPageUrl(PortletRequest renderRequest, String pageName) {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<Layout> layouts;
		try {
			// Get private or public layouts (pages) depending whether the user is signed in
			if (themeDisplay.isSignedIn()) {
				User user = UserLocalServiceUtil.getUser(PortalUtil.getUserId(renderRequest));
				layouts = LayoutLocalServiceUtil.getLayouts(user.getGroupId(), true);
				logger.info("private user: {} layouts num: {}", user.getScreenName(), layouts.size());
			} else {
				Long groupId = themeDisplay.getLayout().getGroupId();
				logger.info("public groupId: {}", groupId);
				layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
				logger.info("public layouts num: {}", layouts.size());
			}

			for (Layout layout : layouts) {
				String fullLayoutUrl = PortalUtil.getLayoutFullURL(layout, (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY));
				if (fullLayoutUrl.contains(pageName))
					return fullLayoutUrl;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
