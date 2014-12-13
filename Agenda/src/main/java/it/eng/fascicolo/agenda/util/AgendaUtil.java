package it.eng.fascicolo.agenda.util;

public class AgendaUtil {
	
	public static int strToInt(String s) {
		int val = 0;
		try {
			val = Integer.parseInt(s);
		} catch (Exception ex) {
			val = 0;
		}
		return val;
	}
	
	public static String intToStr(int val) {
		return "" + val;
	}
	
	public static String getStringNotNull(String value) {
		String res = "";
		if (value != null) {
			res = value;
		}
		return res;
	}
	
}