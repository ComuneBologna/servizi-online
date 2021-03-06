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
 * FceTipoAccount generated by hbm2java
 */
@Entity
@Table(name = "FCE_TIPO_ACCOUNT", uniqueConstraints = @UniqueConstraint(columnNames = { "DESCRIZIONE", "TIPO" }))
public class FceTipoAccount implements java.io.Serializable {

	private BigDecimal idTipoAccount;
	private String descrizione;
	private String tipo;
	private Set<FceUtente> fceUtentes = new HashSet<FceUtente>(0);

	public FceTipoAccount() {
	}

	public FceTipoAccount(BigDecimal idTipoAccount, String descrizione, String tipo) {
		this.idTipoAccount = idTipoAccount;
		this.descrizione = descrizione;
		this.tipo = tipo;
	}

	public FceTipoAccount(BigDecimal idTipoAccount, String descrizione, String tipo, Set<FceUtente> fceUtentes) {
		this.idTipoAccount = idTipoAccount;
		this.descrizione = descrizione;
		this.tipo = tipo;
		this.fceUtentes = fceUtentes;
	}

	@Id
	@Column(name = "ID_TIPO_ACCOUNT", unique = true, nullable = false, precision = 38, scale = 0)
	public BigDecimal getIdTipoAccount() {
		return this.idTipoAccount;
	}

	public void setIdTipoAccount(BigDecimal idTipoAccount) {
		this.idTipoAccount = idTipoAccount;
	}

	@Column(name = "DESCRIZIONE", nullable = false, length = 100)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Column(name = "TIPO", nullable = false, length = 10)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fceTipoAccount")
	public Set<FceUtente> getFceUtentes() {
		return this.fceUtentes;
	}

	public void setFceUtentes(Set<FceUtente> fceUtentes) {
		this.fceUtentes = fceUtentes;
	}

}
