package it.eng.fascicolo.intornoame.beans;

public class KmlBean {

	private String id;
	private String descrizione;
	
	public KmlBean() {
	}

	public KmlBean(String id, String descrizione) {
		super();
		this.id = id;
		this.descrizione = descrizione;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
}
