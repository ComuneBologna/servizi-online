package it.eng.eli4u.service.velocity;

import it.eli4you.engines.masterindex.core.xml.output.CampoType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eli4you.engines.masterindex.core.xml.output.ElementoRowType;
import it.eli4you.engines.masterindex.core.xml.output.ElementoType;
import it.eli4you.engines.masterindex.core.xml.output.FaultType;
import it.eli4you.engines.masterindex.core.xml.output.RowType;
import it.eli4you.engines.masterindex.core.xml.output.TabellaType;
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


public class LaMiaImpresa implements HtmlRenderer {
	private final Logger logger = LoggerFactory.getLogger(LaMiaImpresa.class);
	
	public static final String VELOCITY_TEMPLATE = "velocity/la_mia_impresa.vm.html";

    public static void main(String[] args) throws Exception {
    	LaMiaImpresa obj = new LaMiaImpresa();
    	
    	String result = obj.render("", "");

        // show output, for debug purpose
        System.out.println(result);
        
        // save to a file
        String outputFileName = "/home/domenico/Downloads/iperbole_2014-10-17/test_out.html";
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
    	ArrayList impreseList = new ArrayList();
        
        // TODO - Change locale for internationalization
    	Locale locale = Locale.ITALY;

    	// read views
		Map impresa = null;
		int index = 1;
		char sedeIndex = 'a';
		ArrayList sediList = new ArrayList();
		
    	for (ViewType view : dettaglioFonte.getView()) {
    		impresa = new HashMap();
    		boolean isSede = false;
    		//Map sedePrincipale = new HashMap();
    		//Map unitaLocale = new HashMap();
    		
    		//ArrayList sediList = new ArrayList();
            ArrayList dichiarazioniList = new ArrayList();
            ArrayList utenzeGasList = new ArrayList();
            ArrayList utenzeElettricheList = new ArrayList();
            ArrayList locazioniList = new ArrayList();
            ArrayList procedimentiEdiliziList = new ArrayList();
            
            // NOTE: Only a single impresa to date
			impresa.put("index", 1);
			Map sede = new HashMap();
			sede.put("index", sedeIndex++);
			
    		for (ElementoType elemento : view.getElemento()) {
				//scan each element
				if (elemento.getCampo() != null) {
					// read single fields
					CampoType campo = elemento.getCampo();
					String valore = campo.getSetValori().getValore();
					if (campo.getId().equals("DENOMINAZIONE_DITTA")) {
						impresa.put("denominazione", valore);
						//impresa.put("index", index++);
					} else if (campo.getId().equals("CODICE_FISCALE")) {
						impresa.put("codice_fiscale", FormatUtil.formatCodiceFiscale(valore));
					} else if (campo.getId().equals("NUM_ISCR_RI")) {
						impresa.put("numero_iscrizione_ri", valore);
					} else if (campo.getId().equals("INDIRIZZO")) {
						sede.put("indirizzo", valore);
					} else if (campo.getId().equals("DATA_COSTITUZIONE")) {
						sede.put("data_costituzione", valore);
					} else if (campo.getId().equals("DESCRIZIONE_STATO_ATTIVITA")) {
						sede.put("stato_attivita", valore);
					} else if (campo.getId().equals("DESCRIZIONE_ATTIVITA")) {
						sede.put("descrizione_attivita", valore);
					} else if (campo.getId().equals("CATEGORIA_CATASTALE")) {
						sede.put("categoria", FormatUtil.getCategoriaCatastale(valore));
					} else if (campo.getId().equals("PERC_POSS")) {
						sede.put("pct_possesso", valore + "%");
					} else if (campo.getId().equals("SUPERFICIE")) {
						sede.put("superficie", valore + " mq.");
					} else if (campo.getId().equals("RENDITA_CATASTALE")) {
						sede.put("rendita_catastale", "&euro; " + valore);
					}
					  else if (campo.getId().equals("DENOMINAZIONE_UL")) {
						sede.put("denominazione", valore);
					} else if (campo.getId().equals("DESCRIZIONE_ATTIVITA_UL")) {
						sede.put("descrizione_attivita", valore);
					} else if (campo.getId().equals("DATA_APERTURA_UL")) {
						sede.put("data_costituzione", valore);
					} else if (campo.getId().equals("DATA_CESSAZIONE_UL")) {
						sede.put("data_cessazione", valore);
					} else if (campo.getId().equals("INDIRIZZO_UL")) {
						sede.put("indirizzo", valore);
					} else if (campo.getId().equals("TIPO_UL")) {
						if (valore.equals("SE")) {
							isSede = true;
						}
					}
					
				} else if (elemento.getTabella() != null) {
					TabellaType tabella = elemento.getTabella();
					String id = tabella.getId();

					if (id.equals("DOCUMENTI")) {
						// dichiarazioni
						for (RowType row : tabella.getRows()) {
							String descrizione = "";
							String dataPresentazione = "";
							for (ElementoRowType rowElem : row.getRowElement()) {
								String campo = rowElem.getTesto().getId();
								String valore = rowElem.getTesto().getValore();
								if (campo.equals("DESCRIZIONE_DOCUMENTO")) {
									descrizione = valore;
								}
								if (campo.equals("DATA_PRESENTAZIONE")) {
									dataPresentazione = valore;
								}
							}
							if (descrizione.length() > 0) {
								Map dichiarazione = new HashMap();
								dichiarazione.put("descrizione", descrizione + " - " + FormatUtil.formatDate(dataPresentazione, locale));
								dichiarazioniList.add(dichiarazione);
							}
						}
						sede.put("dichiarazioniList", dichiarazioniList);
						
					} else if (id.equals("INFO_GAS")) {
						// utenze gas
						for (RowType row : tabella.getRows()) {
							String anno = row.getRowElement().get(0).getTesto().getValore();
							String consumo = row.getRowElement().get(1).getTesto().getValore();
							
							Map utenzaGas = new HashMap();
							utenzaGas.put("descrizione", "&euro; " + consumo + " - anno " + anno);
							utenzeGasList.add(utenzaGas);
						}
						sede.put("utenzeGasList", utenzeGasList);
					
					} else if (id.equals("INFO_ELETTRICO")) {
						// utenze elettriche
						for (RowType row : tabella.getRows()) {
							String anno = row.getRowElement().get(0).getTesto().getValore();
							String importo = row.getRowElement().get(1).getTesto().getValore();
							String consumo = "";
							if (row.getRowElement().size() > 2) {
								consumo = row.getRowElement().get(2).getTesto().getValore();
							}
							
							Map utenzaElettrica = new HashMap();
							utenzaElettrica.put("descrizione", consumo + " - &euro; " + importo + " - anno " + anno);
							utenzeElettricheList.add(utenzaElettrica);
						}
						sede.put("utenzeElettricheList", utenzeElettricheList);
					
					} else if (id.equals("INFO_LOCAZIONI")) {
						// info locazioni
						for (RowType row : tabella.getRows()) {
							String ruolo_soggetto = "";
							String ufficio_contratto = "";
							String anno_contratto = "";
							String serie_contratto = "";
							String numero_contratto = "";
							
							for (ElementoRowType rowElem : row.getRowElement()) {
								String campo = rowElem.getTesto().getId();
								String valore = rowElem.getTesto().getValore(); 
								if (campo.equals("RUOLO_SOGGETTO")) {
									ruolo_soggetto = valore;
								} else if (campo.equals("UFFICIO_CONTRATTO")) {
									ufficio_contratto = valore;
								} else if (campo.equals("ANNO_CONTRATTO")) {
									anno_contratto = valore;
								} else if (campo.equals("SERIE_CONTRATTO")) {
									serie_contratto = valore;
								} else if (campo.equals("NUMERO_CONTRATTO")) {
									numero_contratto = valore;
								}
							}
							if (ruolo_soggetto.length() > 0) {
								String ruolo = ruolo_soggetto.equals("D") ? "Proprietario" : "Affittuario";
								String numContratto = ufficio_contratto + "/" + anno_contratto + "/" + serie_contratto + "/" + numero_contratto;
								Map locazione = new HashMap();
								locazione.put("descrizione", ruolo + ", contratto n. " + numContratto);
								locazioniList.add(locazione);
							}
						}
						sede.put("locazioniList", locazioniList);
						
					} else if (id.equals("INFO_LICENZE_COMMERCIALI")) {
						// info licenze commerciali
						// TODO - Not bound yet!
					
					} else if (id.equals("INFO_PROCEDIMENTI")) {
						// procedimenti edilizi
						for (RowType row : tabella.getRows()) {
							String descrizione = "";
							String dataRichiesta = "";
							for (ElementoRowType rowElem : row.getRowElement()) {
								String campo = rowElem.getTesto().getId();
								String valore = rowElem.getTesto().getValore(); 
								if (campo.equals("DESCRIZIONE_PROCEDIMENTO")) {
									descrizione = valore;
								}
								if (campo.equals("DATA_RICHIESTA")) {
									dataRichiesta = valore;
								}
							}
							if (descrizione.length() > 0) {
								Map procedimentoEdilizio = new HashMap();
								procedimentoEdilizio.put("descrizione", descrizione + " - " + FormatUtil.formatDate(dataRichiesta, locale));
								procedimentiEdiliziList.add(procedimentoEdilizio);
							}
						}
						sede.put("procedimentiEdiliziList", procedimentiEdiliziList);
						
					}
					
				}
			}
    		
    		if (isSede) {
    			sede.put("nome", "Sede");
    			//sede.put("index", "a");
    			sediList.add(0, sede);
    		} else {
    			sede.put("nome", "Unit&agrave; locale");
    			//unitaLocale.put("index", "b");
				sediList.add(sede);
			}
			
			//impresa.put("sediList", sediList);
			//impreseList.add(impresa);
    	}
    	if (impresa != null) {
    		impresa.put("sediList", sediList);
    		impreseList.add(impresa);
    	}
    	
        // add that list to a VelocityContext
        context.put("impreseList", impreseList);
		
	}

	

	private void buildTestData(VelocityContext context) {
        // prepare data
		ArrayList impreseList =  new ArrayList();
		ArrayList sediList =  new ArrayList();
        ArrayList dichiarazioniList = new ArrayList();
        ArrayList utenzeGasList = new ArrayList();
        ArrayList utenzeElettricheList = new ArrayList();
        ArrayList locazioniList = new ArrayList();
        ArrayList procedimentiEdiliziList = new ArrayList();
        
        /***  // TEST fault
        Map fault = new HashMap();
        fault.put("code", "012");
        fault.put("description", "Errore nell'invocazione del servizio SERVICE-NAME");
        
     // add that object to a VelocityContext
        context.put("fault", fault);
        ***/
        
        Map impresa = new HashMap();
        impresa.put("index", "1");
        
        impresa.put("denominazione", "Bar Max di: <small>Massimiliano Passarella</small>");
        impresa.put("data_iscrizione", "13/09/1996");
        impresa.put("data_rinnovo", "30 giugno 2015");
        
        
        // Add sede
        Map sede = new HashMap();
        
        sede.put("index", "a");
        sede.put("indirizzo", "Via Murri, 49");
        sede.put("descrizione_attivita", "Commercio con somministrazione di bevande");
        sede.put("telefono", "890765432");
        sede.put("stato_attivita", "attiva");
        sede.put("data_costituzione", "13/09/1996");
        sede.put("categoria", "A/2");
        sede.put("pct_possesso", "100%");
        sede.put("superficie", "20 mq.");
        sede.put("rendita_catastale", "&euro; 124");
        sede.put("data_inizio_residenza", "23/11/2004");
        
        
        // Add Dichiarazioni
        Map dichiarazione = new HashMap();
        dichiarazione.put("descrizione", "Denuncia stagionale Tarsu - 12 febbraio 2011");
        dichiarazioniList.add(dichiarazione);
        
        dichiarazione = new HashMap();
        dichiarazione.put("descrizione", "Dichiarazione IMU - 11 aprile 2012");
        dichiarazioniList.add(dichiarazione);
        
        dichiarazione = new HashMap();
        dichiarazione.put("descrizione", "Denuncia stagionale Tarsu - 10 febbraio 2013");
        dichiarazioniList.add(dichiarazione);

        sede.put("dichiarazioniList", dichiarazioniList);


        // Add Utenze Gas
        Map utenzaGas = new HashMap();
        utenzaGas.put("descrizione", "&euro; 856 - anno 2010");
        utenzeGasList.add(utenzaGas);
        
        utenzaGas = new HashMap();
        utenzaGas.put("descrizione", "&euro; 824 - anno 2011");
        utenzeGasList.add(utenzaGas);
        
        utenzaGas = new HashMap();
        utenzaGas.put("descrizione", "&euro; 915 - anno 2012");
        utenzeGasList.add(utenzaGas);
        
        utenzaGas = new HashMap();
        utenzaGas.put("descrizione", "&euro; 1.618 - anno 2013");
        utenzeGasList.add(utenzaGas);
        
        sede.put("utenzeGasList", utenzeGasList);
        
        
        // Add Utenze Elettriche
        Map utenzaElettrica = new HashMap();
        utenzaElettrica.put("descrizione", "345 - anno 2010");
        utenzeElettricheList.add(utenzaElettrica);
        
        utenzaElettrica = new HashMap();
        utenzaElettrica.put("descrizione", "367 - anno 2011");
        utenzeElettricheList.add(utenzaElettrica);
        
        utenzaElettrica = new HashMap();
        utenzaElettrica.put("descrizione", "382 - anno 2012");
        utenzeElettricheList.add(utenzaElettrica);
        
        utenzaElettrica = new HashMap();
        utenzaElettrica.put("descrizione", "472 - anno 2013");
        utenzeElettricheList.add(utenzaElettrica);
        
        sede.put("utenzeElettricheList", utenzeElettricheList);
        
        
        // Add Locazioni
        Map locazione = new HashMap();
        locazione.put("descrizione", "Proprietario, contratto n. 798/2006/3/916");
        locazioniList.add(locazione);
        
        locazione = new HashMap();
        locazione.put("descrizione", "Affittuario, contratto n. 795/2005/3/14");
        locazioniList.add(locazione);
        
        sede.put("locazioniList", locazioniList);
        
        
        // Add Procedimenti Edilizi
        Map procedimentoEdilizio = new HashMap();
        procedimentoEdilizio.put("descrizione", "Dichiarazione Inizio Attività - 08 gennaio 2011");
        procedimentiEdiliziList.add(procedimentoEdilizio);
        
        procedimentoEdilizio = new HashMap();
        procedimentoEdilizio.put("descrizione", "Dichiarazione Inizio Attività - 14 dicembre 2012");
        procedimentiEdiliziList.add(procedimentoEdilizio);
        
        procedimentoEdilizio = new HashMap();
        procedimentoEdilizio.put("descrizione", "Dichiarazione Inizio Attività - 21 giugno 2013");
        procedimentiEdiliziList.add(procedimentoEdilizio);
        
        sede.put("procedimentiEdiliziList", procedimentiEdiliziList);
        
        sediList.add(sede);
        
        impresa.put("sediList", sediList);
        
        impreseList.add(impresa);
        
        // add list to the VelocityContext
        context.put("impreseList", impreseList);
    	
    }


private Object cleanDescription(String string) {
	// TODO Auto-generated method stub
	return string;
}
	
    
}