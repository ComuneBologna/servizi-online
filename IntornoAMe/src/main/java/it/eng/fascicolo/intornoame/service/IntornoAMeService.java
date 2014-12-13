package it.eng.fascicolo.intornoame.service;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameCategoria;
import it.eng.fascicolo.commons.jpa.model.FceIntornoameKml;
import it.eng.fascicolo.intornoame.beans.CategoriaKmlBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public interface IntornoAMeService {

	ArrayList<CategoriaKmlBean> getCategorieKmlAttiveBeans();
	List<String> getElencoIdKmlAttivi();
	List<FceIntornoameCategoria> getElencoCategorieKmlAttiveSorted();
	FceIntornoameKml getKmlById(BigDecimal idKml);

}
