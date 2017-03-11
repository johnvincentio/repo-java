package com.idc.utils;

public class JVString {

	public static String replace (String input, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer ();

		while ((e = input.indexOf (pattern, s)) >= 0) {
			buf.append (input.substring (s, e));
			buf.append (replace);
			s = e + pattern.length ();
		}
		buf.append (input.substring (s));
		return buf.toString ();
	}

	public static String removeLastIgnoreCase (String pattern, String remove) {
		String tmp = pattern.trim ().toLowerCase ();
		int p1 = tmp.lastIndexOf (remove);
		int last = tmp.length ();
		if (p1 > -1 && p1 < last) last = p1;
		return pattern.substring (0, last);
	}

	public static String insert (String input, int nNumber) {
		StringBuffer buf = new StringBuffer ();
		buf.append (nNumber).append (input);
		return buf.toString ();
	}

	public static String insert (String input, String str) {
		StringBuffer buf = new StringBuffer ();
		buf.append (str).append (input);
		return buf.toString ();
	}

	public static String insert (int nNumber, String str) {
		StringBuffer buf = new StringBuffer ();
		buf.append (nNumber).append (nNumber);
		return buf.toString ();
	}

	public static String initUpper (String input) {
		StringBuffer buf = new StringBuffer ();
		buf.append (input.substring (0, 1).toUpperCase ());
		buf.append (input.substring (1));
		return buf.toString ();
	}

	public static String initLower (String input) {
		StringBuffer buf = new StringBuffer ();
		if (input.length () < 1) return input;
		buf.append (input.substring (0, 1).toLowerCase ());
		buf.append (input.substring (1));
		return buf.toString ();
	}

	public static void dump (String str) {
		StringBuffer buf = new StringBuffer (str);
		char chr;
		for (int i = 0; i < buf.length (); i++) {
			chr = buf.charAt (i);
			int iv = chr;
			System.out.println ("(i) " + i + " :" + chr + ": :" + iv + ":");
		}
	}

	public static String removeNull (String input) {
//    	dump("before");
		StringBuffer ibuf = new StringBuffer (input);
		StringBuffer obuf = new StringBuffer ();
		char chr;
		for (int i = 0; i < ibuf.length (); i++) {
			chr = ibuf.charAt (i);
			int iv = chr;
			if (iv > 0) obuf.append (chr);
		}
		return obuf.toString ();
	}

	public static boolean isEmpty (String value) {
		if (value == null) return true;
		if ("".equals (value.trim ())) return true;
		return false;
	}

	public static String getCapitalized (String str) {
		char ch;
		char prevCh = '.';
		int max = str.length ();
		char data[] = new char[max];

		for (int i = 0; i < max; i++) {
			ch = str.charAt (i);
			if (Character.isLetter (ch) && !Character.isLetter (prevCh)) {
				data[i] = Character.toUpperCase (ch);
			}
			else
				data[i] = Character.toLowerCase (ch);
			prevCh = ch; // prevCh for next iteration is ch.
		}
		str = new String (data);
		return str;
	}

	public static String cleanString (String input) {
		String str = input;
		StringBuffer buf = new StringBuffer ();
		char ch;
		for (int i = 0; i < str.length (); i++) {
			ch = str.charAt (i);
			if (Character.isLetterOrDigit (ch) || ch == '!' || ch == '@' || ch == '#' || ch == '$' || ch == '%' || ch == '^' || ch == '&'
					|| ch == '*' || ch == '(' || ch == ')' || ch == '_' || ch == '-' || ch == '+' || ch == '=' || ch == '{' || ch == '[' || ch == '}'
					|| ch == ']' || ch == '|' || ch == '\\' || ch == ':' || ch == ';' || ch == '"' || ch == '\'' || ch == '<' || ch == ','
					|| ch == '>' || ch == '.' || ch == '?' || ch == '/' || ch == ' ') {
				buf.append (ch);
			}
			else
				System.out.println ("*** filtered out bad char from :" + str + ":");
		}
		return buf.toString ();
	}

	public static byte[] hexStringToByteArray (String s) {
		int len = s.length ();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit (s.charAt (i), 16) << 4) + Character.digit (s.charAt (i + 1), 16));
		}
		return data;
	}

	/**
	 * Convenience method for use by Runtime.getRuntime().exec().
	 * Note that the first parameter must be split into
	 * strings and each added to the string array.
	 * 
	 * @param str1	Command, for example open -n /Applications/TextEdit.app
	 * @param str2  Parameter, for example a file.
	 * @return		String[] ready for use by Runtime.getRuntime().exec()
	 */
	public static String[] createStringArrayForExecCmd (String str1, String str2) {
		String[] parts = str1.split(" ");
		String[] strArray = new String[parts.length + 1];
		int n = 0;
		for (String s : parts) {
			strArray[n++] = s;
		}
		strArray[n] = str2;
		return strArray;
	}
}
