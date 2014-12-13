package it.eng.eli4u.service;

import it.eng.eli4u.imieidati.utils.IMieiDatiConstants;
import it.eng.eli4u.service.command.IServiceCommand;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Deprecated
public class IMieiDatiServlet extends HttpServlet {
	private final Logger logger = LoggerFactory.getLogger(IMieiDatiServlet.class);

	private static final long serialVersionUID = 4443214037048729781L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			resp.setCharacterEncoding("UTF-8");
			String command = req.getParameter("cmd");

			String clazz = "it.eng.eli4u.service.command."+command+"Command";
			IServiceCommand serviceCommand = (IServiceCommand) Class.forName(clazz).newInstance();
			String ret = null;//serviceCommand.execute(req);
			
			resp.getWriter().write(ret);
		}
		catch (Throwable t ) {
			logger.error("ERROR invoking service '" + req.getParameter("cmd") + "' -- " + t.getMessage(), t);

			//ErrorMessage errorMessage = new ErrorMessage(IMieiDatiConstants.CODICE_ERR_GENERICO, ""+t.getMessage());
			//String ret = new Gson().toJson(errorMessage);
			String ret = IMieiDatiConstants.getDefaultErrorMessage();
			
			resp.getWriter().write(ret);
			
		}
	}
	
}
