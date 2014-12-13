package it.eng.eli4u.imieidati.services.eli4u;

import java.io.Serializable;

import javax.xml.bind.JAXBException;

public class DettaglioEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	String xml, uniqueUserId, uniqueIdSezione;

	public DettaglioEntry(String xml, String uniqueUserId, String uniqueIdSezione) throws JAXBException {
		this.xml = xml;
		this.uniqueIdSezione = uniqueIdSezione;
		this.uniqueUserId = uniqueUserId;

	}

	public String getXml() {
		return xml;
	}

	public String getUniqueUserId() {
		return uniqueUserId;
	}

	public String getUniqueIdSezione() {
		return uniqueIdSezione;
	}

}