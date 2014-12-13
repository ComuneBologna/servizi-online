package it.eng.fascicolo.intornoame.beans;

import java.util.ArrayList;
import java.util.List;

public class CategoriaKmlBean {
	
	private String idCategoria;
	private String descrizione;
	private List<KmlBean> kmls=new ArrayList<KmlBean>();

	public String getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(String idCategoria) {
		this.idCategoria = idCategoria;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public List<KmlBean> getKmls() {
		return kmls;
	}
	public void setKmls(List<KmlBean> kmls) {
		this.kmls = kmls;
	}
	
}
