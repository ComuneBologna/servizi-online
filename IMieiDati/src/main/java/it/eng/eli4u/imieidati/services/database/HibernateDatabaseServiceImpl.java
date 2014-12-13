package it.eng.eli4u.imieidati.services.database;

import it.eng.eli4u.imieidati.model.ParametroServizio;
import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;
import it.eng.eli4u.imieidati.services.database.hibernate.HibernateSessionFactory;
import it.eng.eli4u.service.command.DettaglioSezioneCommand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HibernateDatabaseServiceImpl implements IDatabaseService {
	
	private final Logger logger = LoggerFactory.getLogger(DettaglioSezioneCommand.class);

	/**
	 * Return the list of sections taken from db
	 */
	@Override
	public List<Sezione> listaSezioni() {

		Session session = null;
		Transaction tx = null;

		List<Sezione> ret = new ArrayList<Sezione>();

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session
					.createCriteria(it.eng.eli4u.imieidati.services.database.entities.Sezione.class);

			criteria.addOrder(Order.asc("id"));
			List<it.eng.eli4u.imieidati.services.database.entities.Sezione> listaDb = criteria
					.list();

			tx.commit();

			// traduzione listaDb in lista di oggetti di tipo sezione
			for (it.eng.eli4u.imieidati.services.database.entities.Sezione sezione : listaDb) {

				Sezione sezioneModel = convertIntoModelSection(sezione);

				ret.add(sezioneModel);
			}

			return ret;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	/**
	 * Return the list of localizations taken from db
	 */
	@Override
	public List<Localizzazione> listaLocalizzazioni() {

		Session session = null;
		Transaction tx = null;

		List<Localizzazione> ret = new ArrayList<Localizzazione>();

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session
					.createCriteria(it.eng.eli4u.imieidati.services.database.entities.Localizzazione.class);

			criteria.addOrder(Order.asc("idLocalizz"));
			List<it.eng.eli4u.imieidati.services.database.entities.Localizzazione> listaDb = criteria
					.list();

			tx.commit();

			ret = listaDb;

			return ret;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	
	@Override
	public List<Localizzazione> listaLocalizzazioni(String locale) throws IOException {
		Session session = null;
		//Transaction tx = null;

		List<Localizzazione> ret = new ArrayList<Localizzazione>();

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			//tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(it.eng.eli4u.imieidati.services.database.entities.Localizzazione.class);
			criteria.add(Restrictions.eq("locale", locale));
			
			criteria.addOrder(Order.asc("idLocalizz"));
			List<it.eng.eli4u.imieidati.services.database.entities.Localizzazione> listaDb = criteria.list();

			//tx.commit();

			ret = listaDb;

			return ret;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			/*
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			*/
			session.close();
		}
	}
	
	
	/**
	 * Return the section with ID id
	 * 
	 * @param id
	 *            the id of the section
	 * @return sezione
	 */
	@Override
	public Sezione findSezioneById(String id) {

		Session session = null;
		Transaction tx = null;
		Sezione sezioneModel = null;

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session
					.createCriteria(it.eng.eli4u.imieidati.services.database.entities.Sezione.class);
			criteria.add(Restrictions.eq("id", Double.parseDouble(id)));

			List<it.eng.eli4u.imieidati.services.database.entities.Sezione> listaDb = criteria
					.list();

			tx.commit();

			// if exists, convert and return it!
			if (listaDb.size() > 0) {
				it.eng.eli4u.imieidati.services.database.entities.Sezione sezione = listaDb
						.get(0);

				// traduzione listaDb in lista di oggetti di tipo sezione
				sezioneModel = convertIntoModelSection(sezione);
			}

			return sezioneModel;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	/**
	 * Insert or update a section
	 * 
	 * @param sezione
	 *            the section to insert or modify
	 */
	@Override
	public void insUpdSezione(Sezione sezione) {

		Session session = null;
		Transaction tx = null;

		// Check if exists
		Sezione sezioneInDb = findSezioneById(sezione.getId());

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// If exists Update it!
			if (sezioneInDb != null) {
				it.eng.eli4u.imieidati.services.database.entities.Sezione existingSection = (it.eng.eli4u.imieidati.services.database.entities.Sezione) session
						.load(it.eng.eli4u.imieidati.services.database.entities.Sezione.class,
								Double.parseDouble(sezione.getId()));

				existingSection = updateSection(sezione, existingSection);

				Double idDouble = (Double) session.save(existingSection);
				Integer id = idDouble.intValue();
				System.out.println("MODIFICATA SEZIONE CON ID = "
						+ id.toString());
			}

			// Insert new if not exists.
			else {
				it.eng.eli4u.imieidati.services.database.entities.Sezione sezioneDb = new it.eng.eli4u.imieidati.services.database.entities.Sezione();
				sezioneDb = updateSection(sezione, sezioneDb);

				Double idDouble = (Double) session.save(sezioneDb);
				Integer id = idDouble.intValue();
				System.out.println("NUOVO ID = " + id.toString());
			}

			tx.commit();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}

	}

	/**
	 * Delete a section
	 * 
	 * @param sezione
	 *            the section to delete
	 */
	@Override
	public void deleteSezione(Sezione sezione) {

		Session session = null;
		Transaction tx = null;

		// Check if section exists!
		Sezione sezioneInDb = findSezioneById(sezione.getId());

		// if founded, delete it!
		if (sezioneInDb != null) {

			try {

				session = HibernateSessionFactory.getSessionFactory()
						.openSession();
				tx = session.beginTransaction();

				it.eng.eli4u.imieidati.services.database.entities.Sezione existingSection = (it.eng.eli4u.imieidati.services.database.entities.Sezione) session
						.load(it.eng.eli4u.imieidati.services.database.entities.Sezione.class,
								Double.parseDouble(sezione.getId()));

				// For delete convert section param into section db format
				existingSection = updateSection(sezione, existingSection);

				session.delete(existingSection);
				System.out.println("ELIMINATA SEZIONE CON ID = "
						+ sezione.getId());

				tx.commit();

			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				throw new RuntimeException(ex.getMessage(), ex);
			} finally {
				if (tx != null && tx.isActive()) {
					try {
						tx.rollback();
					} catch (Exception ex) {
					}
				}
				session.close();
			}
		}

	}

	/**
	 * Utils Method for converting Sezione Database into Sezione Model
	 */
	private Sezione convertIntoModelSection(
			it.eng.eli4u.imieidati.services.database.entities.Sezione sezione) {
		// Convert flag type
		short flag = sezione.getFlgAbilitato();
		boolean flagAbilitato = (flag != 0);

		// Convert id type
		String idString = Integer.toString((int) sezione.getId());

		ArrayList<ParametroServizio> list = new ArrayList<ParametroServizio>();

		for (it.eng.eli4u.imieidati.services.database.entities.ParametroServizio paramServizio : sezione
				.getParametroServizios()) {

			ParametroServizio paramModel = new ParametroServizio();
			paramModel.setCodice(paramServizio.getCodice());
			paramModel.setValore(paramServizio.getValore());

			list.add(paramModel);
		}

		Sezione sezioneModel = new Sezione(idString, sezione.getCodice(),
				sezione.getTitolo(), sezione.getDescrizione(),
				sezione.getImage(), sezione.getUriXsl(),
				sezione.getUriXslMobile(), sezione.getDatiXsl(), sezione.getDatiXslMobile(), sezione.getHtmlStatico(),
				flagAbilitato, sezione.getIdServizio(),
				sezione.getUrlServizio(), sezione.getNomeServizio(),
				sezione.getUsernameServizio(), sezione.getPasswordeServizio(),
				list, sezione.getPortletAbilitate(), sezione.getRenderingClass());

		return sezioneModel;
	}

	/**
	 * Utils Method for updating Sezione Database from Sezione Model info
	 */
	private it.eng.eli4u.imieidati.services.database.entities.Sezione updateSection(
			Sezione sezione,
			it.eng.eli4u.imieidati.services.database.entities.Sezione existingSection) {
		existingSection.setCodice(sezione.getCodice());
		existingSection.setId(Double.parseDouble(sezione.getId()));
		existingSection.setDescrizione(sezione.getDescrizione());
		existingSection.setFlgAbilitato((short) (sezione.isFlgAbilitato() ? 1
				: 0));
		existingSection.setHtmlStatico(sezione.getHtmlStatico());
		existingSection.setIdServizio(sezione.getIdServizio());
		existingSection.setImage(sezione.getImage());
		existingSection.setNomeServizio(sezione.getNomeServizio());
		existingSection.setPasswordeServizio(sezione.getPasswordServizio());
		existingSection.setTitolo(sezione.getTitolo());
		existingSection.setUriXsl(sezione.getUriXsl());
		existingSection.setUriXslMobile(sezione.getUriXslMobile());
		existingSection.setUrlServizio(sezione.getUrlServizio());
		existingSection.setUsernameServizio(sezione.getUsernameServizio());
		existingSection.setPortletAbilitate(sezione.getPortletAbilitate());
		existingSection.setRenderingClass(sezione.getRenderingClass());

		if (sezione.getParametriInputServizio() != null) {

			Set<it.eng.eli4u.imieidati.services.database.entities.ParametroServizio> paramsSet = new HashSet<it.eng.eli4u.imieidati.services.database.entities.ParametroServizio>();

			double idParam = 0;
			for (ParametroServizio paramServizio : sezione
					.getParametriInputServizio()) {

				it.eng.eli4u.imieidati.services.database.entities.ParametroServizio param = new it.eng.eli4u.imieidati.services.database.entities.ParametroServizio();
				param.setCodice(paramServizio.getCodice());
				param.setValore(paramServizio.getValore());
				param.setSezione(existingSection);
				param.setIdParam(idParam);

				paramsSet.add(param);

				idParam++;
			}

			existingSection.setParametroServizios(paramsSet);
		}

		return existingSection;
	}

	/**
	 * Get message localized, if there is no file in language locale, use the
	 * english one, if the key does not exist in the localized file, use the
	 * default value in database.properties.
	 * 
	 * @param locale
	 *            local code
	 * @param msg
	 *            message key for localization
	 * @throws IOException
	 */
	@Override
	public String mesaggio(String locale, String msg) {

		String messaggio = null;

		// Split locale because it can be it_IT for example but filename has
		// only first part
		String[] partsLocale = locale.split("_");

		messaggio = findLocalizationByLocalAndKey(partsLocale[0], msg);

		// if the key is not in db, return the english one
		if (messaggio == null) {
			messaggio = findLocalizationByLocalAndKey("en", msg);
		}

		// If no en, or local, return the key!
		if (messaggio == null) {
			messaggio = msg;
		}

		return messaggio;
	}

	/**
	 * Util Method for find Localizzazione
	 * 
	 * @param id
	 *            the id of the section
	 * @return sezione
	 */
	public String findLocalizationByLocalAndKey(String locale, String key) {

		Session session = null;
		Transaction tx = null;
		String local = null;

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(Localizzazione.class);
			criteria.add(Restrictions.eq("locale", locale));
			criteria.add(Restrictions.eq("chiave", key));

			List<Localizzazione> listaDb = criteria.list();

			tx.commit();

			// if exists, convert and return it!
			if (listaDb.size() > 0) {
				Localizzazione localization = listaDb.get(0);
				local = localization.getValore();
			}

			return local;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	/**
	 * Main Method for test
	 * 
	 */

	public static void main(String[] args) throws IOException {

		HibernateDatabaseServiceImpl propDbImp = new HibernateDatabaseServiceImpl();

		// propDbImp.findSezioneById("1");
		propDbImp.listaLocalizzazioni();

	}

	/**
	 * Util Method for insert or update Localizzazione
	 * 
	 * @param localizzazione
	 *            the localization object
	 * @return sezione
	 */
	@Override
	public void insUpdLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		Session session = null;
		Transaction tx = null;

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();

				// Check if exists
				Localizzazione localizzazioneInDb = findLocalizzazioneById(String
						.valueOf(localizzazione.getIdLocalizz()));

				// If exists Update it!
				if (localizzazioneInDb != null) {

					session.update(localizzazione);
					System.out.println("MODIFICATA LOCALIZZAZIONE ");
				}
				
				//else insert it
				else{
					
					//Get number of elements so the index of new element is count!
					int count = ((Long)session.createQuery("select count(*) from Localizzazione").uniqueResult()).intValue();
					
					//Set id of new element to count value
					Localizzazione localizzazioneDB = new Localizzazione();
					localizzazioneDB.setChiave(localizzazione.getChiave());
					localizzazioneDB.setLocale(localizzazione.getLocale());
					localizzazioneDB.setValore(localizzazione.getValore());
					localizzazioneDB.setIdLocalizz(count);
					
					Double idDouble = (Double) session.save(localizzazioneDB);
					Integer id = idDouble.intValue();
					System.out.println("NUOVO ID = " + id.toString());
				}

			tx.commit();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	/**
	 * Util Method for find Localizzazione
	 * 
	 * @param id
	 *            the id of the localization object
	 * @return sezione
	 */
	@Override
	public Localizzazione findLocalizzazioneById(String id) {

		Session session = null;
		Transaction tx = null;
		Localizzazione localizzazione = null;

		try {

			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(Localizzazione.class);
			criteria.add(Restrictions.eq("id", Double.parseDouble(id)));

			List<Localizzazione> listaDb = criteria.list();

			tx.commit();

			// if exists, convert and return it!
			if (listaDb.size() > 0) {
				localizzazione = listaDb.get(0);
			}

			return localizzazione;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}

	}

	/**
	 * Insert new Localizzazione
	 * 
	 * @param localizzazione
	 *            the localization object
	 * @return sezione
	 */
	@Override
	public void insertLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		Session session = null;
		Transaction tx = null;

		try {
			session = HibernateSessionFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();

			//Get number of elements so the index of new element is count!			
			List<Localizzazione> localizzazioniList = listaLocalizzazioni();
			Localizzazione last = localizzazioniList.get(localizzazioniList.size() - 1);
			
			//Set id of new element to count value
			Localizzazione localizzazioneDB = new Localizzazione();
			localizzazioneDB.setChiave(localizzazione.getChiave());
			localizzazioneDB.setLocale(localizzazione.getLocale());
			localizzazioneDB.setValore(localizzazione.getValore());
			localizzazioneDB.setIdLocalizz(last.getIdLocalizz() + 1);
			
			Double idDouble = (Double) session.save(localizzazioneDB);
			Integer id = idDouble.intValue();
			System.out.println("NUOVO ID = " + id.toString());	
			
			tx.commit();
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (tx != null && tx.isActive()) {
				try {
					tx.rollback();
				} catch (Exception ex) {
				}
			}
			session.close();
		}
	}

	/**
	 * Delete Localizzazione
	 * 
	 * @param localization
	 *            the localization object
	 * @return sezione
	 */
	@Override
	public void deleteLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		Session session = null;
		Transaction tx = null;

		// Check if localizzazione exists!
		Localizzazione localizzazioneInDb = findLocalizzazioneById(String.valueOf(localizzazione.getIdLocalizz()));

		// if founded, delete it!
		if (localizzazioneInDb != null) {

			try {

				session = HibernateSessionFactory.getSessionFactory()
						.openSession();
				tx = session.beginTransaction();
				
				session.delete(localizzazioneInDb);
				System.out.println("ELIMINATA SEZIONE CON ID = "
						+ localizzazione.getIdLocalizz());

				tx.commit();

			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				throw new RuntimeException(ex.getMessage(), ex);
			} finally {
				if (tx != null && tx.isActive()) {
					try {
						tx.rollback();
					} catch (Exception ex) {
					}
				}
				session.close();
			}
		}

			
	}

}
