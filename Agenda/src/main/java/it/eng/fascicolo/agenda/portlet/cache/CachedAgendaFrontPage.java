package it.eng.fascicolo.agenda.portlet.cache;

import it.eng.eli4u.agenda.services.portal.AgendaPortalService;
import it.eng.fascicolo.agenda.dao.AgendaDAO;
import it.eng.fascicolo.agenda.spring.SpringApplicationContext;
import it.eng.fascicolo.agenda.util.AgendaUtil;
import it.eng.fascicolo.commons.jpa.model.FceAgenda;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class CachedAgendaFrontPage {
	@Autowired
	AgendaPortalService portal;

	Logger logger = LoggerFactory.getLogger(CachedAgendaFrontPage.class);
	@Autowired
	AgendaDAO agendaDAO;

	/***
	 * Method to get events based on a date range
	 * 
	 * @param strPersonaCobo
	 *            String representation of the user
	 * @param strStartDate
	 *            String representation of the start date
	 * @param strEndDate
	 *            String representation of the end date
	 * @return
	 * @throws Exception
	 */

	@Cacheable(value = "agendaFrontPageCache", key = "T(java.lang.String).valueOf(#idUtente).concat('-').concat(#strStartDate).concat('-').concat(#strEndDate).concat('-').concat(#locale)")
	public String getDateRange(String idUtente, String strStartDate, String strEndDate, Locale locale) throws Exception {
		logger.info("AgendaFrontPagePortlet getDateRange IN");

		String output;
		DateFormat dfFullFormatForOutput = new SimpleDateFormat("yyyy-MM-dd", locale);

		// Format dates
		Date startDate = dfFullFormatForOutput.parse(strStartDate);
		Date endDate = dfFullFormatForOutput.parse(strEndDate);

		List<FceAgenda> listAgenda = null;
		if (idUtente == null || idUtente.trim().isEmpty()) {
			listAgenda = agendaDAO.getEvents(startDate, endDate, null, null);
		} else {
			listAgenda = agendaDAO.getEvents(startDate, endDate, idUtente, null);
		}

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("events");
		doc.appendChild(rootElement);

		for (FceAgenda a : listAgenda) {
			// event elements
			Element event = doc.createElement("event");
			event.setAttribute("title", a.getTitolo());
			event.setAttribute("start", dfFullFormatForOutput.format(a.getDatainizio()));
			event.setAttribute("color", a.getFceAgendaCategoria().getHexcolore());
			rootElement.appendChild(event);
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		logger.debug("output: {}", output);
		logger.debug("AgendaFrontPagePortlet getDateRange OUT");
		return output;
	}

	@Cacheable(value = "agendaFrontPageCache", key = "T(java.lang.String).valueOf(#idUtente).concat('-').concat(#fullPageUrl).concat('-').concat(#strNumberToLoad).concat('-').concat(#strOffset).concat('-').concat(#locale)")
	public String getPortletContent(String idUtente, String fullPageUrl, String strNumberToLoad, String strOffset, Locale locale) throws Exception {
		int offset = AgendaUtil.strToInt(strOffset);
		int numberToLoad = AgendaUtil.strToInt(strNumberToLoad);
		// strPersonaCobo = (strPersonaCobo == null || strPersonaCobo.trim().length() == 0) ? null : strPersonaCobo;

		// Setup velocity for front page HTML
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		Template t = ve.getTemplate("frontPageAgenda.vm", "iso-8859-1");

		VelocityContext context = new VelocityContext();

		/*****************************************************************************************/

		agendaDAO = (AgendaDAO) SpringApplicationContext.getBean("agendaDAO");
		List<FceAgenda> listAgenda;

		// Check for logged in user then get the upcoming events from DB
		// String idAccount = portal.getUserName(renderRequest);
		// PortalService.checkForUser(strPersonaCobo);
		if (idUtente == null || idUtente.trim().isEmpty()) {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, null, null);
		} else {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, idUtente, null);
		}
		logger.info("listAgenda.size(): " + listAgenda.size());

		DateFormat dfDate = new SimpleDateFormat("dd MMMMM yyyy", locale);
		DateFormat dfTime = new SimpleDateFormat("HH:mm", locale);

		context.put("dfDate", dfDate);
		context.put("dfTime", dfTime);
		context.put("listAgenda", listAgenda);
		context.put("idAccount", idUtente);
		context.put("fullPageUrl", fullPageUrl);

		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		/*****************************************************************************************/

		return writer.toString();
	}

}
