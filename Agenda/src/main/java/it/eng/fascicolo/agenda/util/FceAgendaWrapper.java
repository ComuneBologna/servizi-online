package it.eng.fascicolo.agenda.util;

import it.eng.fascicolo.commons.jpa.model.FceAgenda;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.jpa.model.FceAgendaSorgenti;
import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.math.BigDecimal;
import java.util.Date;

public class FceAgendaWrapper {

	private final FceAgenda agenda;
	private boolean preferito;
	
	public FceAgendaWrapper(FceAgenda agenda) {
		this.agenda=agenda;
	}

	public BigDecimal getIdagenda() {
		return agenda.getIdagenda();
	}

	public FceUtente getFceUtente() {
		return agenda.getFceUtente();
	}

	public FceAgendaSorgenti getFceAgendaSorgenti() {
		return agenda.getFceAgendaSorgenti();
	}

	public FceAgendaCategoria getFceAgendaCategoria() {
		return agenda.getFceAgendaCategoria();
	}

	public Date getDatacreazione() {
		return agenda.getDatacreazione();
	}

	public Date getDatainizio() {
		return agenda.getDatainizio();
	}

	public Date getDatafine() {
		return agenda.getDatafine();
	}

	public String getTitolo() {
		return agenda.getTitolo();
	}

	public String getDescrizione() {
		return agenda.getDescrizione();
	}

	public Character getTuttoilgiorno() {
		return agenda.getTuttoilgiorno();
	}

	public boolean isPreferito() {
		return preferito;
	}

	public void setPreferito(boolean preferito) {
		this.preferito = preferito;
	}
	
}
