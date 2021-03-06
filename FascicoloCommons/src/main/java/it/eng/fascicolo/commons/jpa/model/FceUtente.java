package it.eng.fascicolo.commons.jpa.model;

// Generated Dec 5, 2014 12:35:56 PM by Hibernate Tools 4.0.0

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * FceUtente generated by hbm2java
 */
@Entity
@Table(name = "FCE_UTENTE", uniqueConstraints = @UniqueConstraint(columnNames = "ID_ACCOUNT"))
public class FceUtente implements java.io.Serializable {

	private BigDecimal idutente;
	private FceComuni fceComuniByResidenzaIdComune;
	private FceTipoAccount fceTipoAccount;
	private FceProfessione fceProfessione;
	private FceComuni fceComuniByNascitaIdComune;
	private FceComuni fceComuniByDomicilioIdComune;
	private FceStatoNewsletter fceStatoNewsletter;
	private String nome;
	private String cognome;
	private Date nascitaData;
	private String codicefiscale;
	private String residenzaVia;
	private String residenzaCivico;
	private String email;
	private String telefono;
	private String cellulare;
	private byte[] foto;
	private String idAccount;
	private String residenzaCap;
	private String domicilioVia;
	private String domicilioCivico;
	private String domicilioCap;
	private String fotoMimetype;
	private String emailPec;
	private Character primoAccesso;
	private byte[] logoEBologna;
	private String emailNewsletter;
	private String logoEBolognaNickName;
	private String logoEBolognaColor;
	private String logoEBolognaMimetype;
	private byte[] logoEBolognaMixed;
	private String logoEBolognaMixedMimetype;
	private String nascitaComune;
	private String nascitaProvincia;
	private String residenzaComune;
	private String residenzaProvincia;
	private Set<FceInteresse> fceInteresses = new HashSet<FceInteresse>(0);
	private Set<FceAgenda> fceAgendas = new HashSet<FceAgenda>(0);

	public FceUtente() {
	}

	public FceUtente(FceTipoAccount fceTipoAccount) {
		this.fceTipoAccount = fceTipoAccount;
	}

	public FceUtente(FceComuni fceComuniByResidenzaIdComune, FceTipoAccount fceTipoAccount, FceProfessione fceProfessione, FceComuni fceComuniByNascitaIdComune, FceComuni fceComuniByDomicilioIdComune, FceStatoNewsletter fceStatoNewsletter, String nome, String cognome, Date nascitaData,
			String codicefiscale, String residenzaVia, String residenzaCivico, String email, String telefono, String cellulare, byte[] foto, String idAccount, String residenzaCap, String domicilioVia, String domicilioCivico, String domicilioCap, String fotoMimetype, String emailPec,
			Character primoAccesso, byte[] logoEBologna, String emailNewsletter, String logoEBolognaNickName, String logoEBolognaColor, String logoEBolognaMimetype, byte[] logoEBolognaMixed, String logoEBolognaMixedMimetype, String nascitaComune, String nascitaProvincia, String residenzaComune,
			String residenzaProvincia, Set<FceInteresse> fceInteresses, Set<FceAgenda> fceAgendas) {
		this.fceComuniByResidenzaIdComune = fceComuniByResidenzaIdComune;
		this.fceTipoAccount = fceTipoAccount;
		this.fceProfessione = fceProfessione;
		this.fceComuniByNascitaIdComune = fceComuniByNascitaIdComune;
		this.fceComuniByDomicilioIdComune = fceComuniByDomicilioIdComune;
		this.fceStatoNewsletter = fceStatoNewsletter;
		this.nome = nome;
		this.cognome = cognome;
		this.nascitaData = nascitaData;
		this.codicefiscale = codicefiscale;
		this.residenzaVia = residenzaVia;
		this.residenzaCivico = residenzaCivico;
		this.email = email;
		this.telefono = telefono;
		this.cellulare = cellulare;
		this.foto = foto;
		this.idAccount = idAccount;
		this.residenzaCap = residenzaCap;
		this.domicilioVia = domicilioVia;
		this.domicilioCivico = domicilioCivico;
		this.domicilioCap = domicilioCap;
		this.fotoMimetype = fotoMimetype;
		this.emailPec = emailPec;
		this.primoAccesso = primoAccesso;
		this.logoEBologna = logoEBologna;
		this.emailNewsletter = emailNewsletter;
		this.logoEBolognaNickName = logoEBolognaNickName;
		this.logoEBolognaColor = logoEBolognaColor;
		this.logoEBolognaMimetype = logoEBolognaMimetype;
		this.logoEBolognaMixed = logoEBolognaMixed;
		this.logoEBolognaMixedMimetype = logoEBolognaMixedMimetype;
		this.nascitaComune = nascitaComune;
		this.nascitaProvincia = nascitaProvincia;
		this.residenzaComune = residenzaComune;
		this.residenzaProvincia = residenzaProvincia;
		this.fceInteresses = fceInteresses;
		this.fceAgendas = fceAgendas;
	}

	@SequenceGenerator(name = "generator", sequenceName = "SEQ_FCE_UTENTE")
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "generator")
	@Column(name = "IDUTENTE", unique = true, nullable = false, precision = 38, scale = 0)
	public BigDecimal getIdutente() {
		return this.idutente;
	}

	public void setIdutente(BigDecimal idutente) {
		this.idutente = idutente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESIDENZA_ID_COMUNE")
	public FceComuni getFceComuniByResidenzaIdComune() {
		return this.fceComuniByResidenzaIdComune;
	}

	public void setFceComuniByResidenzaIdComune(FceComuni fceComuniByResidenzaIdComune) {
		this.fceComuniByResidenzaIdComune = fceComuniByResidenzaIdComune;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_TIPO_ACCOUNT", nullable = false)
	public FceTipoAccount getFceTipoAccount() {
		return this.fceTipoAccount;
	}

	public void setFceTipoAccount(FceTipoAccount fceTipoAccount) {
		this.fceTipoAccount = fceTipoAccount;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PROFESSIONE")
	public FceProfessione getFceProfessione() {
		return this.fceProfessione;
	}

	public void setFceProfessione(FceProfessione fceProfessione) {
		this.fceProfessione = fceProfessione;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NASCITA_ID_COMUNE")
	public FceComuni getFceComuniByNascitaIdComune() {
		return this.fceComuniByNascitaIdComune;
	}

	public void setFceComuniByNascitaIdComune(FceComuni fceComuniByNascitaIdComune) {
		this.fceComuniByNascitaIdComune = fceComuniByNascitaIdComune;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOMICILIO_ID_COMUNE")
	public FceComuni getFceComuniByDomicilioIdComune() {
		return this.fceComuniByDomicilioIdComune;
	}

	public void setFceComuniByDomicilioIdComune(FceComuni fceComuniByDomicilioIdComune) {
		this.fceComuniByDomicilioIdComune = fceComuniByDomicilioIdComune;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_STATO_ISCRIZIONE_NEWSLETTER")
	public FceStatoNewsletter getFceStatoNewsletter() {
		return this.fceStatoNewsletter;
	}

	public void setFceStatoNewsletter(FceStatoNewsletter fceStatoNewsletter) {
		this.fceStatoNewsletter = fceStatoNewsletter;
	}

	@Column(name = "NOME", length = 200)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "COGNOME", length = 200)
	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "NASCITA_DATA", length = 7)
	public Date getNascitaData() {
		return this.nascitaData;
	}

	public void setNascitaData(Date nascitaData) {
		this.nascitaData = nascitaData;
	}

	@Column(name = "CODICEFISCALE", length = 16)
	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}

	@Column(name = "RESIDENZA_VIA", length = 200)
	public String getResidenzaVia() {
		return this.residenzaVia;
	}

	public void setResidenzaVia(String residenzaVia) {
		this.residenzaVia = residenzaVia;
	}

	@Column(name = "RESIDENZA_CIVICO", length = 10)
	public String getResidenzaCivico() {
		return this.residenzaCivico;
	}

	public void setResidenzaCivico(String residenzaCivico) {
		this.residenzaCivico = residenzaCivico;
	}

	@Column(name = "EMAIL", length = 200)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "TELEFONO", length = 20)
	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Column(name = "CELLULARE", length = 20)
	public String getCellulare() {
		return this.cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	@Column(name = "FOTO")
	public byte[] getFoto() {
		return this.foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	@Column(name = "ID_ACCOUNT", unique = true)
	public String getIdAccount() {
		return this.idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}

	@Column(name = "RESIDENZA_CAP", length = 5)
	public String getResidenzaCap() {
		return this.residenzaCap;
	}

	public void setResidenzaCap(String residenzaCap) {
		this.residenzaCap = residenzaCap;
	}

	@Column(name = "DOMICILIO_VIA", length = 200)
	public String getDomicilioVia() {
		return this.domicilioVia;
	}

	public void setDomicilioVia(String domicilioVia) {
		this.domicilioVia = domicilioVia;
	}

	@Column(name = "DOMICILIO_CIVICO", length = 10)
	public String getDomicilioCivico() {
		return this.domicilioCivico;
	}

	public void setDomicilioCivico(String domicilioCivico) {
		this.domicilioCivico = domicilioCivico;
	}

	@Column(name = "DOMICILIO_CAP", length = 5)
	public String getDomicilioCap() {
		return this.domicilioCap;
	}

	public void setDomicilioCap(String domicilioCap) {
		this.domicilioCap = domicilioCap;
	}

	@Column(name = "FOTO_MIMETYPE", length = 100)
	public String getFotoMimetype() {
		return this.fotoMimetype;
	}

	public void setFotoMimetype(String fotoMimetype) {
		this.fotoMimetype = fotoMimetype;
	}

	@Column(name = "EMAIL_PEC", length = 200)
	public String getEmailPec() {
		return this.emailPec;
	}

	public void setEmailPec(String emailPec) {
		this.emailPec = emailPec;
	}

	@Column(name = "PRIMO_ACCESSO", length = 1)
	public Character getPrimoAccesso() {
		return this.primoAccesso;
	}

	public void setPrimoAccesso(Character primoAccesso) {
		this.primoAccesso = primoAccesso;
	}

	@Column(name = "LOGO_E_BOLOGNA")
	public byte[] getLogoEBologna() {
		return this.logoEBologna;
	}

	public void setLogoEBologna(byte[] logoEBologna) {
		this.logoEBologna = logoEBologna;
	}

	@Column(name = "EMAIL_NEWSLETTER", length = 200)
	public String getEmailNewsletter() {
		return this.emailNewsletter;
	}

	public void setEmailNewsletter(String emailNewsletter) {
		this.emailNewsletter = emailNewsletter;
	}

	@Column(name = "LOGO_E_BOLOGNA_NICK_NAME", length = 100)
	public String getLogoEBolognaNickName() {
		return this.logoEBolognaNickName;
	}

	public void setLogoEBolognaNickName(String logoEBolognaNickName) {
		this.logoEBolognaNickName = logoEBolognaNickName;
	}

	@Column(name = "LOGO_E_BOLOGNA_COLOR", length = 10)
	public String getLogoEBolognaColor() {
		return this.logoEBolognaColor;
	}

	public void setLogoEBolognaColor(String logoEBolognaColor) {
		this.logoEBolognaColor = logoEBolognaColor;
	}

	@Column(name = "LOGO_E_BOLOGNA_MIMETYPE", length = 40)
	public String getLogoEBolognaMimetype() {
		return this.logoEBolognaMimetype;
	}

	public void setLogoEBolognaMimetype(String logoEBolognaMimetype) {
		this.logoEBolognaMimetype = logoEBolognaMimetype;
	}

	@Column(name = "LOGO_E_BOLOGNA_MIXED")
	public byte[] getLogoEBolognaMixed() {
		return this.logoEBolognaMixed;
	}

	public void setLogoEBolognaMixed(byte[] logoEBolognaMixed) {
		this.logoEBolognaMixed = logoEBolognaMixed;
	}

	@Column(name = "LOGO_E_BOLOGNA_MIXED_MIMETYPE", length = 40)
	public String getLogoEBolognaMixedMimetype() {
		return this.logoEBolognaMixedMimetype;
	}

	public void setLogoEBolognaMixedMimetype(String logoEBolognaMixedMimetype) {
		this.logoEBolognaMixedMimetype = logoEBolognaMixedMimetype;
	}

	@Column(name = "NASCITA_COMUNE", length = 200)
	public String getNascitaComune() {
		return this.nascitaComune;
	}

	public void setNascitaComune(String nascitaComune) {
		this.nascitaComune = nascitaComune;
	}

	@Column(name = "NASCITA_PROVINCIA", length = 40)
	public String getNascitaProvincia() {
		return this.nascitaProvincia;
	}

	public void setNascitaProvincia(String nascitaProvincia) {
		this.nascitaProvincia = nascitaProvincia;
	}

	@Column(name = "RESIDENZA_COMUNE", length = 200)
	public String getResidenzaComune() {
		return this.residenzaComune;
	}

	public void setResidenzaComune(String residenzaComune) {
		this.residenzaComune = residenzaComune;
	}

	@Column(name = "RESIDENZA_PROVINCIA", length = 40)
	public String getResidenzaProvincia() {
		return this.residenzaProvincia;
	}

	public void setResidenzaProvincia(String residenzaProvincia) {
		this.residenzaProvincia = residenzaProvincia;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "FCE_UTENTE_INTERESSE", joinColumns = { @JoinColumn(name = "ID_UTENTE", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ID_INTERESSE", nullable = false, updatable = false) })
	public Set<FceInteresse> getFceInteresses() {
		return this.fceInteresses;
	}

	public void setFceInteresses(Set<FceInteresse> fceInteresses) {
		this.fceInteresses = fceInteresses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fceUtente")
	public Set<FceAgenda> getFceAgendas() {
		return this.fceAgendas;
	}

	public void setFceAgendas(Set<FceAgenda> fceAgendas) {
		this.fceAgendas = fceAgendas;
	}

}
