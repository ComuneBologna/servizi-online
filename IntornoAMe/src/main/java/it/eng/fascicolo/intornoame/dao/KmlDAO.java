package it.eng.fascicolo.intornoame.dao;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameKml;

import java.math.BigDecimal;
import java.util.List;

public interface KmlDAO {

	public FceIntornoameKml getKmlFile(BigDecimal idKml);
	public List<FceIntornoameKml> getElencoKmlAttivi();
	
}
