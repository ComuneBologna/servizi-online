package it.eng.eli4u.imieidati.model;


import java.io.Serializable;
import java.util.List;

public class Sezione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String codice;
	private String titolo;
	private String descrizione;
	private String image;
	private String iconImage;
	private String uriXsl;
	private String uriXslMobile;
	private String datiXsl;
	private String datiXslMobile;
	private String htmlStatico;
	private boolean flgAbilitato;
	private String idServizio;
	private String urlServizio;
	private String portletAbilitate;
	private String renderingClass;
	
	//
	private String nomeServizio;
	private String usernameServizio;
	private String passwordServizio;

	private List<ParametroServizio> parametriInputServizio;
	
	public Sezione() {
	}

	public Sezione(String id, String codice, String titolo, boolean flgAbilitato) {
		this.id = id;
		this.codice = codice;
		this.titolo = titolo;
		this.flgAbilitato = flgAbilitato;
	}

	public Sezione(String id, String codice, String titolo, String descrizione,
			String image, String uriXsl, String uriXslMobile, String datiXsl, String datiXslMobile, String htmlStatico, boolean flgAbilitato,
			String idServizio, String urlServizio, String nomeServizio,
			String usernameServizio, String passwordeServizio,
			List<ParametroServizio> parametroServizios, String portletAbilitate, String renderingClass) {
		this.id = id;
		this.codice = codice;
		this.titolo = titolo;
		this.descrizione = descrizione;
		this.image = image;
		this.uriXsl = uriXsl;
		this.uriXslMobile = uriXslMobile;
		this.datiXsl = datiXsl;
		this.datiXslMobile = datiXslMobile;
		this.htmlStatico = htmlStatico;
		this.flgAbilitato = flgAbilitato;
		this.idServizio = idServizio;
		this.urlServizio = urlServizio;
		this.nomeServizio = nomeServizio;
		this.usernameServizio = usernameServizio;
		this.passwordServizio = passwordeServizio;
		this.parametriInputServizio = parametroServizios;
		this.portletAbilitate = portletAbilitate;
		this.renderingClass = renderingClass;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUriXsl() {
		return uriXsl;
	}

	public void setUriXsl(String uriXsl) {
		this.uriXsl = uriXsl;
	}

	public String getUriXslMobile() {
		return uriXslMobile;
	}

	public void setUriXslMobile(String uriXslMobile) {
		this.uriXslMobile = uriXslMobile;
	}

	public String getHtmlStatico() {
		return htmlStatico;
	}

	public void setHtmlStatico(String htmlStatico) {
		this.htmlStatico = htmlStatico;
	}

	public boolean isFlgAbilitato() {
		return flgAbilitato;
	}

	public void setFlgAbilitato(boolean flgAbilitato) {
		this.flgAbilitato = flgAbilitato;
	}

	public String getIdServizio() {
		return idServizio;
	}

	public void setIdServizio(String idServizio) {
		this.idServizio = idServizio;
	}

	public String getUrlServizio() {
		return urlServizio;
	}

	public void setUrlServizio(String urlServizio) {
		this.urlServizio = urlServizio;
	}

	public List<ParametroServizio> getParametriInputServizio() {
		return parametriInputServizio;
	}

	public void setParametriInputServizio(
			List<ParametroServizio> parametriInputServizio) {
		this.parametriInputServizio = parametriInputServizio;
	}

	public String getNomeServizio() {
		return nomeServizio;
	}

	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;
	}

	public String getUsernameServizio() {
		return usernameServizio;
	}

	public void setUsernameServizio(String usernameServizio) {
		this.usernameServizio = usernameServizio;
	}

	public String getPasswordServizio() {
		return passwordServizio;
	}

	public void setPasswordServizio(String passwordServizio) {
		this.passwordServizio = passwordServizio;
	}

	public String getPortletAbilitate() {
		return portletAbilitate;
	}

	public void setPortletAbilitate(String portletAbilitate) {
		this.portletAbilitate = portletAbilitate;
	}

	public String getDatiXsl() {
		return datiXsl;
	}

	public void setDatiXsl(String datiXsl) {
		this.datiXsl = datiXsl;
	}

	public String getDatiXslMobile() {
		return datiXslMobile;
	}

	public void setDatiXslMobile(String datiXslMobile) {
		this.datiXslMobile = datiXslMobile;
	}

	public String getRenderingClass() {
		return renderingClass;
	}

	public void setRenderingClass(String renderingClass) {
		this.renderingClass = renderingClass;
	}

	public String getIconImage() {
		return iconImage;
	}

	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}


}
