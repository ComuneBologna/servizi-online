package it.eng.eli4u.imieidati.utils;

public class IMieiDatiConstants {
	
	//Codici di errore
	public static final String CODICE_ERR_GENERICO="999";
	
	// messaggio errore generico
	public static final String MSG_ERR_GENERICO="Servizio non disponibile";
	
	//codici di particolari sezioni
	public static final String CODICE_SEZIONE_I_MIEI_DATI="IMD";
	public static final String ID_SEZIONE_I_MIEI_DATI="999";
	
	public static final String PORTLET_NOME_REQUEST_ATTRIBUTE = "imieidati.current_portlet_name";
	public static final String IS_ADMIN_ATTRIBUTE = "imieidati.isadmin";

	public static String getDefaultErrorMessage() {
		String ret = "" + 
				"<div class=\"panel-body\">" + 
				"    <div style=\"margin-bottom: 0px;\" class=\"alert alert-danger\">" + 
				"        <strong>Errore! </strong> Servizio momentaneamente non disponibile." +
				"    </div>" +
				"</div>";
		return ret;
	}
	
	public static String getNoSectionMessage() {
		String ret = "" + 
				"<div class=\"panel-body\">" + 
				"    <div style=\"margin-bottom: 0px;\" class=\"alert alert-danger\">" + 
				"        Nessun dato da visualizzare." +
				"    </div>" +
				"</div>";
		return ret;
	}
	
}
