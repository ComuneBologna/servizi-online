package it.eng.eli4u.agenda.services.portal;

import java.util.Locale;

import javax.portlet.PortletRequest;

public class LiferayCoboPortalService extends LiferayPortalService {
	@Override
	public Locale getLinguaPortale(PortletRequest request) {
		return Locale.ITALIAN;
	}
}
