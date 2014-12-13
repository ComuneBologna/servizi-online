package it.eng.fascicolo.intornoame.dao;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameCategoria;

import java.math.BigDecimal;
import java.util.List;

public interface CategoriaDAO {

	public FceIntornoameCategoria getCategoriaKml(BigDecimal idCategoria);
	public List<FceIntornoameCategoria> getElencoCategorieKmlAttive();
	public List<FceIntornoameCategoria> getElencoCategorieKmlAttiveSorted();
	 
}
