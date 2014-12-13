package it.eng.eli4u.service.velocity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FormatUtil {

	public static String formatDate(String strDate, Locale locale) {
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (Exception e) {
			return ret;
		}
		
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
		ret = df.format(date);
		
		return ret;
	}
	
	
	public static String formatCodiceFiscale(String cf) {
		String ret = "";
		if (cf != null) {
			cf = cf.replace(" ", "");
			if (cf.length() >= 16) {
				ret = cf.substring(0, 3) + " ";
				ret += cf.substring(3, 6) + " ";
				ret += cf.substring(6, 11) + " ";
				ret += cf.substring(11, 15) + " ";
				ret += cf.substring(15, 16);
			}
		}
		return ret;
	}
	
	public static String getCategoriaCatastale(String valore) {
		String ret = "";
		if (valore != null && valore.length() > 0) {
			String tipo = valore.substring(0, 1);
			int value = -1;
			try {
				value = Integer.parseInt(valore.substring(1));
			} catch (Exception ex) { value = -2;}
			if (value > 0) {
				ret = tipo + "/" + value;
			} else {
				ret = valore;
			}
		}
		return ret;
	}
	
	
}
