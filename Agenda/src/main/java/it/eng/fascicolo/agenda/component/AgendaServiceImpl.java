package it.eng.fascicolo.agenda.component;

import it.eng.fascicolo.agenda.dao.AgendaDAO;
import it.eng.fascicolo.agenda.dao.FceUtenteDAO;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.jpa.model.FceAgendaUtente;
import it.eng.fascicolo.commons.jpa.model.FceAgendaUtenteId;
import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component(value="agendaService")
public class AgendaServiceImpl implements AgendaService {
	
	@Autowired
	private FceUtenteDAO utenteDAO;
	@Autowired
	private AgendaDAO agendaDAO;

	public AgendaServiceImpl() {
	}

	@Override
	@Transactional
	public void aggiungiPreferiti(String idAccount, String idAgenda) {
		FceUtente utente=utenteDAO.findByIdAccount(idAccount);
		FceAgendaUtenteId id=new FceAgendaUtenteId(new BigDecimal(idAgenda), utente.getIdutente());
		FceAgendaUtente au=new FceAgendaUtente(id, "S");
		agendaDAO.aggiornaAgendaUtente(au);		
	}

	@Override
	@Transactional
	public void rimuoviPreferiti(String idAccount, String idAgenda) {
		FceUtente utente=utenteDAO.findByIdAccount(idAccount);
		FceAgendaUtenteId id=new FceAgendaUtenteId(new BigDecimal(idAgenda), utente.getIdutente());
		FceAgendaUtente au=new FceAgendaUtente(id, "N");
		agendaDAO.aggiornaAgendaUtente(au);		
	}

	/*
	 * Torna una lista di idAgenda corrispondenti agli eventi preferiti
	 * @see it.eng.fascicolo.agenda.component.AgendaService#getPreferitiAgendaIdByUtente(java.math.BigDecimal)
	 */
	@Override
	public List<BigDecimal> getPreferitiAgendaIdByUtente(String idAccount) {
		FceUtente utente=utenteDAO.findByIdAccount(idAccount);
		ArrayList<BigDecimal> al=null;
		if (utente!=null) {
			List<FceAgendaUtente> lista=agendaDAO.getAgendaUtenteByUtente(utente.getIdutente());
			if (lista!= null && lista.size()>0) {
				al=new ArrayList<BigDecimal>();
				for (FceAgendaUtente au : lista) {
					if (au.getFlagPreferito().startsWith("S")) {
						al.add(au.getId().getIdAgenda());
					}
				}
			}
		}
		return al;
	}

	@Override
	public List<FceAgendaCategoria> getCategorieEventiPreferitiUtente(String idAccount) {
		FceUtente utente=utenteDAO.findByIdAccount(idAccount);
		return agendaDAO.getCategorieEventiPreferitiUtente(utente.getIdutente());
	}

}
