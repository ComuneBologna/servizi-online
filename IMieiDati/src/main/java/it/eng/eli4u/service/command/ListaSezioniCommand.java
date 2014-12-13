package it.eng.eli4u.service.command;

import it.eng.eli4u.imieidati.model.Sezione;
import it.eng.eli4u.imieidati.services.IDatabaseService;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;

import java.io.IOException;
import java.util.List;

import javax.portlet.ResourceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;


public class ListaSezioniCommand implements IServiceCommand {
	Logger logger = LoggerFactory.getLogger(ListaSezioniCommand.class);

	@Autowired
	IDatabaseService databaseService;
	@Autowired
	IIMieiDatiPortalService portalService;

	@Override
	public String execute(ResourceRequest req) throws IOException {

		Gson gson = new Gson();

		try {
			// TODO controlli preliminari su utente e token in caso torna un
			// errore specializzato

			// lista delle sezioni
			List<Sezione> listaSezione = databaseService.listaSezioni();

			// Localize Title and Description of Sections
			String locale = portalService.linguaPortale(req);			
			int i = 0;
			for (Sezione sezione : listaSezione) {

				if (sezione.getCodice().equals(
						IMieiDatiConstants.CODICE_SEZIONE_I_MIEI_DATI)) {
					i++;
					continue;
				}
				sezione.setTitolo(databaseService.mesaggio(locale, "sezione."
						+ i + ".titolo"));
				sezione.setDescrizione(databaseService.mesaggio(locale,
						"sezione." + i + ".descrizione"));
				
				// avoid sending unuseful but sensitive information
				sezione.setNomeServizio(null);
				sezione.setUrlServizio(null);
				sezione.setUsernameServizio(null);
				sezione.setPasswordServizio(null);
				sezione.setParametriInputServizio(null);
				sezione.setRenderingClass(null);
				
				i++;
			}
			
			String output = gson.toJson(listaSezione);

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
