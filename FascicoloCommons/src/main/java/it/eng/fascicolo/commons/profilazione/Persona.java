package it.eng.fascicolo.commons.profilazione;

import java.io.Serializable;
import java.util.Set;
/**
 * Entità che descrive una persona autenticata su fascicolo. Questa classe è ritrovata dalle portlet in sessione
 * @author pluttero
 *
 */
public interface Persona extends Serializable {
	public static final String SESSION_KEY = "fascicolo.persona";
	public static final String FORMATO_DATA_SIMPLEDATEFORMAT = "dd/MM/yyyy";
	
	public String getIdAccount();

	public String getNome();

	public String getCognome();

	public String getCf();

	public String getEmail();
	
	public String getEmailPec();
	
	public String getTelefono();
	
	public String getCellulare();

	public String getNascitaComune();
	
	public String getNascitaIdComune();
	
	public String getNascitaData();
	
	public String getResidenzaVia();

	public String getResidenzaNumeroCivico();

	public String getResidenzaCap();

	public String getResidenzaIdComune();

	public String getResidenzaComune();

	public String getDomicilioVia();

	public String getDomicilioNumeroCivico();

	public String getDomicilioCap();

	public String getDomicilioIdComune();

	public String getDomicilioComune();
	
	public String getProfessione();
	
	public Set<String> getElencoInteressi();
	
	public String getFotoBase64();
	
	public String getFotoMimeType();
	
	public String getLogoEBolognaBase64();
	
	public String getEmailNewsletter();
	
	public String getLogoEBolognaNickName();
	
	public String getLogoEBolognaColor();
	
	public String getLogoEBolognaMimeType();
	
	public String getLogoEBolognaMixed();
	 
	public String getLogoEBolognaMixedMimetype();
	
	public String getStatoIscrizioneNewsletter();
	
}
