
package it.eng.fascicolo.cms.portlet;

import it.eng.fascicolo.cms.portlet.cache.CachedCMSTiles;
import it.eng.fascicolo.cms.portlet.cache.IURLReader;
import it.eng.fascicolo.cms.portlet.context.SpringApplicationContext;
import it.eng.fascicolo.commons.profilazione.bo.LivelloAutenticazione;
import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/***
 * Portlet to retrieve and display data from iperbole. Configurable.
 * 
 * @author Eyal Gross
 */


public class PortletCMSTiles extends com.liferay.util.bridges.mvc.MVCPortlet {

	Logger logger = LoggerFactory.getLogger(PortletCMSTiles.class);
	protected String viewJsp_ = null;

	@Autowired
	IURLReader tilesUrlReader;

	@Override
	public void init() throws PortletException {
	  super.init();
	  viewJsp_ = getInitParameter("view-jsp");
	}
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException, PortletSecurityException, IOException {

		logger.debug("PortletCMSTiles: doView IN");
		
		try {
			// Avoid autowiring problems, if any
			if (tilesUrlReader == null) {
				tilesUrlReader = SpringApplicationContext.getContext().getBeansOfType(CachedCMSTiles.class).values().iterator().next();
			}
			// setCSS(renderRequest);
			setBody(renderRequest);
			
			include(viewJsp_, renderRequest, renderResponse);
			logger.debug("PortletCMSTiles: doView OUT");
		}
		catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.debug("PortletCMSTiles: doView OUT");
		}

	}

	public IURLReader getTilesUrlReader() {
		return tilesUrlReader;
	}

	public void setTilesUrlReader(IURLReader tilesUrlReader) {
		this.tilesUrlReader = tilesUrlReader;
	}

	/***
	 * Method to build the url, get the content and add it to the request to be
	 * displayed.
	 * 
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setBody(RenderRequest renderRequest)
		throws Exception {

		logger.debug("PortletCMSTiles: setBody IN");

		PortletSession portletSession = renderRequest.getPortletSession();
		logger.info("jsessionid portletside: " + portletSession.getId());

		Properties prop = new Properties();
		prop.load(PortletCMSTiles.class.getClassLoader().getResourceAsStream("portlet.properties"));

		// Get preferences from configuration
		PortletPreferences prefs = renderRequest.getPreferences();

		// Save parameters
		String baseHtmlURL = prefs.getValue("baseURL", prop.getProperty("tilesBaseHtmlUrl"));
		String tileId = prefs.getValue("tileId", "");
		logger.info("tileId {}", tileId);
		String targetPage = prefs.getValue("targetPage", "");
		logger.info("targetPage {}", targetPage);
		String targetId = prefs.getValue("targetId", "");
		logger.info("targetId {}", targetId);
		String idWeak = prefs.getValue("idWeak", "");
		logger.info("idWeak {}", idWeak);
		String idStrong = prefs.getValue("idStrong", "");
		logger.info("idStrong {}", idStrong);

		String bodyStr = null;
		String nome = null;

		if (baseHtmlURL != "") {
			if (!tileId.equals("")) {
				// Get user from session
				String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
				logger.info("strPersonaCobo", strPersonaCobo);

				if (strPersonaCobo != null) {
					logger.info("strPersonaCobo != null");
					
					ObjectMapper mapper = new ObjectMapper();
					PersonaCoboView personaCobo = mapper.readValue(strPersonaCobo, PersonaCoboView.class);
					nome = personaCobo.getNome();
					logger.info("nome: {}",nome);
					
					if (LivelloAutenticazione.STRONG.toString().equalsIgnoreCase(personaCobo.getLivelloAutenticazione()) && !StringUtils.isEmpty(idStrong)) {
						logger.debug("LivelloAutenticazione {}", "STRONG");
						tileId = idStrong;
					}
					else if (LivelloAutenticazione.WEAK.toString().equalsIgnoreCase(personaCobo.getLivelloAutenticazione()) && !StringUtils.isEmpty(idWeak)) {
						logger.debug("LivelloAutenticazione {}", "WEAK");
						tileId = idWeak;
					}
					String parameter = "&id=" + tileId;
					logger.debug("parameter {}", parameter);
					bodyStr = getHtml(renderRequest, baseHtmlURL, parameter, targetPage, targetId);
				}
				else {
					logger.debug("PortletCMSTiles: utente non loggato");
					bodyStr = getHtml(renderRequest, baseHtmlURL, "&id=" + tileId, targetPage, targetId);
				}

			}
			else {
				bodyStr = getHtml(renderRequest, baseHtmlURL, "", targetPage, targetId);
			}
		}
		else {
			bodyStr = "Si prega di configurare la portlet.";
		}

		renderRequest.setAttribute("userName", nome!= null ? nome : "");
		renderRequest.setAttribute("drupalBody", bodyStr != null ? bodyStr : "");
		logger.debug("PortletCMSTiles: setBody OUT");
	}

	private String getHtml(RenderRequest request, String baseHtmlURL, String parameter, String targetPage, String targetId)
		throws Exception {

		logger.debug("PortletCMSTiles: getHtml IN");
		Properties prop = new Properties();
		prop.load(PortletCMSTiles.class.getClassLoader().getResourceAsStream("portlet.properties"));

		String fullUrlString = baseHtmlURL + "?html=true" + parameter;
		Document doc = tilesUrlReader.readURLContent(fullUrlString);
		
		String bodyStr = new String();
		NodeList nl = doc.getElementsByTagName("html");
		bodyStr = nl.item(0).getTextContent();

		String fullPageParam = null;
		org.jsoup.nodes.Document docjsoup = Jsoup.parse(bodyStr);
		
		Elements linkElements = docjsoup.select("a");
		String relHref = null;
		if (linkElements != null) {
			for (Element link : linkElements) {
				relHref = link.attr("href");
				Map<String, List<String>> paramsMap = getQueryParams(relHref);
				logger.debug("getHtml: paramsMap {}", paramsMap);
				if (!paramsMap.isEmpty()) {
					if (paramsMap.containsKey("fullpage")) {
						List<String> fullPageParams = paramsMap.get("fullpage");
						for (String param : fullPageParams) {
							logger.debug("getHtml: param {}", param);
							fullPageParam = param;
						}
					}
					else if (paramsMap.containsKey("id") && !StringUtils.isEmpty(targetPage)) {
						String fullPageUrl = getFullPageUrl(request, targetPage);
						fullPageUrl = fullPageUrl + "" + targetId;
						bodyStr = bodyStr.replaceAll("http://<placeholderDettaglio>", fullPageUrl);
					}
				}
			}
		}
		logger.debug("PortletCMSTiles: fullPageParam {}", fullPageParam);
		if (fullPageParam != null) {
			String fullPageUrl = getFullPageUrl(request, fullPageParam);
			logger.debug("PortletCMSTiles: fullPageUrl {}", fullPageUrl);
			if (fullPageUrl != null)
				bodyStr = bodyStr.replaceAll("http://<placeholder>", fullPageUrl);
		}
		else if (!StringUtils.isEmpty(targetPage) && !StringUtils.isEmpty(targetId)) {
			String fullPageUrl = getFullPageUrl(request, targetPage);
			fullPageUrl = fullPageUrl + "?id=" + targetId;
			bodyStr = bodyStr.replaceAll("http://<placeholderDettaglio>", fullPageUrl);
		} else if (!StringUtils.isEmpty(targetPage)) {
			String fullPageUrl = getFullPageUrl(request, targetPage);
			bodyStr = bodyStr.replaceAll("http://<placeholderAccount>", fullPageUrl);
		}
		
		if (bodyStr != null)
			/* problema dei tabindex col validatore */
			bodyStr = bodyStr.replaceAll("tabindex=\"\\s*\"", "tabindex=\"0\"");
		return bodyStr;
	}

	private Map<String, List<String>> getQueryParams(String url) {

		try {
			logger.debug("getQueryParams: IN ");
			logger.debug("getQueryParams: url {}", url);
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			logger.debug("getQueryParams: IN ");
			return params;
		}
		catch (UnsupportedEncodingException ex) {
			logger.error(ex.getMessage(), ex);
			throw new AssertionError(ex);
		}
	}

	private String getFullPageUrl(RenderRequest renderRequest, String pageName)
		throws Exception {

		logger.debug("PortletCMSNewsFront: getFullPageUrl IN");
		StopWatch stopw = new Slf4JStopWatch("PortletCMSTiles-getFullPageUrl", logger);

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<Layout> layouts;

		stopw.start();
		// Get private or public layouts (pages) depending whether the user is
		// signed in
		if (themeDisplay.isSignedIn()) {
			User user = UserLocalServiceUtil.getUser(PortalUtil.getUserId(renderRequest));
			layouts = LayoutLocalServiceUtil.getLayouts(user.getGroupId(), true);
			logger.info("private user: {} layouts num: {}", user.getScreenName(), layouts.size());
		}
		else {
			Long groupId = themeDisplay.getLayout().getGroupId();
			logger.info("public groupId: {}", groupId);
			layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
			logger.info("public layouts num: {}", layouts.size());
		}

		for (Layout layout : layouts) {
			String fullLayoutUrl = PortalUtil.getLayoutFullURL(layout, (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY));
			logger.info("Layout fullUrl: {}", fullLayoutUrl);
			if (fullLayoutUrl.contains(pageName))
				return fullLayoutUrl;
		}
		
		stopw.stop();
		
		logger.debug("PortletCMSNewsFront: getFullPageUrl OUT");
		return null;
	}

	/***
	 * Method to get the css and add it to the request to be inserted in the
	 * head.
	 * 
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setCSS(RenderRequest renderRequest)
		throws Exception {

		logger.debug("PortletCMSTiles: setCSS IN");

		Properties prop = new Properties();
		prop.load(PortletCMSTiles.class.getClassLoader().getResourceAsStream("portlet.properties"));

		// Build URL string
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(prop.getProperty("baseCssUrl") + "?html=true");

		// Build CSS string
		InputStream stream = url.openStream();
		Document doc = db.parse(stream);
		String cssStr = doc.getDocumentElement().getTextContent();
		cssStr = cssStr.replace("\n", "").replace("\r", "");
		cssStr = cssStr.replaceAll("<style", "<style id=\"portlet-cms-style\"");

		renderRequest.setAttribute("drupalCSS", cssStr);

		logger.debug("PortletCMSTiles: setCSS OUT");
	}
}
