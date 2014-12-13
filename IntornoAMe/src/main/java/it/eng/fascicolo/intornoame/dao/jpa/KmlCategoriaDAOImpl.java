package it.eng.fascicolo.intornoame.dao.jpa;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameKmlCat;
import it.eng.fascicolo.intornoame.dao.KmlCategoriaDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

public class KmlCategoriaDAOImpl implements KmlCategoriaDAO {
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<FceIntornoameKmlCat> getElencoKmlAttiviSortedByCategoria() {
		TypedQuery<FceIntornoameKmlCat> query=entityManager.createQuery("from FceIntornoameKmlCat k where k.fceIntornoameCategoria.flagAttivo = 'S' AND k.fceIntornoameKml.flagAttivo='S' order by k.fceIntornoameCategoria.descrizione", FceIntornoameKmlCat.class);
//		Query query = entityManager.createQuery("from FceIntornoameKmlCat k where k.fceIntornoameCategoria.flagAttivo = 'S' AND k.fceIntornoameKml.flagAttivo='S' order by k.fceIntornoameCategoria.descrizione");
		List<FceIntornoameKmlCat> resultList = query.getResultList();
		return resultList;
	}
	
	
}
