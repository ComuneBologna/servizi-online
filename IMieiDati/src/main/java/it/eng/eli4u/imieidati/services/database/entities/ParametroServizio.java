package it.eng.eli4u.imieidati.services.database.entities;

// Generated il giorno 31-lug-2014 11.28.28 by Hibernate Tools 3.4.0.CR1

/**
 * ParametroServizio generated by hbm2java
 */
public class ParametroServizio implements java.io.Serializable {

	private double idParam;
	private Sezione sezione;
	private String codice;
	private String valore;

	public ParametroServizio() {
	}

	public ParametroServizio(double idParam, Sezione sezione, String codice) {
		this.idParam = idParam;
		this.sezione = sezione;
		this.codice = codice;
	}

	public ParametroServizio(double idParam, Sezione sezione, String codice,
			String valore) {
		this.idParam = idParam;
		this.sezione = sezione;
		this.codice = codice;
		this.valore = valore;
	}

	public double getIdParam() {
		return this.idParam;
	}

	public void setIdParam(double idParam) {
		this.idParam = idParam;
	}

	public Sezione getSezione() {
		return this.sezione;
	}

	public void setSezione(Sezione sezione) {
		this.sezione = sezione;
	}

	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getValore() {
		return this.valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

}