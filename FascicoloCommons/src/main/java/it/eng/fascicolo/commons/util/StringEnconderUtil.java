package it.eng.fascicolo.commons.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringEnconderUtil {

	static Logger logger = LoggerFactory.getLogger(StringEnconderUtil.class);
	
	public static final String UTF8 = "UTF-8";
	public static final String ISO88591 = "iso-8859-1";
	
	public static String encodeStringUtf8(String text){
		return new String (Charset.forName("UTF-8").encode(text).array());
	}
	
	public static String getStringUrlEncoded(String text) {
		try {
			return URLEncoder.encode(text, UTF8);
		} catch (Exception e) {
			logger.error("non è possibile fare encode del testo: "+text,e);
			return text;
		}
	}
	
	
	public static String encodeString(String text, String format){
		String textEncoded = text;
		
		try {
			byte ptext[] = text.getBytes();
			textEncoded = new String(ptext, format);
		} catch (UnsupportedEncodingException uee) {
			logger.debug("problema nella conversione in UTF-8 della stringa:  "+text, uee);
		}
		return textEncoded;
	}
	
	public static String encodeStringIsoToUtf8(String text){
		String encoded = text;
		try {
			encoded = new String (text.getBytes (ISO88591), UTF8);
		} catch (UnsupportedEncodingException uee) {
			logger.debug("problema nella conversione da "+ISO88591+" in "+UTF8+" della stringa body "+text,uee);
		}
		return encoded;
	}
}