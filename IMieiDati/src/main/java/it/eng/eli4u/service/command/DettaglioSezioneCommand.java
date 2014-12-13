package it.eng.eli4u.service.command;

import it.eli4you.engines.masterindex.core.xml.input.MessaggioInputType;
import it.eli4you.engines.masterindex.core.xml.input.ParamType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eng.eli4u.imieidati.model.ParametroServizio;
import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.IEli4uCachedInvokerService;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;
import it.eng.eli4u.imieidati.services.eli4u.DettaglioEntry;
import it.eng.eli4u.imieidati.utils.DettaglioWrapper;
import it.eng.eli4u.service.velocity.HtmlRenderer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class DettaglioSezioneCommand implements IServiceCommand {
	private JAXBContext jaxbContext = null;
	private final Logger logger = LoggerFactory.getLogger(DettaglioSezioneCommand.class);
	
	@Autowired
	IDatabaseService propertiesDbServiceImpl;
	@Autowired
	IIMieiDatiPortalService portalService;
	@Autowired
	IEli4uCachedInvokerService invokerService;
	
	
	public DettaglioSezioneCommand() {
		try {
			jaxbContext = JAXBContext.newInstance(DettaglioType.class);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public String execute(ResourceRequest req) throws Exception {

		String id = req.getParameter("id");
		// if null, try to find valid section index
		if (id == null || id.length() == 0) {
			id = (String)req.getAttribute("section_id");
		}

		// 1. prendo la sezione dal database

		Sezione sezione = propertiesDbServiceImpl.findSezioneById(id);

		// 2. controllo se è valorizzato il campo htmlStatico -> se si torno
		// quello
		if (sezione.getHtmlStatico() != null && !sezione.getHtmlStatico().trim().isEmpty()) {
			return sezione.getHtmlStatico();
		}

		// 3. controllo se la richiesta è fatta da mobile o meno TODO
		// 4. prendo l'xsl salvato per la sezione (o quello mobile)
		String uriXsl = null;
//PL 17/10/2014 temporaneamente disabilitato, da poortlet richiede api server dep
		
//		if (req.getHeader("User-Agent").toLowerCase().indexOf("mobile") != -1) {
//			// you're in mobile land
//			uriXsl = sezione.getUriXslMobile();
//		} else {
//			// nope, this is probably a desktop
//			uriXsl = sezione.getUriXsl();
//		}
		uriXsl = sezione.getUriXsl();
		// 5. se non c' neanche xsl prendo xsl di default
		if (uriXsl == null || sezione.getUriXsl().trim().isEmpty()) {
			uriXsl = "default.xsl";
		}

		// 6. chiamo il servizio con i parametri della sezione (torna un oggetto
		// DETTAGLIO TYPE) TODO
		MessaggioInputType messaggioInput = getParametriServizioFromParametriSezione(
				sezione, req);

		String userId = portalService.getUserName(req.getPortletSession());
		String uniqueIdSezione = sezione.getCodice();
		DettaglioEntry dettaglioCached = invokerService.invokeServiceCacheable(
				sezione.getUrlServizio(), sezione.getNomeServizio(),
				sezione.getUsernameServizio(), sezione.getPasswordServizio(), userId, uniqueIdSezione,
				messaggioInput);
		DettaglioType dettaglioType = invokerService.fromDettaglioEntry(dettaglioCached);
		/**  TODO - OLD internationalization - REMOVE
		// 7. serializzo dettaglioType come stringa
		if (!sezione.getCodice().equals(
				IMieiDatiConstants.CODICE_SEZIONE_I_MIEI_DATI)) {

			dettaglioType = localizeAllTitlesOfDettaglio(dettaglioType, req,
					propertiesDbServiceImpl, sezione);
		}
		**/

		String renderingClass = sezione.getRenderingClass();
		String htmlFile = "";
		
		// 8. applico la trasformazione Java, se esiste
		if (renderingClass != null && renderingClass.length() > 0) {
			htmlFile = doTrasformazioneJava(messaggioInput, dettaglioType, renderingClass);
		} else {
		// 9. applico la trasformazione XSL
			htmlFile = doTrasformazioneXsl(dettaglioType, uriXsl);
		}
		
		// 10. internazionalizzazione
		htmlFile = internationalize(htmlFile, req, propertiesDbServiceImpl);
		
		// 10. torno il risultato
		return htmlFile;

	}

	/*
	 * Apply internationalization to an HTML text
	 */
	private String internationalize(String html, ResourceRequest request, IDatabaseService propertiesDbServiceImpl) throws Exception {
		String intl = "";
		
		// Get Locale from Liferay or WebSphere portal service
		String locale = portalService.linguaPortale(request);
		if (locale == null || locale.length() == 0) {
			locale = "en";
		}
		
		List<Localizzazione> listaLocalizzazioni = propertiesDbServiceImpl.listaLocalizzazioni(locale);
		intl = translate(html, listaLocalizzazioni);
		
		return intl;
	}
	

	private String translate(String html, List<Localizzazione> listaLocalizzazioni) throws Exception {
		// get and initialize the engine
		VelocityEngine ve = new VelocityEngine();
		//ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		//ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
		ve.setProperty("string.resource.loader.class", "it.eng.eli4u.service.velocity.StringResourceLoader");
		//ve.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
		//ve.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
		//ve.setProperty("string.resource.loader.encoding", "UTF-8"); 
		
		ve.init();
		VelocityContext context = new VelocityContext();
		
		// fill context with localized data
		for (Localizzazione localizzazione : listaLocalizzazioni) {
			String value = localizzazione.getValore();
			value = value == null ? "" : value;
			String utf8_Value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
			context.put(localizzazione.getChiave(), utf8_Value);
		}
		
		//  get the Template and apply it
		//Template t = ve.getTemplate(VELOCITY_TEMPLATE, "UTF-8");
		Template t = ve.getTemplate(html, "UTF-8");
		StringWriter writer = new StringWriter();
		t.merge( context, writer );

		return writer.toString();
	}

	private String doTrasformazioneJava(MessaggioInputType messaggioInput, DettaglioType dettaglioType, String renderingClass) throws Exception {
		String result = "";
		if (renderingClass != null) {
			String clazz = "it.eng.eli4u.service.velocity." + renderingClass;
			
			HtmlRenderer renderer = (HtmlRenderer)Class.forName(clazz).newInstance();
			result = renderer.render(messaggioInput, dettaglioType);
		}
		return result;
	}

	private String doTrasformazioneXsl(DettaglioType dettaglioType,
			String uriXsl) {
		DettaglioWrapper dettaglioWrapper = new DettaglioWrapper();
		dettaglioWrapper.setCurrentViewElements(dettaglioType);
		return dettaglioWrapper.getCurrentViewElements(uriXsl);
	}

	/**
	 * This method is used to convert the section params to a MessaggioInputType
	 * and return it
	 * 
	 * @param sezione
	 *			the section
	 * @param req
	 *			the request
	 * @return the message with all params for calling the service
	 * @throws Exception 
	 */
	private MessaggioInputType getParametriServizioFromParametriSezione(
			Sezione sezione, ResourceRequest req) throws Exception {

		MessaggioInputType messaggioInput = new MessaggioInputType();

		for (ParametroServizio paramServizio : sezione.getParametriInputServizio()) {

			ParamType param = new ParamType();
			param.setInternalKey(paramServizio.getCodice());
			param.setExternalKey(paramServizio.getCodice());

			if (paramServizio.getValore().startsWith("$P{")
					&& paramServizio.getValore().endsWith("}")) {

				String valore = paramServizio.getValore();
				String codice = valore.substring(3, paramServizio.getValore()
						.length() - 1);
				String value = portalService.parametroPortale(req, codice);

				param.setValue(value);
			}

			else {

				param.setValue(paramServizio.getValore());
			}

			messaggioInput.getParam().add(param);
		}

		return messaggioInput;
	}

	/**
	 * This method is used to localize all sections content with tag titolo
	 * 
	 * @param dettaglioType
	 *			the detail xml
	 * @param sezione
	 *			the section
	 * @param req
	 *			the request
	 * @param propertiesDbServiceImpl
	 *			the istance of class PropertyDatabaseServiceImpl for calling
	 *			localize method
	 * @return the message with all params for calling the service
	 */
	private DettaglioType localizeAllTitlesOfDettaglio(
			DettaglioType dettaglioType, ResourceRequest req,
			IDatabaseService propertiesDbServiceImpl, Sezione sezione)
			throws IOException, JAXBException, DocumentException {

		// DettaglioType --> String
		String dettaglioString = marshal(dettaglioType);

		Document document = DocumentHelper.parseText(dettaglioString);

		// Get Locale from Liferay Service
		String locale = portalService.linguaPortale(req);

		//Iterate all elements and all attributes to find title attributes
		Element root = document.getRootElement();
		iterateAttributes(root, sezione, propertiesDbServiceImpl, locale);

		iterateElementCHild(root, sezione, propertiesDbServiceImpl, locale);
		
		dettaglioString = document.asXML();

		dettaglioType = unmarshal(dettaglioString);

		return dettaglioType;
	}
	
	/**
	 * This method is used to iterate through elements child
	 * 
	 * @param root root element
	 * @param sezione the section	
	 * @param locale current language code
 	 * @param propertiesDbServiceImpl
	 *			the istance of class PropertyDatabaseServiceImpl for calling
	 *			localize method		
	 */
	private void iterateElementCHild(Element root, Sezione sezione,
			IDatabaseService propertiesDbServiceImpl, String locale) throws IOException{
		
		//iterate through elements child
		 for ( Iterator i = root.elementIterator(); i.hasNext(); ) 
		 {
		  Element row = (Element) i.next();
			iterateAttributes(row, sezione, propertiesDbServiceImpl, locale);

		  Iterator itr = row.elementIterator();
		  while(itr.hasNext())
			   {
				Element child = (Element) itr.next();
				//do what ever you want, I will just print
				iterateAttributes(child, sezione, propertiesDbServiceImpl, locale);
				
				iterateElementCHild(child, sezione, propertiesDbServiceImpl, locale);

				}
	   }
	}

	/**
	 * This method is used to iterate through all elemts' attributes for finding titolo attribute
	 * Localize the attribute, if message is null, leave the current message
	 * @param element root element
	 * @param sezione the section	
	 * @param locale current language code
 	 * @param propertiesDbServiceImpl
	 *			the istance of class PropertyDatabaseServiceImpl for calling
	 *			localize method		
	 */
	private void iterateAttributes(Element element, Sezione sezione,
			IDatabaseService propertiesDbServiceImpl, String locale)
			throws IOException {

		// iterate through attributes of element
		for (Iterator j = element.attributeIterator(); j.hasNext();) {
			Attribute attribute = (Attribute) j.next();
			String attributeName = attribute.getName();

			if (attributeName.equals("titolo")) {
				
				String messageLocalized = propertiesDbServiceImpl.mesaggio(locale, "sezione." + sezione.getId() + ".dettaglio."
								+ element.getName() + ".titolo");
				if(messageLocalized != null)
				attribute.setValue(messageLocalized);
			}
		}
	}

	/**
	 * This method is used to convert the xml object to a string and return it
	 * 
	 * @param dettaglioType
	 *			is the object that contains the xml
	 * @return xml String
	 */
	public String marshal(DettaglioType dettaglioType) throws JAXBException {
		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter w = new StringWriter();
		m.marshal(dettaglioType, w);
		return w.toString();
	}

	/**
	 * This method is used to convert a string to xml object and return it
	 * 
	 * @param xml
	 *			is the string
	 * @return DettaglioType the object that contains the xml
	 */
	public DettaglioType unmarshal(final String xml) throws JAXBException {
		return (DettaglioType) jaxbContext.createUnmarshaller().unmarshal(new StringReader(xml));
	}

	/**
	 * This method is used to load the xml file to a document and return it
	 * 
	 * @param xmlFileName
	 *			is the xml file name to be loaded
	 * @return Document
	 */
	public Document getDocument(final String xmlFileName) {
		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(xmlFileName);
		} catch (DocumentException e) {
			logger.error(e.getMessage(), e);
		}
		return document;
	}

}
