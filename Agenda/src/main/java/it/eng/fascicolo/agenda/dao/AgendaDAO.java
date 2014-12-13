package it.eng.fascicolo.agenda.dao;

import it.eng.fascicolo.commons.jpa.model.FceAgenda;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.jpa.model.FceAgendaUtente;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface AgendaDAO {
	public List<FceAgenda> getNextEvents(int num, int offset, String idUtente, String categories);
	public List<FceAgenda> getEvents(Date start, Date end, String idUtente, String categories);
	public FceAgenda getEvent(BigDecimal idagenda);
	public List<FceAgendaCategoria> getCategoria();
	public void aggiornaAgendaUtente(FceAgendaUtente au);
	public List<FceAgendaUtente> getAgendaUtenteByUtente(BigDecimal idUtente);
	public FceAgendaCategoria getCategoriaPreferita();
	public List<FceAgendaCategoria> getCategorieByList(String listaCategorie);
	public List<FceAgendaCategoria> getCategorieEventiPreferitiUtente(BigDecimal idUtente); 
}
