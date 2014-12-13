
package it.eng.fascicolo.intornoame.portlet;

import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;
import it.eng.fascicolo.intornoame.portlet.cache.IntornoAMeCache;
import it.eng.fascicolo.intornoame.service.IntornoAMePortalService;
import it.eng.fascicolo.intornoame.service.LiferayPortalService;
import it.eng.fascicolo.intornoame.spring.SpringApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class IntornoAMeFrontPagePortlet extends GenericPortlet {

	Logger logger = LoggerFactory.getLogger(IntornoAMeFrontPagePortlet.class);

	private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	private static final String VIEW_JSP_PATH = "/jsp/viewIntornoAMeFrontPage.jsp";

	private String distanzaKmDalCentroCitta=null;
	private String defaultIconMap=null;
	private String longitudineCentroCitta=null;
	private String latitudineCentroCitta=null;
	private String urlRicercaLuogo=null;
	private String urlTilesCmsDrupalLogged=null;
	private String urlTilesCmsDrupalGuest=null;
	private String fullPageUrl=null;
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		distanzaKmDalCentroCitta=config.getInitParameter("distanzaKmDalCentroCitta");
		defaultIconMap=config.getInitParameter("defaultIconMap");
		longitudineCentroCitta=config.getInitParameter("longitudineCentroCitta");
		latitudineCentroCitta=config.getInitParameter("latitudineCentroCitta");
		urlRicercaLuogo=config.getInitParameter("urlRicercaLuogo");
		urlTilesCmsDrupalLogged=config.getInitParameter("urlTilesCmsDrupalLogged");
		urlTilesCmsDrupalGuest=config.getInitParameter("urlTilesCmsDrupalGuest");
		fullPageUrl=config.getInitParameter("fullPageUrl");
	}
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException, PortletSecurityException, IOException {

		renderRequest.setAttribute("defaultIconMap", defaultIconMap);
		renderRequest.setAttribute("distanzaKmDalCentroCitta", distanzaKmDalCentroCitta);
		renderRequest.setAttribute("longitudineCentroCitta", longitudineCentroCitta);
		renderRequest.setAttribute("latitudineCentroCitta", latitudineCentroCitta);
		renderRequest.setAttribute("urlRicercaLuogo", urlRicercaLuogo);

		logger.debug("IntornoAMeFrontPagePortlet IN");
		try {
			PortletSession portletSession = renderRequest.getPortletSession();
			logger.debug("jsessionid portletside: " + portletSession.getId());

			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();
			Template t = ve.getTemplate("intornoamefrontpage.template.vm.html", "iso-8859-1");
			VelocityContext context = new VelocityContext();
			renderResponse.setContentType(CONTENT_TYPE_HTML);
			String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);			
			String urlStringDrupal=null;
			if (strPersonaCobo!=null) {
	            context.put("titoloPortlet", "Intorno a me");
	            urlStringDrupal=urlTilesCmsDrupalLogged;
	        } else {
	            context.put("titoloPortlet", "Intorno a te");
	            urlStringDrupal=urlTilesCmsDrupalGuest;
	        }
			context.put("contesto", renderRequest.getContextPath());

			/* Lettura contenuti CMS Drupal */
			
			IntornoAMeCache caching = (IntornoAMeCache)SpringApplicationContext.getBean("intornoAMeCaching");
			String bodyStr = caching.getFrontPageText(urlStringDrupal);
			
			IntornoAMePortalService portalService = (IntornoAMePortalService) SpringApplicationContext.getBean("portalService");			
			if (portalService instanceof LiferayPortalService) {
				String str = getFullPageUrl(renderRequest, fullPageUrl);
				if (str!=null) {
					bodyStr = bodyStr.replaceAll("http://<placeholder>", str);
				}
				logger.debug("Sostituito Placeholder per Liferay");
			} else {
				//TODO Websphere
				logger.debug("Placeholder da sostituire per altri Portali");
			}
			
            context.put("drupalBody", bodyStr);
			logger.debug("Contenuto Drupal: "+bodyStr);

			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			renderRequest.setAttribute("intornoAMeFrontPage", writer.toString());
			PortletRequestDispatcher prd = this.getPortletContext().getRequestDispatcher(VIEW_JSP_PATH);
			prd.include(renderRequest, renderResponse);
		}
		catch (Exception e) {
			logger.error("PortalException", e);
		}

	}
	
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) {

		logger.debug("IntornoAMeFrontPagePortlet serveResource IN");

	}
	//TODO portare in LiferayPortalService
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
