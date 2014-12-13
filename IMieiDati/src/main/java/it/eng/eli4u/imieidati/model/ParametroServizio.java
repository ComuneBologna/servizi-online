package it.eng.eli4u.imieidati.model;

import java.io.Serializable;

public class ParametroServizio implements Serializable {
	
	private String codice;
	private String valore;
	public String getCodice() {
		return codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getValore() {
		return valore;
	}
	public void setValore(String valore) {
		this.valore = valore;
	}
	
	
	

}
