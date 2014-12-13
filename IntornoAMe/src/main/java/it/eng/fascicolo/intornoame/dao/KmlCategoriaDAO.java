package it.eng.fascicolo.intornoame.dao;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameKmlCat;

import java.util.List;

public interface KmlCategoriaDAO {

	public List<FceIntornoameKmlCat> getElencoKmlAttiviSortedByCategoria();
	
}
