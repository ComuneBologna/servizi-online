package it.eng.eli4u.imieidati.model;

import java.io.Serializable;

public class ErrorMessage implements Serializable{
	
	private String codiceErrore;
	private String descrizione;
	
	
	
	public ErrorMessage(String codice, String descrizione) {
		super();
		this.codiceErrore = codice;
		this.descrizione = descrizione;
	}



	public String getCodiceErrore() {
		return codiceErrore;
	}



	public void setCodiceErrore(String codiceErrore) {
		this.codiceErrore = codiceErrore;
	}



	public String getDescrizione() {
		return descrizione;
	}



	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


}
