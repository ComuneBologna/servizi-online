package it.eng.fascicolo.agenda.dao.jpa;

import it.eng.fascicolo.agenda.dao.FceUtenteDAO;
import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.io.Serializable;

import javax.persistence.Query;

public class FceUtenteDAOImpl extends AbstractJpaDAO<Serializable> implements FceUtenteDAO {

	public FceUtente findByIdAccount(String idAccount) {
		logger.debug("findByIdAccount: IN");
		FceUtente result = null;
		Query query = entityManager.createQuery("from FceUtente u where u.idAccount=?1");
		try {
			query.setParameter(1, idAccount);
			result = (FceUtente)query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("findByIdAccount: OUT");
		return result;
	}

}
