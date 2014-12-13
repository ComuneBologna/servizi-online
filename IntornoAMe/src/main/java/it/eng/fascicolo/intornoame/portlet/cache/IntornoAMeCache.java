package it.eng.fascicolo.intornoame.portlet.cache;

public interface IntornoAMeCache {

	/**
	 * Ritorna il testo per frontpage. La stringa è sempre not null
	 * @param urlStringDrupal
	 * @return
	 */
	public String getFrontPageText(String urlStringDrupal);
}
