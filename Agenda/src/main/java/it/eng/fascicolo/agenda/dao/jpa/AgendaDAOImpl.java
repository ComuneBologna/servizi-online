package it.eng.fascicolo.agenda.dao.jpa;

import it.eng.fascicolo.agenda.dao.AgendaDAO;
import it.eng.fascicolo.commons.jpa.model.FceAgenda;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.jpa.model.FceAgendaUtente;
import it.eng.fascicolo.commons.jpa.model.FceAgendaUtenteId;
import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Data Access Object for Agenda data type
 * @author egross
 *
 */
public class AgendaDAOImpl implements AgendaDAO {
	@PersistenceContext
	EntityManager entityManager;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	/***
	 * Method to get a number of events determined by an amount, offset, user and categories
	 */
	@Override
	public List<FceAgenda> getNextEvents(int num, int offset, String idUtente, String categories) {
		logger.info("Enter into getNextEvents in AgendaDAOImpl");
		logger.info("num = " + num + ", idUtente = " + idUtente);
		
		Date today = new Date();
		
		//Get Category objects for filtering
		List<FceAgenda> categoryList = getCategories(categories);
		
		//Get public events	
		Query query;
		if (categoryList != null) {
			query = entityManager.createQuery("from FceAgenda a where a.fceUtente is null and a.datainizio >= ?1 and a.fceAgendaCategoria in (?2)");
			query.setParameter(1, today);
			query.setParameter(2, categoryList);
		} else {
			query = entityManager.createQuery("from FceAgenda a where a.fceUtente is null and a.datainizio >= ?1");
			query.setParameter(1, today);
		}
		
		List<FceAgenda> publicEventsList = query.getResultList();
		logger.info("publicEventsList.size(): " + publicEventsList.size());
		
		//Get private events
		List<FceAgenda> privateEventsList = null;
		if (idUtente != null && idUtente != "") {
			Query queryUtente = entityManager.createQuery("from FceUtente u where u.idAccount = ?1");
			queryUtente.setParameter(1, idUtente);
			List<FceUtente> utenteList = queryUtente.getResultList();
			logger.info("utenteList.size(): " + utenteList.size());
			
			for (FceUtente u : utenteList) {
				Query query2;
				if (categoryList != null) {
					query2 = entityManager.createQuery("from FceAgenda a where a.fceUtente = ?1 and a.datainizio >= ?2 and a.fceAgendaCategoria in (?3)");
					query2.setParameter(1, u);
					query2.setParameter(2, today);
					query2.setParameter(3, categoryList);
				} else {
					query2 = entityManager.createQuery("from FceAgenda a where a.fceUtente = ?1 and a.datainizio >= ?2");
					query2.setParameter(1, u);
					query2.setParameter(2, today);
				}				
				privateEventsList = query2.getResultList();
				logger.info("privateEventsList.size(): " + privateEventsList.size());	
			}				
		}
		
		//Join public and private events
		if (privateEventsList != null)
			publicEventsList.addAll(privateEventsList);
		
		logger.info("publicEventsList.size() after merge: " + publicEventsList.size());
		
		//Sort list
		sortList(publicEventsList);
		
		//Return only a subset of the results based on the offset and size of list arguments
		if (offset != 0) {
			if (offset >= publicEventsList.size())
				publicEventsList = null;
			else {
				if ((offset + num) > publicEventsList.size())
					publicEventsList = publicEventsList.subList(offset, publicEventsList.size());
				else
					publicEventsList = publicEventsList.subList(offset, offset + num);
				
				logger.info("publicEventsList.size() after subset: " + publicEventsList.size());
			}				
		} else {
			if (num > publicEventsList.size())
				publicEventsList = publicEventsList.subList(0, publicEventsList.size());
			else
				publicEventsList = publicEventsList.subList(0, num);
			
			logger.info("publicEventsList.size() after subset: " + publicEventsList.size());
		}
				
		logger.info("Exit out of getNextEvents in AgendaDAOImpl");		
		return publicEventsList;
	}

	/***
	 * Method to get events between two dates (inclusive) and based on a user and set of categories
	 */
	@Override
	public List<FceAgenda> getEvents(Date start, Date end, String idUtente, String categories) {
		logger.info("Enter into getEvents in AgendaDAOImpl");
		logger.info("Start date = " + start + ", End Date = " + end + ", idUtente = " + idUtente);
		
		//Get Category objects for filtering
		List<FceAgenda> categoryList = getCategories(categories);
		
		//Get public events
		Query query;
		if (categoryList != null) {
			if (end == null){			
				query = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.fceUtente is null and a.fceAgendaCategoria in (?2)");
				query.setParameter(1, start);
				query.setParameter(2, categoryList);
			} else {
				query = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.datainizio <= ?2 and a.fceUtente is null and a.fceAgendaCategoria in (?3)");
				query.setParameter(1, start);
				query.setParameter(2, end);
				query.setParameter(3, categoryList);
			}
		} else {
			if (end == null){			
				query = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.fceUtente is null");
				query.setParameter(1, start);
			} else {
				query = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.datainizio <= ?2 and a.fceUtente is null");
				query.setParameter(1, start);
				query.setParameter(2, end);
			}
		}	
		
		List<FceAgenda> publicEventsList = query.getResultList();
		logger.info("publicEventsList.size(): " + publicEventsList.size());
		
		//Get private events
		List<FceAgenda> privateEventsList = null;
		if (idUtente != null && idUtente != "") {
			Query queryUtente = entityManager.createQuery("from FceUtente u where u.idAccount = ?1");
			queryUtente.setParameter(1, idUtente);
			List<FceUtente> utenteList = queryUtente.getResultList();

			for (FceUtente u : utenteList) {
				Query query2;
				if (categoryList != null) {
					if (end == null){
						query2 = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.fceUtente = ?2 and a.fceAgendaCategoria in (?3)");
						query2.setParameter(1, start);
						query2.setParameter(2, u);
						query2.setParameter(3, categoryList);
					} else {
						query2 = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.datainizio <= ?2 and a.fceUtente = ?3 and a.fceAgendaCategoria in (?4)");
						query2.setParameter(1, start);
						query2.setParameter(2, end);
						query2.setParameter(3, u);
						query2.setParameter(4, categoryList);
					}
				} else {
					if (end == null){
						query2 = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.fceUtente = ?2");
						query2.setParameter(1, start);
						query2.setParameter(2, u);
					} else {
						query2 = entityManager.createQuery("from FceAgenda a where a.datainizio >= ?1 and a.datainizio <= ?2 and a.fceUtente = ?3");
						query2.setParameter(1, start);
						query2.setParameter(2, end);
						query2.setParameter(3, u);
					}
				}								
				privateEventsList = query2.getResultList();
				logger.info("privateEventsList.size(): " + privateEventsList.size());
			}			
		}
		
		//Join public and private events
		if (privateEventsList != null)
			publicEventsList.addAll(privateEventsList);		
		
		logger.info("publicEventsList.size() after merge: " + publicEventsList.size());
		
		//Sort list
		sortList(publicEventsList);
		
		logger.info("Exit out of getEvents in AgendaDAOImpl");
		return publicEventsList;
	}

	/**
	 * Method to get a single event based on an agenda id
	 */
	@Override
	public FceAgenda getEvent(BigDecimal idagenda) {
		logger.info("Enter into getEvent in AgendaDAOImpl");
		logger.info("agendaId = " + idagenda);
		
		//Get public events
		Query query = entityManager.createQuery("from FceAgenda a where a.idagenda = ?1");
		query.setParameter(1, idagenda);
		FceAgenda result = (FceAgenda) query.getSingleResult();
		
		logger.info("Exit out of getEvent in AgendaDAOImpl");
		return result;
	}

	/***
	 * Method to get all categories from the database
	 */
	@Override
	public List<FceAgendaCategoria> getCategoria() {
		logger.info("Enter into getCategoria in AgendaDAOImpl");
		
		//Get public events
		Query query = entityManager.createQuery("from FceAgendaCategoria c ORDER BY c.flagPreferito, c.nome");
		List<FceAgendaCategoria> result = query.getResultList();
		
		logger.info("Exit out of getCategoria in AgendaDAOImpl");
		return result;
	}
	
	/***
	 * Method to sort the data in a list in order of date asc 
	 * @param resultList1
	 */
	private void sortList(List<FceAgenda> resultList1) {
		logger.info("Enter into sortList in AgendaDAOImpl");
		
		Collections.sort(resultList1, new Comparator<FceAgenda>() {

			@Override
			public int compare(FceAgenda a1, FceAgenda a2) {
				if (a1.getDatainizio().before(a2.getDatainizio()))
					return -1;
				else if (a1.getDatainizio().after(a2.getDatainizio()))
					return 1;
				else
					return 0;
			}
		});
		
		logger.info("Exit out of sortList in AgendaDAOImpl");
	}
	
	/***
	 * Method to get categories from the database based on a list of categories
	 * @param categories
	 * @return
	 */
	public List<FceAgenda> getCategories(String categories) {
		List<String> strCategoryList = null;
		List<BigDecimal> decCategoryList = null;
		List<FceAgenda> categoryList = null;
		Query queryCategoria;
		if (categories != null && !categories.equals("")) {
			categories = categories.replaceAll("categoria-", "");
			strCategoryList = new ArrayList<String>(Arrays.asList(categories.split(",")));
			decCategoryList = new ArrayList<BigDecimal>();
			for (String s : strCategoryList) {
				decCategoryList.add(new BigDecimal(s));
			}			
			queryCategoria = entityManager.createQuery("from FceAgendaCategoria fac where fac.idagendacategoria in (?1)");
			queryCategoria.setParameter(1, decCategoryList);
			categoryList = queryCategoria.getResultList();
			logger.info("categoryList.size(): " + categoryList.size());
		}
		
		return categoryList;
	}
	
	@Override
	public List<FceAgendaCategoria> getCategorieByList(String listaCategorie) {
		List<String> strCategoryList = null;
		List<BigDecimal> decCategoryList = null;
		List<FceAgendaCategoria> categoryList = null;
		Query queryCategoria;
		if (listaCategorie != null && !listaCategorie.equals("")) {
			listaCategorie = listaCategorie.replaceAll("categoria-", "");
			strCategoryList = new ArrayList<String>(Arrays.asList(listaCategorie.split(",")));
			decCategoryList = new ArrayList<BigDecimal>();
			for (String s : strCategoryList) {
				decCategoryList.add(new BigDecimal(s));
			}			
			queryCategoria = entityManager.createQuery("from FceAgendaCategoria fac where fac.idagendacategoria in (?1)");
			queryCategoria.setParameter(1, decCategoryList);
			categoryList = queryCategoria.getResultList();
			logger.info("categoryList.size(): " + categoryList.size());
		}
		return categoryList;
	}

	@Override
	public void aggiornaAgendaUtente(FceAgendaUtente au) {
		logger.info("Enter into insertAgendaUtente in AgendaDAOImpl");
		entityManager.merge(au);
	}

	@Override
	public List<FceAgendaUtente> getAgendaUtenteByUtente(BigDecimal idUtente) {
		List<FceAgendaUtente> lista=null;
		Query queryPreferiti = entityManager.createQuery("from FceAgendaUtente au where au.id.idUtente = ?1");
		queryPreferiti.setParameter(1, idUtente);
		lista=queryPreferiti.getResultList();
		return lista;
	}

	@Override
	public FceAgendaCategoria getCategoriaPreferita() {
		Query queryCategoria = entityManager.createQuery("from FceAgendaCategoria fac where fac.flagPreferito = ?1");
		queryCategoria.setParameter(1, "S");
		FceAgendaCategoria cat =(FceAgendaCategoria) queryCategoria.getSingleResult();
		return cat;
	}

	/*
	 * Torna tutte le categorie, eccetto la preferita degli eventi selezionati dall'utente
	 * come preferiti.
	 * 
	 * @see it.eng.fascicolo.agenda.dao.AgendaDAO#getCategorieEventiPreferitiUtente(java.math.BigDecimal)
	 */
	@Override
	public List<FceAgendaCategoria> getCategorieEventiPreferitiUtente(
			BigDecimal idUtente) {
		StringBuffer sql=new StringBuffer();
/* VECCHIA QUERY
		sql.append("from FceAgendaCategoria f ");
		sql.append("where f.flagPreferito <> 'S' AND ");
		sql.append("   exists (select a FROM FceAgenda a, FceAgendaUtente n ");
		sql.append("           WHERE a.idagenda=n.id.idAgenda AND n.flagPreferito = ?1 ");
		sql.append("           AND n.id.idUtente = ?2) ");
*/
		sql.append(" SELECT c ");
		sql.append(" FROM FceAgendaCategoria c, FceAgenda a, FceAgendaUtente n ");
		sql.append(" WHERE a.idagenda=n.id.idAgenda ");
		sql.append(" AND a.fceAgendaCategoria.idagendacategoria = c.idagendacategoria ");
		sql.append(" AND n.id.idUtente = ?2 ");
		sql.append(" AND n.flagPreferito = ?1 "); 
		
		logger.debug("JPAQL: "+sql.toString());
		Query queryCategoria = entityManager.createQuery(sql.toString());
		queryCategoria.setParameter(1, "S");
		queryCategoria.setParameter(2, idUtente);
		List<FceAgendaCategoria> l = queryCategoria.getResultList();
		return l;
	}
	
}
