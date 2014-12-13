package it.eng.fascicolo.agenda.portlet;

import it.eng.fascicolo.agenda.component.AgendaService;
import it.eng.fascicolo.agenda.dao.AgendaDAO;
import it.eng.fascicolo.agenda.spring.SpringApplicationContext;
import it.eng.fascicolo.agenda.util.FceAgendaWrapper;
import it.eng.fascicolo.commons.jpa.model.FceAgenda;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;

/***
 * Class to handle requests for the full page version of the agenda
 * @author egross
 *
 */
public class AgendaFullPortlet extends GenericPortlet {

	Logger logger = LoggerFactory.getLogger(AgendaFullPortlet.class);
	
	private static final String VIEW_JSP_PATH = "/jsp/viewAgendaFull.jsp";
	private int numberToLoad;
	private int offset = 0;
	private AgendaDAO agendaDAO;	
	private AgendaService agendaService;
	private List<FceAgenda> previousListAgenda;
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		agendaService=(AgendaService) SpringApplicationContext.getBean("agendaService");
	}

	/***
	 * Method to process the original request
	 */
	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException,
			PortletSecurityException, IOException {
		logger.info("AgendaFullPortlet doView IN");
		Properties prop = new Properties();
		prop.load(AgendaFullPortlet.class.getClassLoader().getResourceAsStream("portlet.properties"));
		numberToLoad = Integer.parseInt(prop.getProperty("numberToLoadFull"));
		try {
			String output = getCalendario(renderRequest);
			logger.info("calendario string: " + output);
			
			renderRequest.setAttribute("events", output);			
			PortletRequestDispatcher prd = this.getPortletContext()
					.getRequestDispatcher(VIEW_JSP_PATH);
			prd.include(renderRequest, renderResponse);
		} catch (Exception e) {
			logger.error("PortalException", e);
			logger.info("AgendaFullPortlet doView out");
		}
	}	
	
	/***
	 * Method to handle the ajax requests from the client
	 */
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {
		logger.info("AgendaFullPortlet serveResource IN");
		
		PortletSession portletSession = request.getPortletSession();
		logger.info("jsessionid portletside: " + portletSession.getId());
		
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		
		agendaDAO = (AgendaDAO) SpringApplicationContext.getBean("agendaDAO");
		
		//Check for and save parameters
		String mostraPiu = request.getParameter("mostraPiu");
		logger.info("mostraPiu: " + mostraPiu);
		String dateRange = request.getParameter("dateRange");
		logger.info("dateRange: " + dateRange);
		String download = request.getParameter("download");
		logger.info("download: " + download);
		String calendarioElenco = request.getParameter("calendarioElenco");
		logger.info("calendarioElenco: " + calendarioElenco);
		String filterElencoByCategory = request.getParameter("filterElencoByCategory");
		logger.info("filterElencoByCategory: " + filterElencoByCategory);
		String aggiungiPreferiti = request.getParameter("aggiungiPreferiti");
		logger.info("aggiungiPreferiti: " + aggiungiPreferiti);
		
		//String strEventId = request.getParameter("eventId");
		
		//Process request based on which parameter is present in url
		try {
			String output = "";			
			if (mostraPiu != null ) {
				output = mostraPiu(request);
				response.setContentType("text");
			} else if (dateRange != null ) {
				// Popolamento degli eventi del calendario
				output = getEventsByDate(request, strPersonaCobo);				
				response.setContentType("text/xml");				
			} else if (download != null ) {
				output = getICal(request, strPersonaCobo);			
				response.setContentType("text/calendar");	
				response.setProperty("Content-Disposition", "attachment; filename=\"Fascicolo_Agenda.ics\"");
			} else if (calendarioElenco != null ) {
				// Popolamento della lista eventi sotto il calendario
				output = getElenco(request, strPersonaCobo);
				response.setContentType("text");
			} else if (filterElencoByCategory != null ) {
				// Popolamento della lista eventi sotto il calendario
				output = getElenco(request, strPersonaCobo);
				response.setContentType("text");
			} else if (aggiungiPreferiti != null) {
				// Gestione del click sul pulsante "preferiti" a fianco dell'evento
				output = gestisciPreferiti(request, strPersonaCobo);
				response.setContentType( "application/json");
			} /* else if (strEventId != null) {				
				output = getEvent(strEventId, strPersonaCobo);				
				response.setContentType("text");
			}*/		

			logger.info("Response data: " + output);
			
			response.resetBuffer();
	        response.getWriter().print(output);
	        response.flushBuffer();
		} catch (Exception e) {
			logger.error("PortalException", e);
			logger.info("AgendaFullPortlet serveResource out");
		}
	}

	/***
	 * Method to get events XML based on a date range passed in as a parameter to populate the calendar
	 * @param request Request containing the parameters
	 * @param strPersonaCobo String representation of the user from the session
	 * @return
	 * @throws Exception
	 */
	private String getEventsByDate(ResourceRequest request, String strPersonaCobo) throws Exception {
		logger.info("AgendaFullPortlet getDateRange IN");
		
		String output;
		DateFormat dfFullFormatForOutput = new SimpleDateFormat("yyyy-MM-dd");
		
		//Get parameters from request
		String strStartDate = request.getParameter("start");
		logger.info("start parameter: " + strStartDate);
		String strEndDate = request.getParameter("end");		
		logger.info("end parameter: " + strEndDate);
		String checkBoxesChecked = request.getParameter("checkBoxesChecked");
		String checkBoxesCheckedOriginal = checkBoxesChecked;
		logger.info("checkBoxesChecked parameter: " + checkBoxesChecked);
		
		//Format dates
		Date startDate = dfFullFormatForOutput.parse(strStartDate);
		Date endDate = dfFullFormatForOutput.parse(strEndDate);
		
		//Check for logged in user then get the upcoming events from DB
		List<FceAgenda> listAgenda  = null;
		String idAccount = checkForUser(strPersonaCobo);
		boolean selezionatoPreferiti=false;
		if (idAccount == null) {
			listAgenda = agendaDAO.getEvents(startDate, endDate, null, checkBoxesChecked);
		} else {
			selezionatoPreferiti=contienePreferito(checkBoxesChecked);
			/*
			 * Se tra le categorie ce n'e' una preferita allora aggiunge le categorie eventualmente
			 * coinvolte nei preferiti che potrebbero mancare dalla selezione fatta dall'utente
			 */
			if (selezionatoPreferiti) {
				checkBoxesChecked=aggiungiEventiPreferitiAllaLista(idAccount,checkBoxesChecked);
			}
			listAgenda = agendaDAO.getEvents(startDate, endDate, idAccount, checkBoxesChecked);
		}	
		logger.info("listAgenda.size(): " + listAgenda.size());
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("events");
		doc.appendChild(rootElement);
		List<BigDecimal> listaPreferiti=agendaService.getPreferitiAgendaIdByUtente(idAccount);
		FceAgendaCategoria fac=agendaDAO.getCategoriaPreferita();
		String colorePreferito=fac.getHexcolore();
		for (FceAgenda a : listAgenda) {
			if (selezionatoPreferiti) {
				if (listaPreferiti!=null && listaPreferiti.contains(a.getIdagenda())) {
					creaEventoDocument(doc, rootElement, a, colorePreferito, dfFullFormatForOutput);
				} else {
					String strAppoggio="categoria-"+a.getFceAgendaCategoria().getIdagendacategoria().longValueExact();
					if (checkBoxesCheckedOriginal.contains(strAppoggio)) {
						creaEventoDocument(doc, rootElement, a, a.getFceAgendaCategoria().getHexcolore(), dfFullFormatForOutput );
					} else {
						// Non fa proprio comparire l'evento sul calendario!
					}
				}
			} else {
				creaEventoDocument(doc, rootElement, a, a.getFceAgendaCategoria().getHexcolore(), dfFullFormatForOutput);
			}
		}
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		
		logger.info("output: " + output);
		logger.debug("AgendaFullPortlet getDateRange OUT");
		return output;
	}
	
	/***
	 * Method to get HTML for initial page load
	 * @param renderRequest
	 * @return
	 * @throws Exception
	 */
	private String getCalendario (RenderRequest renderRequest) throws Exception {
		logger.info("AgendaFullPortlet getCalendario IN");	
		
		//Setup velocity for Calendar HTML
		VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate( "fullPageCalendario.vm" );        
        VelocityContext context = new VelocityContext();        
        StringWriter writer = new StringWriter();
        
        agendaDAO = (AgendaDAO) SpringApplicationContext.getBean("agendaDAO");
        
        PortletSession portletSession = renderRequest.getPortletSession();
		logger.debug("jsessionid portletside: " + portletSession.getId());
		
		//Get user from session
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		logger.info("strPersonaCobo" + strPersonaCobo);

        /*****************************************************************************************/
        
        //Get categories
        List<FceAgendaCategoria> listCategoria = agendaDAO.getCategoria();        
        context.put("listCategoria", listCategoria);
        logger.info("listCategoria.size()" + listCategoria.size());
        
        /*****************************************************************************************/
        
        //Setup velocity for Elenco HTML
        Template t2 = ve.getTemplate( "fullPageElenco.vm" );        
        VelocityContext context2 = new VelocityContext();        
        StringWriter writer2 = new StringWriter();
        
		offset = 0;	
		List<FceAgenda> listAgenda;
		List<FceAgendaWrapper> listAgendaWrapper=null;
		List<BigDecimal> eventiPreferiti=null;
		
		//Check for logged in user then get the upcoming events from DB
		String idAccount = checkForUser(strPersonaCobo);
		if (idAccount == null) {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, null, null);
			listAgendaWrapper=wrappaEventi(listAgenda,null);
		} else {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, idAccount, null);
			eventiPreferiti=agendaService.getPreferitiAgendaIdByUtente(idAccount);
			listAgendaWrapper=wrappaEventi(listAgenda,eventiPreferiti);
		}				
		logger.info("listAgenda.size()" + listAgenda.size());
		
		previousListAgenda = listAgenda;
		
		DateFormat dfFullMonth = new SimpleDateFormat("MMMMM", Locale.ITALIAN);
		DateFormat dfDayNameInWeek = new SimpleDateFormat("EEEEE", Locale.ITALIAN);
		DateFormat dfDayNumberInMonth = new SimpleDateFormat("dd", Locale.ITALIAN);
		DateFormat dfTimeOfDay = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
		DateFormat dfFulldate = new SimpleDateFormat("dd/MM/yyy", Locale.ITALIAN);
		
// VECCHIO		context2.put("listEvents", listAgenda);
		context2.put("listEvents", listAgendaWrapper);
		context2.put("dfDayNameInWeek", dfDayNameInWeek);
		context2.put("dfDayNumberInMonth", dfDayNumberInMonth);
		context2.put("dfFullMonth", dfFullMonth);
		context2.put("dfTimeOfDay", dfTimeOfDay);
		context2.put("dfFulldate", dfFulldate);
		context2.put("fullDatePrev", "");	
		context2.put("personaCobo", strPersonaCobo);
		
		writer2 = new StringWriter();
		t2.merge( context2, writer2 );
		
		context.put("listEvents", writer2.toString());
		context.put("idAccount", idAccount);
		context.put("personaCobo", strPersonaCobo);
		
		/*****************************************************************************************/
		
        writer = new StringWriter();
		t.merge( context, writer );
		
		logger.info("writer.toString()" + writer.toString());
		logger.info("AgendaFullPortlet getCalendario OUT");		
		return writer.toString();
	}
	
	/***
	 * Method to get HTML to display a list of events
	 * @param request
	 * @param strPersonaCobo
	 * @return
	 * @throws Exception
	 */
	private String getElenco (PortletRequest request, String strPersonaCobo) throws Exception {
		logger.info("AgendaFullPortlet getElenco IN");	
		
		//Setup velocity for Calendar HTML
		VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate( "fullPageElenco.vm" );        
        VelocityContext context = new VelocityContext();        
        StringWriter writer = new StringWriter();
        
        /*****************************************************************************************/
		
		DateFormat dfFullFormatForOutput = new SimpleDateFormat("yyyy-MM-dd");
		
		//Get parameters from request
		String strStartDate = request.getParameter("start");
		logger.info("start" + strStartDate);
		String strEndDate = request.getParameter("end");
		logger.info("end" + strEndDate);
		String checkBoxesChecked = request.getParameter("checkBoxesChecked");
		logger.info("checkBoxesChecked" + checkBoxesChecked);
		String checkBoxesCheckedOriginal=checkBoxesChecked;		
		//Format dates
		Date startDate = dfFullFormatForOutput.parse(strStartDate);
		Date endDate = null;
		if (!strEndDate.equals(""))
			endDate = dfFullFormatForOutput.parse(strEndDate);
		
		//Get list of events
		List<FceAgenda> listAgenda=null;
		List<FceAgendaWrapper> listAgendaWrapper=null;
		List<BigDecimal> eventiPreferiti=null;
		String idAccount = checkForUser(strPersonaCobo);
		boolean selezionatoPreferiti=false;
		if (idAccount == null) {
			listAgenda = agendaDAO.getEvents(startDate, endDate, null, checkBoxesChecked);
			listAgendaWrapper=wrappaEventi(listAgenda,null);
		} else {
			selezionatoPreferiti=contienePreferito(checkBoxesChecked);
			/*
			 * Se tra le categorie ce n'e' una preferita allora aggiunge le categorie eventualmente
			 * coinvolte nei preferiti che potrebbero mancare dalla selezione fatta dall'utente
			 */
			if (selezionatoPreferiti) {
				checkBoxesChecked=aggiungiEventiPreferitiAllaLista(idAccount,checkBoxesChecked);
			}
			listAgenda = agendaDAO.getEvents(startDate, endDate, idAccount, checkBoxesChecked);
			eventiPreferiti=agendaService.getPreferitiAgendaIdByUtente(idAccount);
			
			ArrayList<FceAgenda> listaNuova=new ArrayList<FceAgenda>();
			for (FceAgenda a : listAgenda) {
				if (selezionatoPreferiti) {
					if (eventiPreferiti!=null && eventiPreferiti.contains(a.getIdagenda()) ) {
						listaNuova.add(a);
					} else {
						String strApoggio="categoria-"+a.getFceAgendaCategoria().getIdagendacategoria().longValueExact();
						if (checkBoxesCheckedOriginal.contains(strApoggio)) {
							listaNuova.add(a);
						} else {
							// Elimina l'oggetto dalla lista 
							// Lascia andare la classe che non serve
						}
					}
				} else {
					listaNuova.add(a);
				}
			}
			
			listAgendaWrapper=wrappaEventi(listaNuova,eventiPreferiti);
		}		
		logger.info("listAgenda.size()" + listAgenda.size());
		
		DateFormat dfFullMonth = new SimpleDateFormat("MMMMM", Locale.ITALIAN);
		DateFormat dfDayNameInWeek = new SimpleDateFormat("EEEEE", Locale.ITALIAN);
		DateFormat dfDayNumberInMonth = new SimpleDateFormat("dd", Locale.ITALIAN);
		DateFormat dfTimeOfDay = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
		DateFormat dfFulldate = new SimpleDateFormat("dd/MM/yyy", Locale.ITALIAN);
		
// VECCHIO		context.put("listEvents", listAgenda);
		context.put("listEvents", listAgendaWrapper);
		context.put("dfDayNameInWeek", dfDayNameInWeek);
		context.put("dfDayNumberInMonth", dfDayNumberInMonth);
		context.put("dfFullMonth", dfFullMonth);
		context.put("dfTimeOfDay", dfTimeOfDay);
		context.put("dfFulldate", dfFulldate);
		context.put("fullDatePrev", "");		
        context.put("personaCobo", strPersonaCobo);
		
		/*****************************************************************************************/
		
        writer = new StringWriter();
		t.merge( context, writer );
		
		logger.info("writer.toString()" + writer.toString());
		logger.info("AgendaFullPortlet getCalendarioElenco OUT");		
		return writer.toString();
	}
	
	/***
	 * Method to get event
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String mostraPiu (ResourceRequest request) throws Exception {
		logger.info("AgendaFullPortlet mostraPiu IN");	
		
		//Setup verlocity for Elenco HTML
		VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate( "fullPageElenco.vm" );        
        VelocityContext context = new VelocityContext();        
        StringWriter writer = new StringWriter();
        
        /*****************************************************************************************/
       
        PortletSession portletSession = request.getPortletSession();
		logger.debug("jsessionid portletside: " + portletSession.getId());
		
		//Get user from session
		String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);
		logger.info("strPersonaCobo: " + strPersonaCobo);
		
		if (offset == 0) {
			offset = numberToLoad;
		} else {
			offset += numberToLoad;
		}				
		
		List<FceAgenda> listAgenda;
		
		//Check for logged in user then get the upcoming events from DB
		String idAccount = checkForUser(strPersonaCobo);		
		if (idAccount == null) {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, null, null);
		} else {
			listAgenda = agendaDAO.getNextEvents(numberToLoad, offset, idAccount, null);
		}		
		
		if (listAgenda == null) {
			logger.info("AgendaFullPortlet mostraPiu OUT");
			return "";
		} else {
			logger.info("listAgenda.size(): " + listAgenda.size());
			
			DateFormat dfFullMonth = new SimpleDateFormat("MMMMM", Locale.ITALIAN);
			DateFormat dfDayNameInWeek = new SimpleDateFormat("EEE", Locale.ITALIAN);
			DateFormat dfDayNumberInMonth = new SimpleDateFormat("dd", Locale.ITALIAN);
			DateFormat dfTimeOfDay = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
			DateFormat dfFulldate = new SimpleDateFormat("dd/MM/yyy", Locale.ITALIAN);
			
			context.put("listEvents", listAgenda);
			context.put("dfDayNameInWeek", dfDayNameInWeek);
			context.put("dfDayNumberInMonth", dfDayNumberInMonth);
			context.put("dfFullMonth", dfFullMonth);
			context.put("dfTimeOfDay", dfTimeOfDay);
			context.put("dfFulldate", dfFulldate);
			context.put("fullDatePrev", dfFulldate.format(previousListAgenda.get(previousListAgenda.size() - 1).getDatainizio()));		
	        context.put("personaCobo", strPersonaCobo);

	        writer = new StringWriter();
			t.merge( context, writer );
			
			previousListAgenda = listAgenda;
			
			logger.info("writer.toString(): " + writer.toString());
			logger.info("AgendaFullPortlet mostraPiu OUT");			
			return writer.toString();
		}		
		
	}
	
	/***
	 * Method to retrieve an iCal file of all events
	 * @param request Resource request from client
	 * @param strPersonaCobo String representation of the user
	 * @return
	 * @throws Exception
	 */
	private String getICal (ResourceRequest request, String strPersonaCobo) throws Exception {
		logger.info("AgendaFullPortlet getICal IN");
		
		DateFormat dfFullFormatForOutput = new SimpleDateFormat("EEE MMM dd yyyy");		
		
		//Get parameter from request
		String strStartDate = request.getParameter("start");		
		Date startDate = dfFullFormatForOutput.parse(strStartDate);
		
		List<FceAgenda> listAgenda  = null;
		String idAccount = checkForUser(strPersonaCobo);
		if (idAccount == null) {
			listAgenda = agendaDAO.getEvents(startDate, null, null, null);
		} else {
			listAgenda = agendaDAO.getEvents(startDate, null, idAccount, null);
		}
		logger.info("listAgenda.size(): " + listAgenda.size());
		
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Fascicolo//Agenda 1.0//IT"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
	    icsCalendar.getProperties().add(Version.VERSION_2_0);
	    
	    for (FceAgenda a : listAgenda) {
	    	net.fortuna.ical4j.model.Date start = new net.fortuna.ical4j.model.Date(a.getDatainizio());
	    	net.fortuna.ical4j.model.Date end = new net.fortuna.ical4j.model.Date(a.getDatafine());
	    	
	    	VEvent event = new VEvent(start, end, a.getTitolo());
	    	event.getProperties().add(new Uid(String.valueOf(a.hashCode())));
	    	event.getProperties().add(new Description(a.getDescrizione()));
	    	
	    	icsCalendar.getComponents().add(event);
	    }
		
	    logger.info("icsCalendar.toString(): " + icsCalendar.toString());
	    logger.info("AgendaFullPortlet getICal OUT");
		return icsCalendar.toString();
	}
	
	/***
	 * Method to get the idAccount of the user
	 * @param strPersonaCobo
	 * @return
	 * @throws Exception
	 */
	private String checkForUser (String strPersonaCobo) throws Exception {
		logger.info("AgendaFullPortlet checkForUser IN");
		logger.info("strPersonaCobo: " + strPersonaCobo);
		
		if (strPersonaCobo != null) {
			ObjectMapper mapper = new ObjectMapper();
			PersonaCoboView personaCobo = mapper.readValue(strPersonaCobo, PersonaCoboView.class);
			logger.info("personaCobo: " + personaCobo);
			
			logger.info("AgendaFullPortlet checkForUser OUT");
			return personaCobo.getIdAccount();
		}
		
		logger.info("AgendaFullPortlet checkForUser OUT");
		return null;
	}

	private String gestisciPreferiti(ResourceRequest request, String strPersonaCobo) throws Exception {
		String str = request.getParameter("aggiungiPreferiti");
		String idAgenda=request.getParameter("idAgenda");
		logger.debug("idUtente="+" idAgenda"+idAgenda);
		JSONObject jsonObject = new JSONObject();
		String idAccount = checkForUser(strPersonaCobo);

		if (str.trim().equalsIgnoreCase("S")) {
			agendaService.aggiungiPreferiti(idAccount, idAgenda);
			jsonObject.put("preferito", "aggiunto");
		} else {
			agendaService.rimuoviPreferiti(idAccount, idAgenda);
			jsonObject.put("preferito", "rimosso");
		}
		return jsonObject.toString();
	}
	
	/*private String getEvent(String strEventId, String strPersonaCobo) throws Exception {
	logger.debug("AgendaFullPortlet getEvent IN");
	logger.debug("strEventId = " + strEventId);
	
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    ve.init();
    
    Template t = ve.getTemplate( "getEvent.vm" );
    
    VelocityContext context = new VelocityContext();
    
    *//*****************************************************************************************//*
    
	BigDecimal eventId = new BigDecimal(strEventId);
	agendaDAO = (AgendaDAO) SpringApplicationContext.getBean("agendaDAO");
	FceAgenda event = agendaDAO.getEvent(eventId);
	
	FceUtente utente = event.getFceUtente();
	String idAccount = checkForUser(strPersonaCobo);
	
	if (utente == null || utente.getIdAccount().equals(idAccount)) {
		DateFormat dfFullFormat = new SimpleDateFormat("EEEEE dd MMMMM", Locale.ITALIAN);
		String strFullFormat = dfFullFormat.format(event.getDatainizio());
		DateFormat dfTimeOfDay = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
		String strTimeOfDay = dfTimeOfDay.format(event.getDatainizio());			 
        
        context.put("strFullFormat", strFullFormat);
        context.put("strTimeOfDay", strTimeOfDay);
        context.put("categoria", event.getFceAgendaCategoria().getNome());
        context.put("titolo", event.getTitolo());
        context.put("descrizione", event.getDescrizione());
        
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        
        logger.debug("AgendaFullPortlet getEvent OUT");
        return writer.toString();
	} else {
		logger.debug("AgendaFullPortlet getEvent OUT");
		return "Non hai i permessi per vedere questo evento";
	}		
	
	*//*****************************************************************************************//*
}*/
	/*
	 * Classe per wrappare l'evento agenda aggiungendo la proprietà preferito
	 */
	private List<FceAgendaWrapper> wrappaEventi(List<FceAgenda> listAgenda, List<BigDecimal> eventiPreferiti) {
		List<FceAgendaWrapper> l=null;
		if (listAgenda!=null) {
			l=new ArrayList<FceAgendaWrapper>();
			for (FceAgenda agenda : listAgenda) {
				FceAgendaWrapper wrapper=new FceAgendaWrapper(agenda);
				if (eventiPreferiti!=null && eventiPreferiti.contains(agenda.getIdagenda())) {
					wrapper.setPreferito(true);
				}
				l.add(wrapper);
			}
		}
		return l;
	}

	/*
	 * Determina se nell'elenco delle categorie selezionate è presente una categoria
	 * preferita. Torna true se esiste.
	 */
	private boolean contienePreferito(String checkBoxesChecked) {
		boolean ret=false;
		List<FceAgendaCategoria> l=agendaDAO.getCategorieByList(checkBoxesChecked);
		if (l!=null) {
			for (FceAgendaCategoria categoria : l) {
				if (categoria.getFlagPreferito().startsWith("S")) {
					ret=true;
					break;
				}
			}
		}
		return ret;
	}

	/*
	 * Aggiunge alla stringa con l'elenco delle categorie le categorie in più estratte dagli eventi
	 * preferiti dell'utente. Le aggiunge solo se già non esistono in lista
	 */
	private String aggiungiEventiPreferitiAllaLista(String idAccount, String checkBoxesChecked) {
		String nuovo=checkBoxesChecked;
		if (nuovo!=null) {
			List<FceAgendaCategoria> l=agendaService.getCategorieEventiPreferitiUtente(idAccount);
			if(l!=null) {
				String aggiunta=null;
				for(FceAgendaCategoria f : l) {
					aggiunta="categoria-"+f.getIdagendacategoria().longValueExact();
					if (!checkBoxesChecked.contains(aggiunta)) {
						nuovo+=","+aggiunta;
					}
				}
			}
		}
		return nuovo; 
	}

	private void creaEventoDocument(Document doc, Element rootElement, FceAgenda a, String colore, DateFormat dfFullFormatForOutput ) {
		Element event = doc.createElement("event");
		event.setAttribute("title", a.getTitolo());
		event.setAttribute("start", dfFullFormatForOutput.format(a.getDatainizio()));
		event.setAttribute("color", colore);
		event.setAttribute("id", a.getIdagenda().toString());
		event.setAttribute("idCategoria", a.getFceAgendaCategoria().getIdagendacategoria().toPlainString());
		rootElement.appendChild(event);
	}

}
