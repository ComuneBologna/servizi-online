package it.eng.eli4u.service.command;

import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.services.database.entities.Localizzazione;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;

import java.io.IOException;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.xml.bind.JAXBException;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;


public class ListaLocalizzazioniCommand implements IServiceCommand {
	Logger logger = LoggerFactory.getLogger(ListaLocalizzazioniCommand.class);

	@Autowired
	IDatabaseService databaseService;
	@Autowired
	IIMieiDatiPortalService portalService;

	@Override
	public String execute(ResourceRequest req) throws IOException, JAXBException, DocumentException, Exception {

		Gson gson = new Gson();

		try {
			// TODO controlli preliminari su utente e token in caso torna un
			// errore specializzato

			// lista delle localizzazione
			List<Localizzazione> listaLocalizzazioni = databaseService.listaLocalizzazioni();

			String output = gson.toJson(listaLocalizzazioni);

			return output;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//ErrorMessage errorMessage = new ErrorMessage(IMieiDatiConstants.CODICE_ERR_GENERICO, "" + e.getMessage());
			//return gson.toJson(errorMessage);
			String ret = IMieiDatiConstants.getDefaultErrorMessage();
			return ret;
		}

	}

}
