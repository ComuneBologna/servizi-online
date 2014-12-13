package it.eng.eli4u.service.command;

import it.eng.eli4u.application.context.SpringApplicationContext;
import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.utils.EmptyHTMLChecker;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;


public class ListaSezioniValideCommand implements IServiceCommand {
	Logger logger = LoggerFactory.getLogger(ListaSezioniValideCommand.class);

	@Autowired
	IDatabaseService databaseService;
	@Autowired
	IIMieiDatiPortalService portalService;
	
	ArrayList<String> failedConnections;

	
	public ListaSezioniValideCommand() {
		failedConnections = new ArrayList<String>();
	}
	
	
	@Override
	public String execute(ResourceRequest req) throws IOException {

		Gson gson = new Gson();

		try {
			logger.info("DEBUG - ListaSezioniValide - IN");
			//System.out.println("#{*}# - DEBUG - ListaSezioniValide - IN");
			
			List<Sezione> listaSezioniValide = new ArrayList<Sezione>();
			
			// lista delle sezioni
			List<Sezione> listaSezione = databaseService.listaSezioni();
			if (listaSezione == null) {
				listaSezione = new ArrayList<Sezione>();
			}
			// Localize Title and Description of Sections
			String locale = portalService.linguaPortale(req);
			
			// scan each section
			for (int i = 0; i < listaSezione.size(); i++) {
				Sezione sezione = listaSezione.get(i);
				sezione.setTitolo(databaseService.mesaggio(locale, "sezione."
						+ i + ".titolo"));
				sezione.setDescrizione(databaseService.mesaggio(locale,
						"sezione." + i + ".descrizione"));
				
				// avoid failing connection
				if (!failedConnections.contains(getFullHostName(sezione.getUrlServizio()))) {
					if (isSectionValid(sezione, i, req)) {
						// avoid sending unuseful but sensitive information
						sezione.setNomeServizio(null);
						sezione.setUrlServizio(null);
						sezione.setUsernameServizio(null);
						sezione.setPasswordServizio(null);
						sezione.setParametriInputServizio(null);
						sezione.setRenderingClass(null);
						
						// only add valid sections
						listaSezioniValide.add(sezione);
					}
				}
			}
			
			String output = "";
			// show warning message for no valid section
			if (listaSezioniValide.size() == 0) {
				output = IMieiDatiConstants.getNoSectionMessage();
			} else { 
				output = gson.toJson(listaSezioniValide);
			}
			
			//logger.info("DEBUG - ListaSezioniValide - output: " + output);
			//System.out.println("#{*}# - output: " + output);

			logger.info("DEBUG - ListaSezioniValide - OUT");

			return output;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//ErrorMessage errorMessage = new ErrorMessage(IMieiDatiConstants.CODICE_ERR_GENERICO, "" + e.getMessage());
			//return gson.toJson(errorMessage);
			String ret = IMieiDatiConstants.getDefaultErrorMessage();
			ret = gson.toJson(ret);
			
			//logger.info("DEBUG - ListaSezioniValide - ret: " + ret);
			//System.out.println("#{*}# - ret: " + ret);
			
			logger.info("DEBUG - ListaSezioniValide - OUT");

			return ret;
		}
	}

	private boolean isSectionValid(Sezione sezione, int index, ResourceRequest req) throws Exception {
		req.setAttribute("section_id", "" + index);
		
		// load DettaglioSezioneCommand through Spring (because of autowired properties)
		String clazz = "it.eng.eli4u.service.command.DettaglioSezioneCommand";
		Class<?> forName =  Class.forName(clazz);
		
		String html = "";
		try {
			// scan all sections
			IServiceCommand dettaglioSezione = (IServiceCommand)SpringApplicationContext.getContext().getBean(forName); 
			html = dettaglioSezione.execute(req);
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage(), ex);
			html = "";
			updateFailedConnections(ex, sezione);
		}
		
		req.removeAttribute("section_id");
		
		boolean empty = EmptyHTMLChecker.isEmptyData(html); 
		
		return !empty;
	}

	/*
	 * Method to trace connections which fails for network problems
	 *   avoiding multiple waiting time
	 */
	private void updateFailedConnections(Exception ex, Sezione sezione) {
		String msg = ex.getMessage().toUpperCase();
		
		if (msg.indexOf("HTTP") >= 0 ||
			msg.indexOf("NETWORK") >= 0 ||
			msg.indexOf("TIMED OUT") >= 0 ||
			msg.indexOf("TIMEOUT") >= 0) {
			
			String serviceUrl = sezione == null ? "" : sezione.getUrlServizio();
			String fullHostName = getFullHostName(serviceUrl);
			if (!"".equals(fullHostName)) {
				failedConnections.add(fullHostName);
			}
		}
	}
	
	private static String getFullHostName(String strUrl) {
		String fullHostName = "";
		try {
			URL url = new URL(strUrl);
			fullHostName = url.getHost() + ":" + url.getPort();
		} catch (Exception e) {
			fullHostName = "";
		}
		return fullHostName;
	}
	
	
}
