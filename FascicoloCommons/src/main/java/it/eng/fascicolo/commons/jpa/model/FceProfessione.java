package it.eng.fascicolo.commons.jpa.model;

// Generated Dec 5, 2014 12:35:56 PM by Hibernate Tools 4.0.0

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * FceProfessione generated by hbm2java
 */
@Entity
@Table(name = "FCE_PROFESSIONE", uniqueConstraints = @UniqueConstraint(columnNames = "DESCRIZIONE"))
public class FceProfessione implements java.io.Serializable {

	private BigDecimal idProfessione;
	private String descrizione;
	private Set<FceUtente> fceUtentes = new HashSet<FceUtente>(0);

	public FceProfessione() {
	}

	public FceProfessione(BigDecimal idProfessione, String descrizione) {
		this.idProfessione = idProfessione;
		this.descrizione = descrizione;
	}

	public FceProfessione(BigDecimal idProfessione, String descrizione, Set<FceUtente> fceUtentes) {
		this.idProfessione = idProfessione;
		this.descrizione = descrizione;
		this.fceUtentes = fceUtentes;
	}

	@Id
	@Column(name = "ID_PROFESSIONE", unique = true, nullable = false, precision = 22, scale = 0)
	public BigDecimal getIdProfessione() {
		return this.idProfessione;
	}

	public void setIdProfessione(BigDecimal idProfessione) {
		this.idProfessione = idProfessione;
	}

	@Column(name = "DESCRIZIONE", unique = true, nullable = false, length = 200)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fceProfessione")
	public Set<FceUtente> getFceUtentes() {
		return this.fceUtentes;
	}

	public void setFceUtentes(Set<FceUtente> fceUtentes) {
		this.fceUtentes = fceUtentes;
	}

}
