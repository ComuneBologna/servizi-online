package it.eng.eli4u.service.command;

import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.portlet.ResourceRequest;
import javax.xml.bind.JAXBException;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class GestioneLocalizzazioniCommand implements IServiceCommand {
	@Autowired
	IDatabaseService databaseService;
	
	@Override
	public String execute(ResourceRequest req) throws IOException,
			JAXBException, DocumentException, Exception, FileNotFoundException{
		
		Gson gson = new Gson();
		String output = null;
		
		//1. Convert json object to Localizzazione object
		String json = req.getParameter("json");
		
		//1.1 Execute the operation specified
		String operation = req.getParameter("operation");
		
		//2. Save localizzazione in DB
		if(operation != null && operation.equalsIgnoreCase("add")){
			Localizzazione localizzazione = gson.fromJson(json, Localizzazione.class);
			databaseService.insertLocalizzazione(localizzazione);
			output = gson.toJson(localizzazione);
		}
		
		else if(operation != null && operation.equalsIgnoreCase("update")){
			Localizzazione localizzazione = gson.fromJson(json, Localizzazione.class);
			databaseService.insUpdLocalizzazione(localizzazione);
			output = gson.toJson(localizzazione);
		}
			
		else if(operation != null && operation.equalsIgnoreCase("delete")){
			int[] ids = gson.fromJson(json, int[].class); 
			for (int i = 0; i < ids.length; i++) {
				Localizzazione localizzazione = new Localizzazione();
				localizzazione.setIdLocalizz(ids[i]);
				databaseService.deleteLocalizzazione(localizzazione);
				output = gson.toJson(localizzazione);
			}
		}
		
		
		return output;
	}

}