package it.eng.eli4u.imieidati.services.eli4u;

import it.eli4you.engines.masterindex.core.xml.input.MessaggioInputType;
import it.eli4you.engines.masterindex.core.xml.input.ParamType;
import it.eli4you.engines.masterindex.core.xml.output.DettaglioType;
import it.eng.eli4u.imieidati.services.IEli4uCachedInvokerService;
import it.service.integration.Eli4UServiceInvoker;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;


public class Eli4uCachedInvokerServiceImpl implements IEli4uCachedInvokerService {
	private JAXBContext context = null;
	private final Logger logger = LoggerFactory.getLogger(Eli4uCachedInvokerServiceImpl.class);

	public Eli4uCachedInvokerServiceImpl() {
		logger.debug("Init context jaxb");
		try {
			context = JAXBContext.newInstance(DettaglioType.class);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Cacheable(value = "imieiDatiCache", key = "T(java.lang.String).valueOf(#uniqueUserId).concat('-').concat(#uniqueIdSezione)")
	@Override
	public DettaglioEntry invokeServiceCacheable(String serviceUrl, String serviceName, String username, String password, String uniqueUserId, String uniqueIdSezione, MessaggioInputType messaggioInput) throws Exception {

		logger.debug("invokeService: IN");
		logger.debug("serviceName: {}", serviceName);
		logger.debug("uniqueUserId: {}", uniqueUserId);
		logger.debug("uniqueIdSezione: {}", uniqueIdSezione);

		for (ParamType pt : messaggioInput.getParam()) {
			logger.debug("param internalKey:{} externalKey:{} value:{}", pt.getInternalKey(), pt.getExternalKey(), pt.getValue());
		}
		Eli4UServiceInvoker serviceInvoker = new Eli4UServiceInvoker();
		DettaglioType dettaglioType = serviceInvoker.invokeService(serviceUrl, serviceName, username, password, messaggioInput);
		DettaglioEntry de = null;
		try {
			String xml = marshal(dettaglioType);
			de = new DettaglioEntry(xml, uniqueUserId, uniqueIdSezione);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return de;

	}

	@Override
	public DettaglioType fromDettaglioEntry(DettaglioEntry de) {
		DettaglioType dt = null;
		try {
			dt = unmarshal(de.getXml());
		} catch (JAXBException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return dt;
	}

	private String marshal(DettaglioType dettaglioType) throws JAXBException {
		final Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		final StringWriter w = new StringWriter();
		m.marshal(dettaglioType, w);
		return w.toString();
	}

	public DettaglioType unmarshal(final String xml) throws JAXBException {
		return (DettaglioType) context.createUnmarshaller().unmarshal(new StringReader(xml));
	}

	
}
