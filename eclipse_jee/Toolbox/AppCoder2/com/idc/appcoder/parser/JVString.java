package com.idc.appcoder.parser;

/**
 * @author John Vincent
 *
 */

public class JVString {

	public static String replace (String input, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer();
    
		while ((e = input.indexOf(pattern, s)) >= 0) {
			buf.append(input.substring(s, e));
			buf.append(replace);
			s = e + pattern.length();
		}
		buf.append(input.substring(s));
		return buf.toString();
	}
	public static String removeLastIgnoreCase (String pattern, String remove) {
		String tmp = pattern.trim().toLowerCase();
		int p1 = tmp.lastIndexOf(remove);
		int last = tmp.length();
		if (p1 > -1 && p1 < last) last = p1;
		return pattern.substring (0,last);
	}

	public static String insert (String input, int nNumber) {
		StringBuffer buf = new StringBuffer();
		buf.append(nNumber).append(input);
		return buf.toString();
	}

	public static String insert (String input, String str) {
		StringBuffer buf = new StringBuffer();
		buf.append(str).append(input);
		return buf.toString();
    }

    public static String initUpper (String input) {
		StringBuffer buf = new StringBuffer();
		if (input.length() < 1) return input;
		buf.append(input.substring(0, 1).toUpperCase());
		buf.append(input.substring(1));
		return buf.toString();
    }

    public static String initLower (String input) {
		StringBuffer buf = new StringBuffer();
		if (input.length() < 1) return input;
		buf.append(input.substring(0, 1).toLowerCase());
		buf.append(input.substring(1));
		return buf.toString();
    }

    public void dump (String input, String msg) {
    	System.out.println("--- dump String; "+msg+" :"+input+":");
		StringBuffer buf = new StringBuffer(input);
		char chr;
		for (int i=0; i<buf.length(); i++) {
			chr = buf.charAt(i);
			int iv = chr;
			System.out.println("(i) "+i+" :"+chr+": :"+iv+":");
		}
    }

    public static String removeNull (String input) {
//    	dump("before");
		StringBuffer ibuf = new StringBuffer(input);
		StringBuffer obuf = new StringBuffer();
		char chr;
		for (int i=0; i<ibuf.length(); i++) {
			chr = ibuf.charAt(i);
			int iv = chr;
			if (iv > 0) obuf.append(chr);
		}
		return obuf.toString();
    }
   
    public static boolean isEmpty (String value) {
    	if (value == null) return true;
    	if ("".equals(value.trim())) return true;
    	return false;
    }

    public static String getCapitalized (String str) {
    	char ch;
    	char prevCh = '.';        
    	int max = str.length();
    	char data[] = new char[max];

    	for (int i = 0;  i < max;  i++) {
    		ch = str.charAt(i);
    		if (Character.isLetter(ch)  &&  ! Character.isLetter(prevCh)) {
    			data[i] = Character.toUpperCase(ch);                
    		} else
    			data[i] = Character.toLowerCase(ch);           
   			prevCh = ch;              // prevCh for next iteration is ch.
   		}
   		str = new String(data);
   		return str;
   	}
}
