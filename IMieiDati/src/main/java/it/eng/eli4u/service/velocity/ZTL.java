package it.eng.eli4u.service.velocity;

import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eli4you.engines.masterindex.core.xml.output.ElementoType;
import it.eli4you.engines.masterindex.core.xml.output.FaultType;
import it.eli4you.engines.masterindex.core.xml.output.RowType;
import it.eli4you.engines.masterindex.core.xml.output.TabellaType;
import it.eli4you.engines.masterindex.core.xml.output.ViewType;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZTL implements HtmlRenderer {
	private final Logger logger = LoggerFactory.getLogger(ZTL.class);
	
	
	public static final String VELOCITY_TEMPLATE = "velocity/ztl.vm.html";
	
	
    public static void main(String[] args) throws Exception {
    	ZTL obj = new ZTL();
    	
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
        	if (response != null || !(response instanceof DettaglioType)) {
	        	DettaglioType dettaglioFonte = (DettaglioType)response;
	        	
	        	// check for 
	        	buildData(context, dettaglioFonte);
        	}
        }
        
		logger.info("Using VELOCITY_TEMPLATE: " + VELOCITY_TEMPLATE);

        //  get the Template and apply it
        Template t = ve.getTemplate(VELOCITY_TEMPLATE, "UTF-8");
        StringWriter writer = new StringWriter();
        t.merge( context, writer );

        return writer.toString();
    }
    
    
    private void buildData(VelocityContext context, DettaglioType dettaglioFonte) {
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
        ArrayList list = new ArrayList();
        int index = 1;
    	// read views
    	for (ViewType view : dettaglioFonte.getView()) {
    		// parse only "contrassegni" view
    		if (view.getTitolo().equals("contrassegni")) {
    			Map utente = null;
    			for (ElementoType elemento : view.getElemento()) {
    				TabellaType tabella = elemento.getTabella();
    				if (tabella.getTitolo().equals("Utente")) {
    					List<String> columns = tabella.getHeader().getColumnName();
    					// TODO fill Utente info
    			        utente = new HashMap();
    			        utente.put("index", index++);
    			        utente.put("numero", columns.get(1));
    			        utente.put("nome", columns.get(0));
    			        utente.put("periodo_validita", columns.get(2));
    			        utente.put("data_rinnovo", columns.get(3));
    			        // NOTE: those info are added later
     			        //utente.put("veicoliList", veicoliList); 
    			        //list.add(utente);
    					
    				} else if (tabella.getTitolo().equals("Veicoli")) {
    					// TODO fill Veicoli info
    		            ArrayList veicoliList = new ArrayList();
    		            
    		            for (RowType row : tabella.getRows()) {
    		                Map veicolo = new HashMap();
    		                veicolo.put("tipo", row.getRowElement().get(1).getTesto().getValore());
    		                veicolo.put("targa", row.getRowElement().get(0).getTesto().getValore());
    		                veicolo.put("periodo_validita", row.getRowElement().get(2).getTesto().getValore());
    		                veicoliList.add(veicolo);
    		            }
    		            
    		            // At last, add info to Utente
     			        utente.put("veicoliList", veicoliList); 
    			        list.add(utente);
    			        utente = null;
    				}
    			}
    		}
    		
    	}

        // add that list to a VelocityContext
        context.put("contrassegniList", list);
		
	}


	private void buildTestData(VelocityContext context) {
        // prepare data
        ArrayList list = new ArrayList();
        ArrayList veicoliList = new ArrayList();
        
        /***  // TEST fault
        Map fault = new HashMap();
        fault.put("code", "012");
        fault.put("description", "Errore nell'invocazione del servizio SERVICE-NAME");
        
     // add that object to a VelocityContext
        context.put("fault", fault);
        ***/

        Map map2 = new HashMap();
        map2.put("tipo", "Autoveicolo");
        map2.put("targa", "AC 441 ET");
        map2.put("periodo_validita", "dal 1 marzo 2011 al 30 settembre 2014");
        veicoliList.add( map2 );

        map2 = new HashMap();
        map2.put("tipo", "Autoveicolo");
        map2.put("targa", "DF 334 RT");
        map2.put("periodo_validita", "dal 1 agosto 2013 al 31 luglio 2015");
        veicoliList.add( map2 );

        Map map = new HashMap();
        map.put("index", "1");
        map.put("numero", "H/228");
        map.put("nome", "Massimiliano Passarella");
        map.put("periodo_validita", "dal 1 marzo 2012 al 31 luglio 2015");
        map.put("data_rinnovo", "30 giugno 2015");
        map.put("veicoliList", veicoliList);
        list.add( map );
 
        
        
        veicoliList = new ArrayList();
        map2 = new HashMap();
        map2.put("tipo", "Motoveicolo");
        map2.put("targa", "DD 719 WZ");
        map2.put("periodo_validita", "dal 1 giugno 2012 al 31 agosto 2015");
        veicoliList.add( map2 );
        
        map = new HashMap();
        map.put("index", "2");
        map.put("numero", "H/229");
        map.put("nome", "Maura Pelloni");
        map.put("periodo_validita", "dal 1 giugno 2013 al 31 gennaio 2015");
//        map.put("data_rinnovo", "31 dicembre 2014");
        map.put("veicoliList", veicoliList);
        list.add( map );

        // add that list to a VelocityContext
        context.put("contrassegniList", list);
    	
    }
    
}