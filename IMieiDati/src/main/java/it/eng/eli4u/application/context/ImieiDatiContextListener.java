package it.eng.eli4u.application.context;

import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.services.portal.LiferayPortalService;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

public class ImieiDatiContextListener implements ServletContextAware {
	@Autowired
	IIMieiDatiPortalService portalServiceImpl;

	@Override
	public void setServletContext(ServletContext servletContext) {
		// set if liferay or websphere in ServletContext
		if (portalServiceImpl instanceof LiferayPortalService) {

			servletContext.setAttribute("portalService", "liferay");

		} else {
			servletContext.setAttribute("portalService", "websphere");

		}
	}
}
