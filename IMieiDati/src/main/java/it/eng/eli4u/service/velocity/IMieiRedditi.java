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
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IMieiRedditi implements HtmlRenderer {
	private final Logger logger = LoggerFactory.getLogger(IMieiRedditi.class);
	
	public static final String VELOCITY_TEMPLATE = "velocity/i_miei_redditi.vm.html";
	
	public static void main(String[] args) throws Exception {
		IMieiRedditi obj = new IMieiRedditi();
		
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
			if (response != null && response instanceof DettaglioType) {
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
			// parse only "Dati Contabili" views
			if (view.getTitolo().startsWith("Dati contabili")) {
				int len = view.getTitolo().length();
				String anno = view.getTitolo().substring(len - 5, len - 1);
				Map reddito = null;
				
				for (ElementoType elemento : view.getElemento()) {
					TabellaType tabella = elemento.getTabella();
					// fill Reddito info
					reddito = new HashMap();
					reddito.put("index", index++);
					reddito.put("anno", anno);

					// NOTE: those info are added later
	 				//reddito.put("rigaRedditiList", rigaRedditiList); 
					//list.add(reddito);
						
					// fill Riga Reddito info
					ArrayList rigaRedditiList = new ArrayList();
					
					for (RowType row : tabella.getRows()) {
						Map riga = new HashMap();
						String description = row.getRowElement().get(1).getTesto().getValore();
						riga.put("descrizione", cleanDescription(description));
						String importo = row.getRowElement().get(2).getTesto().getValore().trim();
						importo = importo.startsWith("€") ? "&euro;" + importo.substring(1) : importo;
						riga.put("importo", importo);
						rigaRedditiList.add( riga );
					}
					
					// At last, add info to Reddito
					reddito.put("rigaRedditiList", rigaRedditiList);
					list.add( reddito );
					reddito = null;
				}
			}
			
		}

		// add that list to a VelocityContext
		context.put("redditiList", list);
		
	}
	

	private void buildTestData(VelocityContext context) {
		// prepare data
		ArrayList list = new ArrayList();
		ArrayList rigaRedditiList = new ArrayList();
		
		/***  // TEST fault
		Map fault = new HashMap();
		fault.put("code", "012");
		fault.put("description", "Errore nell'invocazione del servizio SERVICE-NAME");
		
	 // add that object to a VelocityContext
		context.put("fault", fault);
		***/

		Map riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Totale imponibile fabbricati ......"));
		riga.put("importo", "€ 1.456,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Totale redditi lavoro dipendente e assimilati ......"));
		riga.put("importo", "€ 18.220,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Reddito d'impresa (o perdita) di spettanza dell'imprenditore ......"));
		riga.put("importo", "€ 33.572,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(""));
		riga.put("importo", "€ 33.001,00");
		rigaRedditiList.add( riga );


		Map reddito = new HashMap();
		reddito.put("index", "1");
		reddito.put("anno", "2006");
		reddito.put("rigaRedditiList", rigaRedditiList);
		list.add( reddito );
 
		
		rigaRedditiList = new ArrayList();
		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Totale imponibile fabbricati ......"));
		riga.put("importo", "€ 1.654,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Totale redditi lavoro dipendente e assimilati ......"));
		riga.put("importo", "€ 19.940,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Reddito d'impresa (o perdita) di spettanza dell'imprenditore ......"));
		riga.put("importo", "€ 14.706,00");
		rigaRedditiList.add( riga );

		riga = new HashMap();
		riga.put("descrizione", cleanDescription(" - Addizionale comunale all'Irpef dovuta importo ......"));
		riga.put("importo", "€ 253,00");
		rigaRedditiList.add( riga );


		reddito = new HashMap();
		reddito.put("index", "2");
		reddito.put("anno", "2008");
		reddito.put("rigaRedditiList", rigaRedditiList);
		list.add( reddito );

		// add that list to a VelocityContext
		context.put("redditiList", list);
		
	}
	
	private static String cleanDescription(String description) {
		String ret = "";
		String prefix = " - ";
		String suffix = " ......";
		
		if (description != null) {
			if (description.startsWith(prefix)) {
				description = description.substring(prefix.length());
			}
			if (description.endsWith(suffix)) {
				description = description.substring(0, description.length() - suffix.length());
			}
			ret = description;
		}
		return ret;
	}
}