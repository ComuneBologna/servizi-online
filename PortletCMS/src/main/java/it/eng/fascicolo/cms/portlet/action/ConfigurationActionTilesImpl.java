package it.eng.fascicolo.cms.portlet.action;

import it.eng.fascicolo.cms.portlet.PortletCMSNewsFront;

import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;

/***
 * Class to handle saving configuration from the liferay Configuration view.
 * @author Eyal Gross
 *
 */
public class ConfigurationActionTilesImpl extends DefaultConfigurationAction {
	Logger logger = LoggerFactory.getLogger(ConfigurationActionTilesImpl.class);
    
	@Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		logger.debug("ConfigurationActionTilesImpl: processAction IN");
		
    	Properties prop = new Properties();
		prop.load(PortletCMSNewsFront.class.getClassLoader().getResourceAsStream("portlet.properties"));
		
		//Set preferences based on UI values or properties file if UI null.
    	PortletPreferences prefs = actionRequest.getPreferences();    	
    	prefs.setValue("baseURL", prefs.getValue("baseURL", prop.getProperty("tilesBaseHtmlUrl")));
    	prefs.setValue("tileId", actionRequest.getParameter("tileId"));
    	prefs.setValue("targetPage", actionRequest.getParameter("targetPage"));
    	prefs.setValue("targetId", actionRequest.getParameter("targetId"));
    	prefs.setValue("idWeak", actionRequest.getParameter("idWeak"));
    	prefs.setValue("idStrong", actionRequest.getParameter("idStrong"));
    	
    	logger.info("idStrong {}", actionRequest.getParameter("idStrong"));
    	logger.info("tileId {}", actionRequest.getParameter("tileId"));
    	
    	//Store preferences in DB
    	try {
    		prefs.store();
    	} catch (ValidatorException ve) {
    		logger.error(ve.getLocalizedMessage(), ve);
    		logger.debug("ConfigurationActionTilesImpl: processAction OUT");
    		SessionErrors.add(actionRequest, ValidatorException.class.getName(), ve);
    		return;
    	}
    	
    	logger.debug("ConfigurationActionTilesImpl: processAction OUT");
    }
    
    @Override
	public String render (PortletConfig portletConfig, RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {		
		return "/configurationTiles.jsp";
	}
}