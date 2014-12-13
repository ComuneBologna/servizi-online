package it.eng.eli4u.imieidati.services.database;

import it.eng.eli4u.imieidati.model.ParametroServizio;
import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyDatabaseServiceImpl implements IDatabaseService {

	// Global Variable Properties
	Properties prop = new Properties();

	/**
	 * Constructor Initialize input stream with properties file
	 */
	public PropertyDatabaseServiceImpl() throws IOException {
		String filename = "database.properties";

		// Read From Properties File
		prop = new Properties();
		InputStream input = null;

		input = getResource(filename);

		if (input == null) {
			input = getResource("/" + filename);
		}

		if (input == null) {
			System.out.println("Sorry, unable to find " + "database.properties");
		}
		// load a properties file
		prop.load(input);

		if (input != null) {
			input.close();
		}
	}
	
	/**
	 * Return Property File
	 * @throws IOException 
	 */
	public Properties getPropertyFile(String filename) throws IOException{
		
		// Read From Properties File
		Properties localProp = new Properties();
		InputStream input = null;

		input = getResource(filename);

		if (input == null) {
			input = getResource("/" + filename);
		}

		if (input == null) {
			System.out.println("Sorry, unable to find " + filename);
		}
		// load a properties file
		localProp.load(input);

		if (input != null) {
			input.close();
		}
		
		return localProp;
	}

	/**
	 * Return the list of sections taken from properties file
	 */
	@Override
	public List<Sezione> listaSezioni() {

		// Create List to return
		List<Sezione> listSezione = new ArrayList<Sezione>();

		// get number of sections from properties file
		int numSezioni = Integer.parseInt(prop.getProperty("numSezioni"));

		// Create list for populating accordion headers
		for (int i = 0; i < numSezioni; i++) {

			String key = "sezione." + i + ".";

			Sezione sez = new Sezione();
			sez.setCodice(prop.getProperty(key + "codice"));
			sez.setId(prop.getProperty(key + "id"));
			sez.setTitolo(prop.getProperty(key + "titolo"));
			sez.setImage(prop.getProperty(key + "image"));
			sez.setIconImage(prop.getProperty(key + "iconImage"));
			sez.setDescrizione(prop.getProperty(key + "descrizione"));
			sez.setFlgAbilitato(Boolean.valueOf(prop.getProperty(key
					+ "flgAbilitato")));
			sez.setNomeServizio(prop.getProperty(key + "nomeServizio"));
			sez.setUrlServizio(prop.getProperty(key + "urlServizio"));
			sez.setUriXsl(prop.getProperty(key + "uriXsl"));
			sez.setPortletAbilitate(prop.getProperty(key + "portletAbilitate"));
			sez.setRenderingClass(prop.getProperty(key + "renderingClass"));
			sez.setUsernameServizio(prop.getProperty(key + "usernameServizio"));
			sez.setPasswordServizio(prop.getProperty(key + "passwordServizio"));

			sez.setParametriInputServizio(listaParametriSezioneById(sez.getId()));

			listSezione.add(sez);

		}
		return listSezione;
	}

	/**
	 * Return the list of parameters taken from section
	 */
	public List<ParametroServizio> listaParametriSezioneById(String id) {

		// Add list of params
		List<ParametroServizio> listaParametri = new ArrayList<ParametroServizio>();

		String paramKey = "sezione." + id + ".inputparam";

		for (String propKey : prop.stringPropertyNames()) {

			if (propKey.startsWith(paramKey)) {
				// String[] numberSplit = propKey.split("\\.");
				// String codice = numberSplit[(numberSplit.length - 1)]; //
				// last array element
				String codice = propKey.substring(paramKey.length() + 1); // last
																			// element,
																			// even
																			// it
																			// is
																			// composite

				// NOTE: Parameter ordering is not implemented yet
				int codeStart = codice.indexOf('.');
				if (codeStart >= 0) {
					codice = codice.substring(codeStart + 1);
				}

				ParametroServizio paramServizio = new ParametroServizio();
				paramServizio.setCodice(codice);
				paramServizio.setValore(prop.getProperty(propKey));
				listaParametri.add(paramServizio);
			}
		}

		return listaParametri;
	}

	/**
	 * Return the section with ID id
	 * 
	 * @param id
	 *            the id of the section
	 */
	@Override
	public Sezione findSezioneById(String id) {

		// Create basic key for access to section info
		String key = "sezione." + id + ".";

		if ((prop.getProperty(key + "id")) != null) {
			Sezione sez = new Sezione();
			sez.setCodice(prop.getProperty(key + "codice"));
			sez.setId(prop.getProperty(key + "id"));
			sez.setTitolo(prop.getProperty(key + "titolo"));
			sez.setImage(prop.getProperty(key + "image"));
			sez.setDescrizione(prop.getProperty(key + "descrizione"));
			sez.setFlgAbilitato(Boolean.valueOf(prop.getProperty(key + "flgAbilitato")));
			sez.setNomeServizio(prop.getProperty(key + "nomeServizio"));
			sez.setUrlServizio(prop.getProperty(key + "urlServizio"));
			sez.setUriXsl(prop.getProperty(key + "uriXsl"));
			sez.setPortletAbilitate(prop.getProperty(key + "portletAbilitate"));
			sez.setRenderingClass(prop.getProperty(key + "renderingClass"));
			sez.setUsernameServizio(prop.getProperty(key + "usernameServizio"));
			sez.setPasswordServizio(prop.getProperty(key + "passwordServizio"));

			// Add list of params
			sez.setParametriInputServizio(listaParametriSezioneById(sez.getId()));

			return sez;

		} else {

			return null;
		}
	}

	/**
	 * Insert or update a section
	 * 
	 * @param sezione
	 *            the section to insert or modify
	 */
	@Override
	public void insUpdSezione(Sezione sezione) throws FileNotFoundException,
			IOException {

		// Find if section exists
		Sezione sezioneInProperties = findSezioneById(sezione.getId());

		OutputStream output = null;

		String id;

		// If not exists, insert new section, otherwise update it
		if (sezioneInProperties == null) {

			output = new FileOutputStream("database.properties");

			// get number of sections from properties file
			int numSezioni = Integer.parseInt(prop.getProperty("numSezioni"));

			// update numSezioni
			prop.setProperty("numSezioni", Integer.toString(numSezioni + 1));

			id = sezione.getId();

		}

		else {

			output = new FileOutputStream("database.properties");

			// get id of existing section from properties file
			id = sezioneInProperties.getId();

		}

		// Create basic key for create section info
		String key = "sezione." + id + ".";

		// set the properties value
		prop.setProperty(key + "codice", sezione.getCodice());
		prop.setProperty(key + "id", sezione.getId());
		prop.setProperty(key + "titolo", sezione.getTitolo());
		prop.setProperty(key + "image", sezione.getImage());
		prop.setProperty(key + "descrizione", sezione.getDescrizione());
		prop.setProperty(key + "flgAbilitato",
				Boolean.toString(sezione.isFlgAbilitato()));
		prop.setProperty(key + "nomeServizio", sezione.getNomeServizio());
		prop.setProperty(key + "urlServizio", sezione.getUrlServizio());
		prop.setProperty(key + "uriXsl", sezione.getUriXsl());
		prop.setProperty(key + "portletAbilitate", sezione.getPortletAbilitate());
		prop.setProperty(key + "renderingClass", sezione.getRenderingClass());
		prop.setProperty(key + "usernameServizio", sezione.getUsernameServizio());
		prop.setProperty(key + "passwordServizio", sezione.getPasswordServizio());

		
		// Add list of params
		for (int i = 0; i < sezione.getParametriInputServizio().size(); i++) {

			String paramKey = "sezione." + id + ".inputparam" + i + ".";

			ParametroServizio paramServizio = sezione
					.getParametriInputServizio().get(i);
			prop.setProperty(paramKey + paramServizio.getCodice(),
					paramServizio.getValore());

		}

		// save properties to project root folder
		prop.store(output, null);

		if (output != null) {
			output.close();
		}
	}

	/**
	 * Delete the section
	 * 
	 * @param sezione
	 *            the section to delete
	 */
	@Override
	public void deleteSezione(Sezione sezione) throws FileNotFoundException,
			IOException {

		// Find if section exists
		Sezione sezioneInProperties = findSezioneById(sezione.getId());

		OutputStream output = null;

		if (sezioneInProperties != null) {

			output = new FileOutputStream("database.properties");

			int id = Integer.parseInt(sezioneInProperties.getId());

			// Create basic key for create section info
			String key = "sezione." + id + ".";

			prop.remove(key + "codice");
			prop.remove(key + "id");
			prop.remove(key + "titolo");
			prop.remove(key + "image");
			prop.remove(key + "descrizione");
			prop.remove(key + "flgAbilitato");
			prop.remove(key + "nomeServizio");
			prop.remove(key + "urlServizio");
			prop.remove(key + "uriXsl");
			prop.remove(key + "portletAbilitate");
			prop.remove(key + "renderingClass");
			prop.remove(key + "usernameServizio");
			prop.remove(key + "passwordServizio");

			// Remove list of parameters
			for (int i = 0; i < sezione.getParametriInputServizio().size(); i++) {

				String paramKey = "sezione." + id + ".inputparam" + i + ".";

				ParametroServizio paramServizio = sezione
						.getParametriInputServizio().get(i);
				prop.setProperty(paramKey + paramServizio.getCodice(),
						paramServizio.getValore());

			}
			// update numSezioni
			int numSezioni = Integer.parseInt(prop.getProperty("numSezioni"));
			prop.setProperty("numSezioni", Integer.toString(numSezioni - 1));

			prop.store(output, null);
		}

		if (output != null) {
			output.close();
		}
	}

	/**
	 * Create inputstream from resource
	 * 
	 * @param resKey
	 *            Name of the resource
	 */
	private InputStream getResource(String resKey) {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(resKey);

		return is;
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
	public String mesaggio(String locale, String msg) throws IOException {

		// Split locale because it can be it_IT for example but filename has
		// only first part
		String[] partsLocale = locale.split("_");

		String fileLocal = partsLocale[0] + ".properties";

		// Read From Properties File
		Properties localProp = new Properties();

		InputStream input = getResourceAsStream(fileLocal);

		// if null there is no file in that language, so load the english one
		if (input == null) {
			input = getResourceAsStream("en.properties");
		}

		localProp.load(input);

		String msgLocalized = localProp.getProperty(msg);

		// close inputstream for localized file
		if (input != null)
			input.close();

		// if the key is not in the file, return the default vaue from
		// database.properties file
		if (msgLocalized == null) {
			input = getResourceAsStream("database.properties");
			localProp.load(input);
			msgLocalized = localProp.getProperty(msg);

			// close inputstream for database.properties file
			if (input != null)
				input.close();
		}

		return msgLocalized;
	}

	/**
	 * Get inputstream from localized file if exists
	 * 
	 * @param filename
	 *            name of file to search
	 */
	private InputStream getResourceAsStream(String filename) {

		InputStream input = null;

		input = getResource(filename);

		if (input == null)
			input = getResource("/" + filename);

		if (input == null) {

			System.out.println("Sorry, unable to find " + filename
					+ ".properties");
		}

		return input;
	}

	/**
	 * Return the list of localizzazioni taken from properties file
	 */
	@Override
	public List<Localizzazione> listaLocalizzazioni() throws IOException {

		// Create List to return
		List<Localizzazione> listLocalizzazione = new ArrayList<Localizzazione>();

		listLocalizzazione = getAllLocForPropFile("en", "en.properties",
				listLocalizzazione);
		listLocalizzazione = getAllLocForPropFile("it", "it.properties",
				listLocalizzazione);

		return listLocalizzazione;
	}
	

	/**
	 * Return the list of localizzazioni taken from properties file
	 */
	@Override
	public List<Localizzazione> listaLocalizzazioni(String locale) throws IOException {
		List<Localizzazione> listLocalizzazione = new ArrayList<Localizzazione>();
		listLocalizzazione = getAllLocForPropFile(locale, locale + ".properties", listLocalizzazione);
		return listLocalizzazione;
	}
	

	/**
	 * Return the list of localizzazioni taken from local properties file
	 */
	private List<Localizzazione> getAllLocForPropFile(String locale,
			String propName, List<Localizzazione> listLocalizzazione)
			throws IOException {

		Properties localProp = new Properties();

		InputStream input = getResourceAsStream(propName);

		localProp.load(input);

		// Get all Keys
		for (String propKey : localProp.stringPropertyNames()) {

			// Create Localizzazione element
			listLocalizzazione.add(getLocalizzazioneForLocale(localProp,
					locale, propKey));
		}

		// close inputstream for localized file
		if (input != null)
			input.close();

		return listLocalizzazione;
	}

	/**
	 * Return single localization object
	 */
	private Localizzazione getLocalizzazioneForLocale(Properties localFile,
			String locale, String propKey) throws IOException {

		// Key is Sezione.id, so split key to find id inside string
		String[] numberSplit = propKey.split("\\.");
		String codice = "-1";
		if (numberSplit.length > 1) {
			codice = numberSplit[(1)]; // get id
		}

		Localizzazione localizzazione = new Localizzazione();
		localizzazione.setChiave(propKey);
		localizzazione.setIdLocalizz(Double.parseDouble(codice));
		localizzazione.setValore(localFile.getProperty(propKey));
		localizzazione.setLocale(locale);
		return localizzazione;
	}

	/**
	 * Find localization by KEY
	 */
	@Override
	public Localizzazione findLocalizzazioneById(String id) throws IOException {

		// Search in en file
		Localizzazione local = findLocalizzazioneInPropFile("en",
				"en.properties", id);

		if (local == null) {
			local = findLocalizzazioneInPropFile("it", "it.properties", id);
		}
		return local;
	}

	/**
	 * Find localization in specific prop file
	 * 
	 * @throws IOException
	 */
	private Localizzazione findLocalizzazioneInPropFile(String locale,
			String propFileName, String id) throws IOException {

		Localizzazione localizz = null;

		Properties localProp = new Properties();

		InputStream input = getResourceAsStream(propFileName);

		localProp.load(input);

		for (String propKey : localProp.stringPropertyNames()) {

			if (propKey.equalsIgnoreCase(id)) {

				// Key is Sezione.id, so split key to find id inside string
				String[] numberSplit = propKey.split("\\.");
				String codice = numberSplit[(1)]; // get id

				localizz = new Localizzazione();
				localizz.setChiave(propKey);
				localizz.setLocale(locale);
				localizz.setIdLocalizz(Double.parseDouble(codice));
				localizz.setValore(localProp.getProperty(propKey));

			}
		}

		// close inputstream for localized file
		if (input != null)
			input.close();

		return localizz;

	}

	/**
	 * Insert or Update Localizzazione in property file
	 * 
	 * @throws IOException
	 */
	@Override
	public void insUpdLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		String localeFile = localizzazione.getLocale() + ".properties";
		String key = localizzazione.getChiave();
		
		Properties localProp = getPropertyFile(localeFile);

		OutputStream out = new FileOutputStream(localeFile);
		localProp.setProperty(key, localizzazione.getValore());
		localProp.store(out, null);

		if (out != null) {
			out.close();
		}

	}

	/**
	 * Insert localization in specific prop file
	 * 
	 * @throws IOException
	 */
	@Override
	public void insertLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		insUpdLocalizzazione(localizzazione);
	}

	/**
	 * Delete localization in specific prop file
	 * 
	 * @throws IOException
	 */
	@Override
	public void deleteLocalizzazione(Localizzazione localizzazione)
			throws FileNotFoundException, IOException {

		String localeFile = localizzazione.getLocale() + ".properties";
		String key = localizzazione.getChiave();
		
		Properties localProp = getPropertyFile(localeFile);

		OutputStream out = new FileOutputStream(localeFile);
		localProp.remove(key);
		localProp.store(out, null);

		if (out != null) {
			out.close();
		}
	}

	/**
	 * Main Method for test
	 * 
	 */

	public static void main(String[] args) throws IOException {

		PropertyDatabaseServiceImpl propDbImp = new PropertyDatabaseServiceImpl();

		Localizzazione localizzazione = new Localizzazione(9.0, "sezione.2.dettaglio.view.titolo", "en", "hi");
		propDbImp.deleteLocalizzazione(localizzazione);

	}

}
