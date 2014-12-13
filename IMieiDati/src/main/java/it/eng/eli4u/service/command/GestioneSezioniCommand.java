package it.eng.eli4u.service.command;

import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.portlet.ResourceRequest;
import javax.xml.bind.JAXBException;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class GestioneSezioniCommand implements IServiceCommand {
	@Autowired
	IDatabaseService databaseService;
	
	@Override
	public String execute(ResourceRequest req) throws IOException,
			JAXBException, DocumentException, Exception, FileNotFoundException{
		
		Gson gson = new Gson();
		
		String operation = req.getParameter("operation");
		
		//1. Convert json object to Section object
		String json = req.getParameter("json");
		
		String output = null;
		
		if(operation != null && operation.equalsIgnoreCase("delete"))
		{
			int[] ids = gson.fromJson(json, int[].class); 
			for (int i = 0; i < ids.length; i++) {
				Sezione sezione = new Sezione();
				sezione.setId(String.valueOf(ids[i]));
				
				databaseService.deleteSezione(sezione);
				
				output = gson.toJson(sezione);
				
				return output;
			}
		}
		
		else if(operation != null && operation.equalsIgnoreCase("add/modify"))
		{
			it.eng.eli4u.imieidati.model.Sezione sezione = gson.fromJson(json, Sezione.class);
			
			//2. Save section in DB
			databaseService.insUpdSezione(sezione);
			
			output = gson.toJson(sezione);
		}
		
		return output;
	}

}
