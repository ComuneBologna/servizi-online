package it.eng.fascicolo.agenda.dao;

import java.io.Serializable;

public interface AbstractDAO<T extends Serializable> {
	
	public T findOne(long id);

}




