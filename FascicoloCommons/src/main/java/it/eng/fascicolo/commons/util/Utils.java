package it.eng.fascicolo.commons.util;

import static java.lang.System.out;
import it.eng.fascicolo.commons.profilazione.Persona;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class Utils {

	static Logger logger = LoggerFactory.getLogger(ImageMixUtil.class);
	
	public static String dateToString(Date date) {
		String result = null;
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(Persona.FORMATO_DATA_SIMPLEDATEFORMAT);
			result = sdf.format(date);
		}
		return result;
	}

	public static Date stringToDate(String date) throws Exception {
		Date result = null;
		if (!StringUtils.isEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat(Persona.FORMATO_DATA_SIMPLEDATEFORMAT);
			try {
				result = sdf.parse(date);
			} catch (ParseException e) {
				if (date!=null){
					logger.error(e.getMessage(), e);
					throw new Exception("Errore nel formato della data: " + date);
				}
			}
		}
		return result;
	}

	public static String randomPastDate(){
		int days = (int)(100*365* (Math.random()));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -days);
		SimpleDateFormat sdf = new SimpleDateFormat(Persona.FORMATO_DATA_SIMPLEDATEFORMAT);
		String format = sdf.format(cal.getTime());
		return format;
	}
	
	public static Date randomPastDateD(){
		int days = (int)(100*365* (Math.random()));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -days);
		SimpleDateFormat sdf = new SimpleDateFormat(Persona.FORMATO_DATA_SIMPLEDATEFORMAT);
		String format = sdf.format(cal.getTime());
		Date dataResult = null;
		try {
			dataResult = stringToDate(format);
		} catch (Exception e){	}
		return dataResult; 
	}
}
