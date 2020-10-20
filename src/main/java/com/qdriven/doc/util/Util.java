package com.qdriven.doc.util;

public class Util {
	public static String removeNLineTabs(String str) {
		if (isStringNotEmpty(str)) {
			str = str.replaceAll("[\\n\\t ]", " ");
			str = str.replaceAll("\\s*\\.\\s*\n\\s*", ". ");
			str = str.replaceAll("\\s*,\\s*", ", ");
			str = str.replaceAll("\\s*;\\s*", "; ");
			return str.trim();
		} else {
			return str;
		}
	}

	public static boolean isInteger(String s) {
		boolean isValidInteger = false;
		try {
			Integer.parseInt(s);

			// s is a valid integer

			isValidInteger = true;
		}

		catch (NumberFormatException ex) {
			// s is not an integer
		}

		return isValidInteger;
	}

	public static boolean isStringEmpty(String str) {
		if (str != null && !str.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isStringNotEmpty(String str) {
		if (str != null && !str.equals("")) {
			return true;
		}
		return false;
	}

}
