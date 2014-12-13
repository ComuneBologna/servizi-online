package it.eng.eli4u.imieidati.utils;

public class EmptyHTMLChecker {

	// Generic check for an empty HTML data
	public static boolean isEmptyData(String html) {
		String s = html == null ? "" : html;
		
		s = s.replace(" ", "");
		s = s.replace("\n", "");
		s = s.replace("\r", "");
		s = s.replace("\t", "");
		
		// remove nested comments, if any
		if (s.length() > 0) {
			int posLT = 0;
			int posGT = 0;
	
			do {
				posLT = s.indexOf("<!--");
				// exit if no more comment left
				if (posLT < 0) {
					break;
				}
				posGT = s.indexOf("-->");
				// exit if no more comment left
				if (posGT < 0) {
					break;
				}
				s = s.substring(0, posLT) + s.substring(posGT + 1);
			} while (posGT < s.length());
		}
		
		// remove well-formed tags
		if (s.length() > 0) {
			int posLT = 0;
			int posGT = 0;
	
			do {
				posLT = s.indexOf('<');
				// exit if non-empty data found
				if (posLT != 0) {
					return false;
				}
				posGT = s.indexOf('>');
				// exit if non-empty invalid html found
				if (posGT < 0) {
					return false;
				}
				s = s.substring(posGT + 1);
			} while (posGT < s.length());
		}
		
		return true;
	}
	

}
