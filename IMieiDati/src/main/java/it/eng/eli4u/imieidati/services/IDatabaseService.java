package it.eng.eli4u.imieidati.services;

import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IDatabaseService {
		
	//LOCALIZZAZIONI
	public List<Localizzazione> listaLocalizzazioni() throws IOException;
	
	public List<Localizzazione> listaLocalizzazioni(String locale) throws IOException;

	public Localizzazione findLocalizzazioneById(String id) throws IOException;
	
	public void insUpdLocalizzazione(Localizzazione localizzazione) throws FileNotFoundException, IOException;
	
	public void insertLocalizzazione(Localizzazione localizzazione) throws FileNotFoundException, IOException;
	
	public void deleteLocalizzazione(Localizzazione localizzazione) throws FileNotFoundException, IOException;

	//SEZIONI
	public List<Sezione> listaSezioni();

	public Sezione findSezioneById(String id);
	
	public void insUpdSezione(Sezione sezione) throws FileNotFoundException, IOException;

	public void deleteSezione(Sezione sezione) throws FileNotFoundException, IOException;
	
	public String mesaggio(String locale, String msg) throws IOException;
	
}
