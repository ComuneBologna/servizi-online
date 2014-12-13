package it.eng.fascicolo.agenda.dao.jpa;

import it.eng.fascicolo.agenda.dao.AgendaDAO;
import it.eng.fascicolo.commons.jpa.model.FceAgenda;
import it.eng.fascicolo.commons.jpa.model.FceAgendaCategoria;
import it.eng.fascicolo.commons.jpa.model.FceTipoAccount;
import it.eng.fascicolo.commons.jpa.model.FceUtente;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@ActiveProfiles(profiles = { "mock" })
@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@Transactional(value="transactionManager", propagation=Propagation.REQUIRES_NEW, readOnly=false)
public class AgendaDAOImplTest {
	
	@Autowired
	AgendaDAO agendaDAO;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Before
    public void setUp() {		
		FceTipoAccount tipoAccount = new FceTipoAccount(new BigDecimal(1000000), "TestDescription", "STRONG" );
		entityManager.persist(tipoAccount);
		
		FceUtente utente = new FceUtente(tipoAccount);
		utente.setIdAccount("TestIdAccount");
		entityManager.persist(utente);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);
		Date today = calendar.getTime();
		BigDecimal id = new BigDecimal(1000000);
		
		for (int i = 0; i < 2; i++) {
			FceAgendaCategoria categoria = new FceAgendaCategoria(id, "categoria-" + id, "#FFFFFF", "N");
			entityManager.persist(categoria);
			
			FceAgenda event = new FceAgenda(id, utente, null, categoria, today, today, today, "TestTitolo" + i, "Test Description" + i, 'N');
			entityManager.persist(event);
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			today = calendar.getTime();
			id = id.add(new BigDecimal(1));
		}
		
		for (int i = 0; i < 2; i++) {
			FceAgendaCategoria categoria = new FceAgendaCategoria(id, "categoria-" + id, "#FFFFFF", "N");
			entityManager.persist(categoria);
			
			FceAgenda event = new FceAgenda(id, null, null, categoria, today, today, today, "TestTitolo" + i, "Test Description" + i, 'N');
			entityManager.persist(event);
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			today = calendar.getTime();
			id = id.add(new BigDecimal(1));
		}
		
		/*for (int i = 0; i < 2; i++) {
			FceAgendaCategoria categoria = new FceAgendaCategoria(id, "categoria-" + id, "#FFFFFF", "N");
			entityManager.persist(categoria);
			
			FceAgenda event = new FceAgenda(id, utente, null, null, today, today, today, "TestTitolo" + i, "Test Description" + i, 'N');
			entityManager.persist(event);
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			today = calendar.getTime();
			id = id.add(new BigDecimal(1));
		}
		
		for (int i = 0; i < 2; i++) {			
			FceAgenda event = new FceAgenda(id, null, null, null, today, today, today, "TestTitolo" + i, "Test Description" + i, 'N');
			entityManager.persist(event);
			
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			today = calendar.getTime();
			id = id.add(new BigDecimal(1));
		}*/
    }
 
    @After
    public void tearDown() {
    	
    }
	
	/*@Test
	public void testGetNextEventsWithoutUserWithoutCategory() { 
		List<FceAgenda> result = agendaDAO.getNextEvents(100, 0, null, null);		
		int count = 0;
		for (FceAgenda a : result) {
			System.out.println("TITLE: " + a.getTitolo());
			if (a.getTitolo().contains("TestTitolo"))
				count++;
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(count == 4);
	}*/
	
	@Test
	public void testGetNextEventsWithoutUserWithCategory() { 
		List<FceAgenda> result = agendaDAO.getNextEvents(100, 0, null, "categoria-1000002,categoria-1000003");
		int count = 0;
		for (FceAgenda a : result) {
			if (a.getTitolo().contains("TestTitolo"))
				count++;
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(count == 2);
	}
	
	/*@Test
	public void testGetNextEventsWithUserWithoutCategory() {
		List<FceAgenda> result = agendaDAO.getNextEvents(100, 0, "FacebookProfile.335652266591364", null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 6);
	}*/
	
	@Test
	public void testGetNextEventsWithUserWithCategory() {
		List<FceAgenda> result = agendaDAO.getNextEvents(100, 0, "TestIdAccount", "categoria-1000000,categoria-1000001");
		int count = 0;
		for (FceAgenda a : result) {
			if (a.getTitolo().contains("TestTitolo"))
				count++;
		}
		Assert.assertNotNull(result);		
		Assert.assertTrue(count == 2);
	}
	
	/*@Test
	public void testGetEventsWithoutUserWithoutCategory() throws Exception {
		Date startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("31/10/2014");
		Date endDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("19/11/2014");
		
		List<FceAgenda> result = agendaDAO.getEvents(startDate, endDate, null, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 4);
	}*/
	
	@Test
	public void testGetEventsWithoutUserWithCategory() throws Exception {
		Calendar calendar = Calendar.getInstance();
		
		Date startDate = calendar.getTime();
		
		calendar.add(Calendar.DAY_OF_MONTH, 4);
		Date endDate = calendar.getTime();
		
		List<FceAgenda> result = agendaDAO.getEvents(startDate, endDate, null, "categoria-1000002,categoria-1000003");
		int count = 0;
		for (FceAgenda a : result) {
			System.out.println("TITLE: " + a.getTitolo());
			if (a.getTitolo().contains("TestTitolo"))
				count++;
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(count == 2);
	}
	
	/*@Test 
	public void testGetEventsWithUserWithoutCategory() throws Exception {
		Date startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("31/10/2014");
		Date endDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("19/11/2014");
		
		List<FceAgenda> result = agendaDAO.getEvents(startDate, endDate, "FacebookProfile.335652266591364", null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() == 5);
	}*/
	
	@Test 
	public void testGetEventsWithUserWithCategory() throws Exception {
		Calendar calendar = Calendar.getInstance();
		
		Date startDate = calendar.getTime();
		
		calendar.add(Calendar.DAY_OF_MONTH, 4);
		Date endDate = calendar.getTime();
		
		List<FceAgenda> result = agendaDAO.getEvents(startDate, endDate, "TestIdAccount", "categoria-1000000,categoria-1000001");
		int count = 0;
		for (FceAgenda a : result) {
			System.out.println("TITLE: " + a.getTitolo());
			if (a.getTitolo().contains("TestTitolo"))
				count++;
		}
		Assert.assertNotNull(result);
		Assert.assertTrue(count == 2);
	}
	
	/*@Test 
	public void testGetEvent() throws Exception {
		FceAgenda result = agendaDAO.getEvent(new BigDecimal("1"));
		System.out.println("RESULT:" + result.getTitolo());
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getTitolo().equalsIgnoreCase("Event 1"));
	}*/
	
}
