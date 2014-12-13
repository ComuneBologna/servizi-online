package it.eng.fascicolo.cms.portlet;

import it.eng.fascicolo.cms.portlet.cache.CachedCMSServizi;
import it.eng.fascicolo.cms.portlet.cache.IURLReader;
import it.eng.fascicolo.cms.portlet.context.SpringApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

import com.liferay.portal.util.PortalUtil;

/***
 * Portlet to retrieve and display data from iperbole. Configurable.
 * @author Eyal Gross
 *
 */


public class PortletCMSServizi extends com.liferay.util.bridges.mvc.MVCPortlet {

	Logger logger = LoggerFactory.getLogger(PortletCMSServizi.class);
	protected String viewJsp_ = "/servizi.jsp";
	
	@Autowired
	IURLReader serviziUrlReader;

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, PortletSecurityException, IOException {
		logger.debug("PortletCMSServizi: doView IN");
		
		try {
			// Avoid autowiring problems, if any
			if (serviziUrlReader == null) {
				serviziUrlReader = SpringApplicationContext.getContext().getBeansOfType(CachedCMSServizi.class).values().iterator().next();
			}
			setCSS(renderRequest);
			setBody(renderRequest);
			include(viewJsp_, renderRequest, renderResponse);
			logger.debug("PortletCMSServizi: doView OUT");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			logger.debug("PortletCMSServizi: doView OUT");
		} 

	}

	public IURLReader getServiziUrlReader() {
		return serviziUrlReader;
	}

	public void setServiziUrlReader(IURLReader serviziUrlReader) {
		this.serviziUrlReader = serviziUrlReader;
	}

	/***
	 * Method to build the url, get the content and add it to the request to be displayed.
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setBody(RenderRequest renderRequest) throws Exception {
		logger.debug("PortletCMSServizi: setBody IN");
		
		Properties prop = new Properties();
		prop.load(PortletCMSServizi.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		//Get preferences from configuration
		PortletPreferences prefs = renderRequest.getPreferences();

		//Save parameters
		String baseHtmlURL = prefs.getValue("baseURL", prop.getProperty("serviziBaseHtmlUrl"));
		String type = prefs.getValue("type", "full");
		
		String bodyStr = new String();
		
		if (baseHtmlURL != "") {
			if (type.equals("full")) {
				bodyStr = getHtml(renderRequest, baseHtmlURL, "");
			} else if (type.equals("sidebar")) {
				String parameter1 = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getParameter("id");
				logger.debug("PortletCMSServizi: parameter1 {}",parameter1);
				String parameter2 = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getParameter("sidebar");
				logger.debug("PortletCMSServizi: parameter2 {}",parameter2);
				bodyStr = getHtml(renderRequest, baseHtmlURL, "&id=" + parameter1 + "&tile_sidebar=true");
			} else if (type.equals("articolo")) {
				String parameter = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest)).getParameter("id");
				bodyStr = getHtml(renderRequest, baseHtmlURL, "&id=" + parameter);
			}
		} else {
			bodyStr = "Si prega di configurare il portlet.";
		}
		
		renderRequest.setAttribute("drupalBody", bodyStr);		
		logger.debug("PortletCMSServizi: setBody OUT");
	}
	
	private String getHtml (RenderRequest request, String baseHtmlURL, String parameter) throws Exception {
		Properties prop = new Properties();
		prop.load(PortletCMSServizi.class.getClassLoader().getResourceAsStream("portlet.properties"));	
		
		String bodyStr = new String();
		/*
		URL url = new URL(baseHtmlURL + "?html=true" + parameter);
		logger.debug("PortletCMSServizi: url {}",url);
		InputStream stream = url.openStream();
		Document doc = db.parse(stream);
		*/
		
		String fullUrlString = baseHtmlURL + "?html=true" + parameter;
		Document doc = serviziUrlReader.readURLContent(fullUrlString);
		
		NodeList nl = doc.getElementsByTagName("html");
		bodyStr = nl.item(0).getTextContent();
		bodyStr = bodyStr.replaceAll("http://<placeholderServizi>", prop.getProperty("serviziFullPageName"));
		bodyStr = bodyStr.replaceAll("tabindex=\"\\s*\"", "tabindex=\"0\"");
		return bodyStr;
	}

	/***
	 * Method to get the css and add it to the request to be inserted in the head.
	 * @param renderRequest
	 * @throws Exception
	 */
	private void setCSS(RenderRequest renderRequest) throws Exception {
		logger.debug("PortletCMSServizi: setCSS IN");
		
		Properties prop = new Properties();
		prop.load(PortletCMSServizi.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		//Build URL string
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(prop.getProperty("baseCssUrl") + "?html=true");
		
		//Build CSS string
		InputStream stream = url.openStream();
		Document doc = db.parse(stream);
		String cssStr = doc.getDocumentElement().getTextContent();
		cssStr = cssStr.replace("\n", "").replace("\r", "");
		cssStr = cssStr.replaceAll("<style", "<style id=\"portlet-cms-style\"");
		
		renderRequest.setAttribute("drupalCSS", cssStr);
		
		logger.debug("PortletCMSServizi: setCSS OUT");
	}
}
