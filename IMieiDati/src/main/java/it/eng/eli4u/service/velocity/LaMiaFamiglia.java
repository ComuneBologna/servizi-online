package it.eng.eli4u.service.velocity;

import it.eli4you.engines.masterindex.core.xml.input.MessaggioInputType;
import it.eli4you.engines.masterindex.core.xml.input.ParamType;
import it.eli4you.engines.masterindex.core.xml.output.CampoType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eli4you.engines.masterindex.core.xml.output.ElementoType;
import it.eli4you.engines.masterindex.core.xml.output.FaultType;
import it.eli4you.engines.masterindex.core.xml.output.ViewType;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LaMiaFamiglia implements HtmlRenderer {
	private final Logger logger = LoggerFactory.getLogger(LaMiaFamiglia.class);
	
	public static final String VELOCITY_TEMPLATE = "velocity/la_mia_famiglia.vm.html";
	
    public static void main(String[] args) throws Exception {
    	LaMiaFamiglia obj = new LaMiaFamiglia();
    	
    	String result = obj.render("", "");

        // show output, for debug purpose
        System.out.println(result);
        
        // save to a file
        String outputFileName = "/home/domenico/Downloads/bo-fascicolo-e917/test_out.html";
        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(result.getBytes());
        fos.flush();
        fos.close();
    }
    
    	
    @Override
	public String render(Object request, Object response) throws Exception {
    	// get and initialize the engine
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        VelocityContext context = new VelocityContext();
        
        if (response != null && response.equals("")) {
        	buildTestData(context);
        } else {
        	// get CODICE_FISCALE from request
        	String codiceFiscaleInput = "";
        	if (request != null && request instanceof MessaggioInputType) {
        		MessaggioInputType inputMessage = (MessaggioInputType)request;
        		for (ParamType param : inputMessage.getParam()) {
        			if (param.getInternalKey().equalsIgnoreCase("CODICE_FISCALE")) {
        				codiceFiscaleInput = param.getValue();
        				break;
        			}
        		}
        	}
        	// evaluate response
        	if (response != null && response instanceof DettaglioType) {
        		DettaglioType dettaglioFonte = (DettaglioType)response;
        	
        		// check for 
        		buildData(context, dettaglioFonte, codiceFiscaleInput);
        	}
        }
        
		logger.info("Using VELOCITY_TEMPLATE: " + VELOCITY_TEMPLATE);

        //  get the Template and apply it
        Template t = ve.getTemplate(VELOCITY_TEMPLATE, "UTF-8");
        StringWriter writer = new StringWriter();
        t.merge( context, writer );

        return writer.toString();
    }
    

    private void buildData(VelocityContext context, DettaglioType dettaglioFonte, String codiceFiscaleInput) {
    	FaultType faultType = dettaglioFonte.getFault();
    	if (faultType != null) {
    		// build FAULT info
            Map fault = new HashMap();
            fault.put("code", faultType.getCode());
            fault.put("description", faultType.getDescription());
            
         // add that object to a VelocityContext
            context.put("fault", fault);
            return;
    	}
    	
    	
        // prepare data
    	String codiceFiscaleOutput = "";
        ArrayList familiariList = new ArrayList();
    	// TODO - Change locale for internationalization
    	Locale locale = Locale.ITALY;
    	int index = 1;
    	
    	// read views
    	for (ViewType view : dettaglioFonte.getView()) {
			Map persona = new HashMap();
			String nome = "";
			String cognome = "";
			String luogo_nascita = "";
			String data_nascita = "";
			String luogo_provenienza = "";
			String data_provenienza = "";
			String relazione_parentale = "";
			
			for (ElementoType elemento : view.getElemento()) {
				CampoType campo = elemento.getCampo();
				String valore = campo.getSetValori().getValore();
				if (campo.getId().equals("NOME")) {
					nome = valore;
				} else if (campo.getId().equals("COGNOME")) {
					cognome = valore;
				} else if (campo.getId().equals("DATA_NASCITA")) {
					data_nascita = valore;
				} else if (campo.getId().equals("LUOGO_NASCITA")) {
					luogo_nascita = valore;
				} else if (campo.getId().equals("DATA_IMMIGRAZIONE")) {
					data_provenienza = valore;
				} else if (campo.getId().equals("LUOGO_IMMIGRAZIONE")) {
					luogo_provenienza = valore;
				} else if (campo.getId().equals("CODICE_FISCALE")) {
					codiceFiscaleOutput = valore;
					String formattedCF = FormatUtil.formatCodiceFiscale(valore);
					persona.put("codice_fiscale", formattedCF);
				} else if (campo.getId().equals("PARTITA_IVA")) {
					if (valore.length() > 0) {
						persona.put("partita_iva", valore);
					}
				} else if (campo.getId().equals("SESSO")) {
					persona.put("sesso", valore);
				} else if (campo.getId().equals("INDIRIZZO_RESIDENZA")) {
					persona.put("residente_in", valore);
				} else if (campo.getId().equals("IDR_TIP_REL_PAR")) {
					relazione_parentale = valore;
				}
			
			}
			
			// fill remaining elements
			persona.put("nome", nome + " " + cognome);
			persona.put("nato_a", luogo_nascita + ", " + FormatUtil.formatDate(data_nascita, locale));
			if (data_provenienza.length() > 0) {
				persona.put("proveniente_da", luogo_provenienza + ", " + FormatUtil.formatDate(data_provenienza, locale));
			}
			persona.put("relazione_parentale", relazione_parentale);
			if (codiceFiscaleOutput.equals(codiceFiscaleInput)) {
				context.put("intestatario", persona);
			} else {
				persona.put("index_persona", index++);
				familiariList.add(persona);
			}
			persona = null;
    		
    	}

        // add that list to a VelocityContext
        context.put("familiariList", familiariList);
		
	}
	

	private void buildTestData(VelocityContext context) {
        // prepare data
        ArrayList familiariList = new ArrayList();
        
        /***  // TEST fault
        Map fault = new HashMap();
        fault.put("code", "012");
        fault.put("description", "Errore nell'invocazione del servizio SERVICE-NAME");
        
     // add that object to a VelocityContext
        context.put("fault", fault);
        ***/
        
        
        Map intestatario = new HashMap();
        intestatario.put("nome", "Massimiliano Passarella");
        intestatario.put("nato_a", "Milano, 23 agosto 1962");
        intestatario.put("residente_in", "Via Verdi, 27");
        intestatario.put("proveniente_da", "Modena, 12 settembre 1986");
        intestatario.put("codice_fiscale", "PSS MSM 62M23 F205 I");
        intestatario.put("partita_iva", "10325221009");
        intestatario.put("sesso", "M");
        intestatario.put("relazione_parentale", "INTESTATARIO SCHEDA");
        
        // addobject to a VelocityContext
        context.put("intestatario", intestatario);
        
        
        Map familiare = new HashMap();
        familiare.put("nome", "Maura Pelloni");
        familiare.put("nato_a", "Modena, 2 agosto 1966");
        familiare.put("codice_fiscale", "PLL MRA 66M42 F257 E");
        familiare.put("sesso", "F");
        familiare.put("relazione_parentale", "MOGLIE");
        
        familiariList.add(familiare);
        
        
        familiare = new HashMap();
        familiare.put("nome", "Giovanni Passarella");
        familiare.put("nato_a", "Modena, 15 giugno 1989");
        familiare.put("codice_fiscale", "PSS GNN 89G15 F257 A");
        familiare.put("sesso", "M");
        familiare.put("relazione_parentale", "FIGLIO");
        
        familiariList.add(familiare);
        
        
        familiare = new HashMap();
        familiare.put("nome", "Stefania Passarella");
        familiare.put("nato_a", "Modena, 4 novembre 1992");
        familiare.put("codice_fiscale", "PSS SFN 92S44 F257 V");
        familiare.put("sesso", "F");
        familiare.put("relazione_parentale", "FIGLIA");
        
        familiariList.add(familiare);
        
        // addobject to a VelocityContext
        context.put("familiariList", familiariList);
        
    }
    

}