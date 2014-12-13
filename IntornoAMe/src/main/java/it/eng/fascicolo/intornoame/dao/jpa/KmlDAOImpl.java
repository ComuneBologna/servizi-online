package it.eng.fascicolo.intornoame.dao.jpa;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameKml;
import it.eng.fascicolo.intornoame.dao.KmlDAO;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class KmlDAOImpl implements KmlDAO {
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public FceIntornoameKml getKmlFile(BigDecimal idKml) {
		TypedQuery<FceIntornoameKml> query=entityManager.createQuery("from FceIntornoameKml k where k.idkml = ?1",FceIntornoameKml.class);
//		Query query = entityManager.createQuery("from FceIntornoameKml k where k.idkml = ?1");
		query.setParameter(1, idKml);
		return query.getSingleResult();
	}

	@Override
	public List<FceIntornoameKml> getElencoKmlAttivi() {
		TypedQuery<FceIntornoameKml> query = entityManager.createQuery("from FceIntornoameKml k where k.flagAttivo = 'S'", FceIntornoameKml.class);
//		Query query = entityManager.createQuery("from FceIntornoameKml k where k.flagAttivo = 'S'");
		List<FceIntornoameKml> resultList = query.getResultList();
		return resultList;
	}
}
