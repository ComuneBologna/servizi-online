package it.eng.eli4u.service.command;

import javax.portlet.ResourceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheSezioneCommand implements IServiceCommand{
	private Logger logger = LoggerFactory.getLogger(CacheSezioneCommand.class);
	@Autowired
	DettaglioSezioneCommand sezione;
	// TODO gestire un msg di ritorno
	// TODO ottimizzare, qui non serve i18n e trasformazione html. template method in dettaglio sezione
	@Override
	public String execute(ResourceRequest req) throws Exception {
		logger.debug("execute: IN");
		String ret = "OK";
		try {
			sezione.execute(req);			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			ret = "KO";
		}
		logger.debug("execute: OUT");
		return ret;

	}
}
