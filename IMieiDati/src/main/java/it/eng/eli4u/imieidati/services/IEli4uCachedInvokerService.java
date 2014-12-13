package it.eng.eli4u.imieidati.services;

import it.eli4you.engines.masterindex.core.xml.input.MessaggioInputType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eng.eli4u.imieidati.services.eli4u.DettaglioEntry;
/**
 * Interfaccia per l'invocazione dei servizi eli4u mediata da caching
 * @author pluttero
 *
 */
public interface IEli4uCachedInvokerService {

	public DettaglioEntry invokeServiceCacheable(String serviceUrl, String serviceName, String username, String password, String uniqueUserId, String uniqueIdSezione,MessaggioInputType messaggioInput) throws Exception;

	public DettaglioType fromDettaglioEntry(DettaglioEntry de);
}
