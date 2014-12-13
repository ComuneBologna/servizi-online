package it.eng.fascicolo.intornoame.portlet;

import it.eli4you.engines.masterindex.core.xml.input.MessaggioInputType;
import it.eli4you.engines.masterindex.core.xml.input.ParamType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eli4you.engines.masterindex.core.xml.output.ElementoType;
import it.eli4you.engines.masterindex.core.xml.output.FaultType;
import it.eli4you.engines.masterindex.core.xml.output.ViewType;
import it.eng.fascicolo.commons.jpa.model.FceIntornoameCategoria;
import it.eng.fascicolo.commons.jpa.model.FceIntornoameKml;
import it.eng.fascicolo.commons.profilazione.bo.PersonaCoboView;
import it.eng.fascicolo.intornoame.beans.CategoriaKmlBean;
import it.eng.fascicolo.intornoame.service.IntornoAMeService;
import it.eng.fascicolo.intornoame.spring.SpringApplicationContext;
import it.service.integration.Eli4UServiceInvoker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IntornoAMePortlet extends GenericPortlet {

	Logger logger = LoggerFactory.getLogger(IntornoAMePortlet.class);
	
	private static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	private static final String VIEW_JSP_PATH = "/jsp/view.jsp";
	final public static String IDKML = "idkml";

	private IntornoAMeService intornoAMeService;
	
	private String distanzaKmDalCentroCitta=null;
	private String urlAcsorService=null;
	private String acsorService=null;
	private String defaultIconMap=null;
	private String longitudineCentroCitta=null;
	private String latitudineCentroCitta=null;
	private String urlRicercaLuogo=null;
	private String coordinatePoligonoRicerca=null;
	private String usernameAcsor=null;
	private String passwordAcsor=null;
	
	@Override
	public void init(PortletConfig config) throws PortletException {
		super.init(config);
		distanzaKmDalCentroCitta=config.getInitParameter("distanzaKmDalCentroCitta");
		urlAcsorService=config.getInitParameter("urlAcsorService");
		acsorService=config.getInitParameter("acsorService");
		defaultIconMap=config.getInitParameter("defaultIconMap");
		longitudineCentroCitta=config.getInitParameter("longitudineCentroCitta");
		latitudineCentroCitta=config.getInitParameter("latitudineCentroCitta");
		urlRicercaLuogo=config.getInitParameter("urlRicercaLuogo");
		coordinatePoligonoRicerca=config.getInitParameter("coordinatePoligonoRicerca");
		usernameAcsor=config.getInitParameter("usernameAcsor");
		passwordAcsor=config.getInitParameter("passwordAcsor");
	}

	@Override
	public void doView(RenderRequest renderRequest,
			RenderResponse renderResponse) throws PortletException,
			PortletSecurityException, IOException {

		renderRequest.setAttribute("defaultIconMap", defaultIconMap);
		renderRequest.setAttribute("distanzaKmDalCentroCitta", distanzaKmDalCentroCitta);
		renderRequest.setAttribute("longitudineCentroCitta", longitudineCentroCitta);
		renderRequest.setAttribute("latitudineCentroCitta", latitudineCentroCitta);
		renderRequest.setAttribute("urlRicercaLuogo", urlRicercaLuogo);
		renderRequest.setAttribute("coordinatePoligonoRicerca", coordinatePoligonoRicerca);

		try {
			PortletSession portletSession = renderRequest.getPortletSession();
			logger.debug("jsessionid portletside: " + portletSession.getId());
			String strPersonaCobo = (String) portletSession.getAttribute(PersonaCoboView.SESSION_KEY, PortletSession.APPLICATION_SCOPE);			
//			renderRequest.setAttribute("personaCoboJSON", strPersonaCobo);
			
			String codiceFiscale=null;
			if (strPersonaCobo!=null){
				ObjectMapper mapper = new ObjectMapper(); 
				PersonaCoboView personaCobo = mapper.readValue (strPersonaCobo, PersonaCoboView.class);
				// Se l'Account è di tipo FEDERA ed è presente il CODICE FISCALE...
				renderRequest.setAttribute("utenteLoggato", "true");
				renderRequest.setAttribute("codiceFiscale", personaCobo.getCf());
				renderRequest.setAttribute("tipoAccount", personaCobo.getTipoAccount());

				if (personaCobo!=null && personaCobo.getTipoAccount().equals("Federa") && personaCobo.getCf()!=null){
					codiceFiscale = personaCobo.getCf();
					// Invocazione del servizio ACSOR
			        MessaggioInputType messaggioInput = new MessaggioInputType();
					ParamType param = new ParamType();
					param.setInternalKey("CODICE_ENTE");
					param.setExternalKey("CODICE_ENTE");
					param.setValue("A944");
					messaggioInput.getParam().add(param);
					param = new ParamType();
					param.setInternalKey("CODICE_FISCALE");
					param.setExternalKey("CODICE_FISCALE");
					param.setValue(codiceFiscale);
					messaggioInput.getParam().add(param);
			        Eli4UServiceInvoker serviceInvoker = new Eli4UServiceInvoker();
			        String indirizzoResidenza=null;
			        try {
			        	DettaglioType dettaglioType = serviceInvoker.invokeService(
			        			urlAcsorService, acsorService, usernameAcsor, passwordAcsor, messaggioInput);
						FaultType fau=dettaglioType.getFault();
						if (fau!=null) {
							logger.error("Errore nell'invocaizone del servizio ACSOR: {}", fau.getCode(),fau.getDescription());
						} else {
							List<ViewType> l=dettaglioType.getView();
							Iterator<ViewType> it=l.iterator();
							while (it.hasNext()) {
								ViewType type = it.next();
								List<ElementoType> le=type.getElemento();
								Iterator<ElementoType> itElem=le.iterator();
								while (itElem.hasNext()) {
									ElementoType elemType = itElem.next();
									if(elemType.getCampo().getNome().equals("INDIRIZZO_RESIDENZA")) {
										indirizzoResidenza=elemType.getCampo().getSetValori().getValore();
										logger.debug("Indirizzo di residenza dell'utente: "+indirizzoResidenza);
										break;
									}
								}
							}
						}
						
			        	renderRequest.setAttribute("indirizzoResidenza", indirizzoResidenza);
			        } catch (Exception e) {
			        	logger.error("Errorre nell'invocazione del servizio ACSOR", e);
			        }
					
				}
			}
			
			renderResponse.setContentType(CONTENT_TYPE_HTML);
			intornoAMeService = (IntornoAMeService) SpringApplicationContext.getBean("intornoAMeService");
			List<FceIntornoameCategoria> categorie = intornoAMeService.getElencoCategorieKmlAttiveSorted();
			
			ArrayList<CategoriaKmlBean> listaBean=intornoAMeService.getCategorieKmlAttiveBeans();
			if (listaBean.size()>0) {
				ObjectMapper om=new ObjectMapper();
				String appoggio=om.writeValueAsString(listaBean);
				renderRequest.setAttribute("categorieKmlJSON", appoggio);
			}
			
			renderRequest.setAttribute("visual", generateViewHtml(categorie, strPersonaCobo==null ? false : true));
			PortletRequestDispatcher prd = this.getPortletContext()
					.getRequestDispatcher(VIEW_JSP_PATH);
			prd.include(renderRequest, renderResponse);
			logger.debug("IntornoAMePortlet OUT");

		} catch (Exception e) {
			logger.error("PortalException", e);
			super.doView(renderRequest, renderResponse);
			logger.debug("IntornoAMePortlet out");
		}

	}

	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws PortletException, IOException {

		FceIntornoameKml fceIntornoameKml = null;
		String idKml = (String) request.getParameter(IDKML);
		try {
			if (idKml == null || "".equals(idKml)) {
				writeOutput("Kml non valido.".getBytes(), response);
				return;
			}

			fceIntornoameKml = intornoAMeService.getKmlById(new BigDecimal(idKml));
			InputStream in = fceIntornoameKml.getKmlfile().getAsciiStream();
			StringWriter w = new StringWriter();
			IOUtils.copy(in, w);
			String kml = w.toString();
			
			kml=kml.replaceAll("\\$\\{ContextPath\\}", request.getContextPath()+"/img");
			
			logger.debug("KML MODIFICATO: "+kml);
			
			if (kml != null) {
				logger.debug("Dimensione del kml [" + kml.length() + "]");
				kml = kml.replaceAll("&lt;", "<");
				kml = kml.replaceAll("&gt;", ">");
				kml = kml.replace("<![CDATA[INIT", "");
				kml = kml.replace("END]]>", "");
				logger.trace("Kml [" + kml + "]");
				writeOutput(kml.getBytes(), response);
			}
		} catch (Exception e) {
			logger.error("Si e' verificato un errore durante il cariamento del kml {}", idKml, e);
		}

	}

	private void writeOutput(byte[] buff, ResourceResponse response)
			throws Exception {
		
		response.getWriter().write(new String(buff).toCharArray());
		response.flushBuffer();
	}

	/**
	 * Genera la stringa html per la pagina di visualizzazione dati profilo utente
	 * @return
	 */
	private String generateViewHtml(List<FceIntornoameCategoria> categorie, boolean utenteLoggato) {
		
		VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        
        Template t = ve.getTemplate("intornoame.template.vm.html","utf8");
        VelocityContext context = new VelocityContext();
        
        ArrayList<Map<String, String>> al = new ArrayList<Map<String, String>>();
        Map<String, String> mappa=null;
        FceIntornoameCategoria fceIntornoameCategoria = null;
        
        Iterator<FceIntornoameCategoria> it=categorie.iterator();
        while (it.hasNext()) {
			fceIntornoameCategoria =  it.next();
			mappa=new HashMap<String, String>();
			mappa.put("id", fceIntornoameCategoria.getIdcategoria().toString());
			mappa.put("descrizione", fceIntornoameCategoria.getDescrizione());
			al.add(mappa);
		}
        
        context.put("elencoCategorie", al);
        if (utenteLoggato) {
            context.put("titoloPortlet", "Intorno a me");
        } else {
            context.put("titoloPortlet", "Intorno a te");
        }

        /*
         * Compone il testo descrittivo della portlet prelevando la descrizione delle categorie
         * separate da virgole e da una "e" per l'ultimo elemento.
         */
        StringBuffer testoPortlet=new StringBuffer("Esplora la mappa per avere informazioni su ");
        it=categorie.iterator();
        int numeroCategorie=categorie.size();
        int contatore=0;
        while (it.hasNext()) {
			fceIntornoameCategoria = it.next();
			contatore++;
			if (contatore==numeroCategorie) {
				testoPortlet.append(" e " + fceIntornoameCategoria.getDescrizione()+".");
			} else {
				if(contatore==1) {
					testoPortlet.append(fceIntornoameCategoria.getDescrizione());
				} else {
					testoPortlet.append(", "+fceIntornoameCategoria.getDescrizione());
				}
			}
		}
        context.put("testoPortlet", testoPortlet.toString());
        // Fine composizione testo descrittivo.
        
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        
        return writer.toString();
		
	}
	
}


