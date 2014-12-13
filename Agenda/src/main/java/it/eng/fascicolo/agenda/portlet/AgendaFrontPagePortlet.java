package it.eng.fascicolo.agenda.portlet;

import it.eng.eli4u.agenda.services.portal.AgendaPortalService;
import it.eng.fascicolo.agenda.portlet.cache.CachedAgendaFrontPage;
import it.eng.fascicolo.agenda.spring.SpringApplicationContext;
import it.eng.fascicolo.agenda.util.AgendaUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Class to handle requests for the front page version of the agenda
 * 
 * @author egross
 * 
 */
public class AgendaFrontPagePortlet extends GenericPortlet {

	Logger logger = LoggerFactory.getLogger(AgendaFrontPagePortlet.class);

	private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	private static final String VIEW_JSP_PATH = "/jsp/viewAgendaFrontPage.jsp";

	/***
	 * Method to process the original request
	 */
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, PortletSecurityException, IOException {
		logger.info("AgendaFrontPagePortlet IN");
		Properties prop = new Properties();
		prop.load(AgendaFrontPagePortlet.class.getClassLoader().getResourceAsStream("portlet.properties"));
		int numberToLoad = Integer.parseInt(prop.getProperty("numberToLoadFront"));
		final int offset = 0;
		try {
			PortletSession portletSession = renderRequest.getPortletSession();
			logger.info("jsessionid portletside: " + portletSession.getId());

			renderResponse.setContentType(CONTENT_TYPE_HTML);

			// get page url
			AgendaPortalService portalService = (AgendaPortalService) SpringApplicationContext.getBean("portalService");
			CachedAgendaFrontPage cachedFrontPage = (CachedAgendaFrontPage) SpringApplicationContext.getBean("cachedFrontPage");
			// Get user from session
			String idUtente = portalService.getUserName(renderRequest); // (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
			String fullPageUrl = portalService.getFullPageUrl(renderRequest,  prop.getProperty("fullPageUrl"));
		
			if (idUtente != null)
				logger.debug("Utente logged in: {}", idUtente);
			logger.debug("Fullpage URL: {}", fullPageUrl);

			// prepare data for caching, avoiding null values
			idUtente = AgendaUtil.getStringNotNull(idUtente);
			fullPageUrl = AgendaUtil.getStringNotNull(fullPageUrl);
			String strNumberToLoad = AgendaUtil.intToStr(numberToLoad);
			String strOffset = AgendaUtil.intToStr(offset);
			
			Locale locale = portalService.getLinguaPortale(renderRequest);
			String writer = cachedFrontPage.getPortletContent(idUtente, fullPageUrl, strNumberToLoad, strOffset, locale);

			logger.trace("writer.toString(): " + writer.toString());
			renderRequest.setAttribute("agendaFrontPage", writer.toString());

			PortletRequestDispatcher prd = this.getPortletContext().getRequestDispatcher(VIEW_JSP_PATH);

			prd.include(renderRequest, renderResponse);
		} catch (Exception e) {
			logger.info("PortalException", e);
			logger.info("AgendaFrontPagePortlet out");
		}

	}

	/***
	 * Method to handle the ajax requests from the client
	 */
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) {
		logger.info("AgendaFrontPagePortlet serveResource IN");

		AgendaPortalService portalService = (AgendaPortalService) SpringApplicationContext.getBean("portalService");
		CachedAgendaFrontPage	cachedFrontPage = (CachedAgendaFrontPage) SpringApplicationContext.getBean("cachedFrontPage");
		
		String idUtente = portalService.getUserName(request);
		if (idUtente != null)
			logger.debug("Utente logged in: {}", idUtente);

		// Get parameters from the request
		String strStartDate = request.getParameter("start");
		logger.info("start: {}",strStartDate);
		String strEndDate = request.getParameter("end");
		logger.info("end: {}",strEndDate);

		try {
			// prepare data for caching, avoiding null values
			idUtente = AgendaUtil.getStringNotNull(idUtente);
			logger.debug("Utente logged in after: {}", idUtente);
			strStartDate = AgendaUtil.getStringNotNull(strStartDate);
			strEndDate = AgendaUtil.getStringNotNull(strEndDate);
			Locale locale = portalService.getLinguaPortale(request);
			logger.info("locale: " + locale);
			String output = cachedFrontPage.getDateRange(idUtente, strStartDate, strEndDate, locale);
			logger.info("output: " + output);

			response.setContentType("text/xml");
			response.resetBuffer();
			response.getWriter().print(output);
			response.flushBuffer();

			logger.info("AgendaFrontPagePortlet serveResource OUT");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.info("AgendaFrontPagePortlet serveResource OUT");
		}
	}

}
