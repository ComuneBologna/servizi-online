package it.eng.fascicolo.intornoame.dao.jpa;

import it.eng.fascicolo.commons.jpa.model.FceIntornoameCategoria;
import it.eng.fascicolo.intornoame.dao.CategoriaDAO;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class CategoriaDAOImpl implements CategoriaDAO {
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public FceIntornoameCategoria getCategoriaKml(BigDecimal idCategoria) {
		Query query = entityManager.createQuery("from FceIntornoameCategoria k where k.idCategoria = ?1");
		query.setParameter(1, idCategoria);
		return (FceIntornoameCategoria) query.getSingleResult();
	}

	@Override
	public List<FceIntornoameCategoria> getElencoCategorieKmlAttive() {
		TypedQuery<FceIntornoameCategoria> query=entityManager.createQuery("from FceIntornoameCategoria k where k.flagAttivo='S'",FceIntornoameCategoria.class); 
//		Query query = entityManager.createQuery("from FceIntornoameCategoria k where k.flagAttivo='S'");
		List<FceIntornoameCategoria> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public List<FceIntornoameCategoria> getElencoCategorieKmlAttiveSorted() {
		TypedQuery<FceIntornoameCategoria> query= entityManager.createQuery("from FceIntornoameCategoria k WHERE k.flagAttivo='S' order by k.descrizione",FceIntornoameCategoria.class);
//		Query query = entityManager.createQuery("from FceIntornoameCategoria k WHERE k.flagAttivo='S' order by k.descrizione");
		List<FceIntornoameCategoria> resultList = query.getResultList();
		return resultList;
	}

}
