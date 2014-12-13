package it.eng.eli4u.imieidati;

import it.eng.eli4u.application.context.SpringApplicationContext;
import it.eng.eli4u.imieidati.services.IIMieiDatiPortalService;
import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;
import it.eng.eli4u.service.command.IServiceCommand;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMieiDatiCaching extends GenericPortlet {

	Logger logger = LoggerFactory.getLogger(IMieiDatiCaching.class);

	@Override
	protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {

		try {
			IIMieiDatiPortalService datiPortalService = SpringApplicationContext.getContext().getBeansOfType(IIMieiDatiPortalService.class).values().iterator().next();
			datiPortalService.setSessionAttributes(renderRequest);
			boolean isAdmin = datiPortalService.isAdministrator();
			renderRequest.setAttribute(IMieiDatiConstants.IS_ADMIN_ATTRIBUTE,isAdmin);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/viewCaching.jsp");
		dispatcher.include(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException {
		logger.debug("serveResource: IN");
		resp.setCharacterEncoding("UTF-8");
		String command = req.getParameter("cmd");
		logger.debug("command: {}",command);
		String ret = null;
		String clazz = "it.eng.eli4u.service.command." + command + "Command";
		try {
			Class<?> forName = Class.forName(clazz);
			IServiceCommand serviceCommand = (IServiceCommand) SpringApplicationContext.getContext().getBean(forName);
			ret = serviceCommand.execute(req);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		logger.trace("return: {}", ret);
		resp.getWriter().write(ret);
		logger.debug("serveResource: OUT");
	}
}