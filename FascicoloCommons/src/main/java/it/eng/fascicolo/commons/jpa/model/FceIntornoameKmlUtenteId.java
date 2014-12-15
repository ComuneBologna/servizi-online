package it.eng.fascicolo.commons.jpa.model;

// Generated Dec 5, 2014 12:35:56 PM by Hibernate Tools 4.0.0

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * FceIntornoameKmlUtenteId generated by hbm2java
 */
@Embeddable
public class FceIntornoameKmlUtenteId implements java.io.Serializable {

	private BigDecimal idkml;
	private BigDecimal idutente;

	public FceIntornoameKmlUtenteId() {
	}

	public FceIntornoameKmlUtenteId(BigDecimal idkml, BigDecimal idutente) {
		this.idkml = idkml;
		this.idutente = idutente;
	}

	@Column(name = "IDKML", nullable = false, precision = 38, scale = 0)
	public BigDecimal getIdkml() {
		return this.idkml;
	}

	public void setIdkml(BigDecimal idkml) {
		this.idkml = idkml;
	}

	@Column(name = "IDUTENTE", nullable = false, precision = 38, scale = 0)
	public BigDecimal getIdutente() {
		return this.idutente;
	}

	public void setIdutente(BigDecimal idutente) {
		this.idutente = idutente;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FceIntornoameKmlUtenteId))
			return false;
		FceIntornoameKmlUtenteId castOther = (FceIntornoameKmlUtenteId) other;

		return ((this.getIdkml() == castOther.getIdkml()) || (this.getIdkml() != null && castOther.getIdkml() != null && this.getIdkml().equals(castOther.getIdkml())))
				&& ((this.getIdutente() == castOther.getIdutente()) || (this.getIdutente() != null && castOther.getIdutente() != null && this.getIdutente().equals(castOther.getIdutente())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getIdkml() == null ? 0 : this.getIdkml().hashCode());
		result = 37 * result + (getIdutente() == null ? 0 : this.getIdutente().hashCode());
		return result;
	}

}