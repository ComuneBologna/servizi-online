package it.eng.eli4u.application.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/**
 * Classe per ottenere il context spring da codice legacy
 * @author pluttero
 *
 */
public class SpringApplicationContext implements ApplicationContextAware {
	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
	public static ApplicationContext getContext(){
		return context;
	}


}
