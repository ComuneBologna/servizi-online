package it.eng.fascicolo.agenda.component;

import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;

import java.math.BigDecimal;
import java.util.List;

public interface AgendaService {
	public void aggiungiPreferiti(String idAccount, String idAgenda);
	public void rimuoviPreferiti(String idAccount, String idAgenda);
	public List<BigDecimal> getPreferitiAgendaIdByUtente(String idAccount); 
	public List<FceAgendaCategoria> getCategorieEventiPreferitiUtente(String idAccount); 
}
