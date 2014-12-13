package it.eng.fascicolo.agenda.dao;

import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.io.Serializable;

public interface FceUtenteDAO extends AbstractDAO<Serializable> {
	public FceUtente findByIdAccount(String idAccount);
}
