package it.eng.fascicolo.cms.portlet;

import it.eng.fascicolo.cms.portlet.cache.CachedCMSNewsFront;
import it.eng.fascicolo.cms.portlet.cache.IURLReader;
import it.eng.fascicolo.cms.portlet.context.SpringApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/***
 * Portlet to retrieve and display data from iperbole. Configurable.
 * @author Eyal Gross
 *
 */


public class PortletCMSNewsFront extends com.liferay.util.bridges.mvc.MVCPortlet {

	Logger logger = LoggerFactory.getLogger(PortletCMSNewsFront.class);
	protected String viewJsp_ = "/newsFrontPage.jsp";

	@Autowired
	IURLReader newsFrontUrlReader;
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, PortletSecurityException, IOException {
		logger.debug("PortletCMSNewsFront: doView IN");
		
		try {
			//EhCacheCacheManager cacheManager = SpringApplicationContext.getContext().getBeansOfType(EhCacheCacheManager.class).values().iterator().next();
			//ArrayList<String> caches = new ArrayList<String>(cacheManager.getCacheNames());
			
			// Avoid autowiring problems, if any
			if (newsFrontUrlReader == null) {
				newsFrontUrlReader = SpringApplicationContext.getContext().getBeansOfType(CachedCMSNewsFront.class).values().iterator().next();
			}
			
			setCSS(renderRequest);
			setBody(renderRequest);
			include(viewJsp_, renderRequest, renderResponse);
			logger.debug("PortletCMSNewsFront: doView OUT");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.debug("PortletCMSNewsFront: doView OUT");
		} 

	}

	public IURLReader getNewsFrontUrlReader() {
		return newsFrontUrlReader;
	}

	public void setNewsFrontUrlReader(IURLReader newsFrontUrlReader) {
		this.newsFrontUrlReader = newsFrontUrlReader;
	}

	/***
	 * Method to build the url, get the content and add it to the request to be displayed.
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setBody(RenderRequest renderRequest) throws Exception {
		logger.debug("PortletCMSNewsFront: setBody IN");
		
		Properties prop = new Properties();
		prop.load(PortletCMSNewsFront.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		//Get preferences from configuration
		PortletPreferences prefs = renderRequest.getPreferences();

		//Save parameters
		String baseHtmlURL = prefs.getValue("baseURL", prop.getProperty("baseHtmlUrl"));		
		Map<String, String> params = new HashMap<String, String>();
		params.put("count", prefs.getValue("count", prop.getProperty("count")));
		params.put("category", prefs.getValue("category", prop.getProperty("category")));
		params.put("summary", prefs.getValue("summary", prop.getProperty("summary")));
		params.put("list", prefs.getValue("list", prop.getProperty("list")));
		
		String urlString = new String();
		String bodyStr = new String();
		
		//Build URL string from parameters
		if (baseHtmlURL != "") {
			boolean isFirstTaken = false;
			
			urlString = baseHtmlURL;
			urlString += "?";
			
			for (String key : params.keySet()) {
				String val = params.get(key);
				if (val != "") {
					if (isFirstTaken) {
						urlString += "&" + key + "=" + val;
					} else {
						urlString += key + "=" + val;
						isFirstTaken = true;
					}								
				}
			}			

			/*
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			URL url = new URL(urlString + "&html=true");
			InputStream stream = url.openStream();
			Document doc = db.parse(stream);
			*/
			String fullUrlString = urlString + "&html=true";
			Document doc = newsFrontUrlReader.readURLContent(fullUrlString);

			NodeList nl = doc.getElementsByTagName("html");
			if (nl != null && nl.item(0)!=null &&  nl.item(0).getTextContent()!=null){
				bodyStr = nl.item(0).getTextContent();			
				String fullPageUrl = getFullPageUrl(renderRequest, prop.getProperty("fullPageName"));
				if (fullPageUrl != null){
					bodyStr = bodyStr.replaceAll("http://<placeholder>", fullPageUrl);
					bodyStr = bodyStr.replaceAll("tabindex=\"\\s*\"", "tabindex=\"0\"");
				}
			}

		} else {
			bodyStr = "Si prega di configurare il portlet.";
		}
		
		renderRequest.setAttribute("drupalBody", bodyStr);
		
		logger.debug("PortletCMSNewsFront: body String = " + bodyStr);
		logger.debug("PortletCMSNewsFront: setBody OUT");
	}

	/***
	 * Method to get the css and add it to the request to be inserted in the head.
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setCSS(RenderRequest renderRequest) throws Exception {
		logger.debug("PortletCMSNewsFront: setCSS IN");
		
		Properties prop = new Properties();
		prop.load(PortletCMSNewsFront.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		//Build URL string
		/*
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(prop.getProperty("baseCssUrl") + "?html=true");

		//Build CSS string
		InputStream stream = url.openStream();
		Document doc = db.parse(stream);
		*/
		String fullUrlString = prop.getProperty("baseCssUrl") + "?html=true";
		Document doc = newsFrontUrlReader.readURLContent(fullUrlString);
		
		//Build CSS string
		String cssStr = doc.getDocumentElement().getTextContent();
		cssStr = cssStr.replace("\n", "").replace("\r", "");
		cssStr = cssStr.replaceAll("<style", "<style id=\"portlet-cms-style\"");
		
		renderRequest.setAttribute("drupalCSS", cssStr);
		
		logger.debug("PortletCMSNewsFront: setCSS OUT");
	}
	
	private String getFullPageUrl (RenderRequest renderRequest, String pageName) throws Exception {
		logger.debug("PortletCMSNewsFront: getFullPageUrl IN");
		
		ThemeDisplay themeDisplay= (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);		
		List<Layout> layouts;
		
		//Get private or public layouts (pages) depending whether the user is signed in
		if (themeDisplay.isSignedIn()) {
			User user = UserLocalServiceUtil.getUser(PortalUtil.getUserId(renderRequest));
			layouts = LayoutLocalServiceUtil.getLayouts(user.getGroupId(), true);
			logger.info("private user: {} layouts num: {}",user.getScreenName(),  layouts.size());
		} else {
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
		
		logger.debug("PortletCMSNewsFront: getFullPageUrl OUT");
		return null;
	}
}
