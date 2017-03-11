package com.idc.utils;

import com.idc.rda.Utils;

public class XMLUtils {
	private static final String NL = "\n";

	public static String makeOpenNode (String name) {
		return "<"+cleanKey (name)+">";
	}
	public static String makeOpenNode (String name, String value) {
		if (value == null) value = "";
		return "<"+cleanKey (name)+" "+value+">";
	}
	public static String makeOpenNode (String key, String name, String value) {
		if (value == null) value = "";
		return "<"+cleanKey (key)+" "+makeQuotedEntry (name, value)+">";
	}
	public static String makeClosedNode (String name) {
		return "</"+cleanKey (name)+">";
	}
	public static String makeEntry (String name, String value) {
		if (value == null) value = "";
		if (value.length() < 1) return "<"+cleanKey (name)+"></"+cleanKey (name)+">";
		return "<"+cleanKey (name)+">"+cleanString (value)+"</"+cleanKey (name)+">";
	}
	public static String makeCDataEntry (String name, String value) {
		if (value == null) value = "";
		if (value.length() < 1) return "<"+cleanKey (name)+"></"+cleanKey (name)+">";
		return "<"+cleanKey (name)+">"+makeCdata (cleanString (value))+"</"+cleanKey (name)+">";
	}
	public static String makeEntry (String name, long value) {
		return "<"+cleanKey (name)+">"+value+"</"+cleanKey (name)+">";
	}
	public static String makeEntry (String key, String[] strName, String[] strValue) {
		StringBuffer buf = new StringBuffer();
		buf.append("<").append(cleanKey (key));
		for (int cnt = 0; cnt < strName.length; cnt++) {
			String name = strName[cnt];
			String value = strValue[cnt];
			if (value == null) value = "";
			buf.append(" ").append (makeQuotedEntry (name, value));
		}
		buf.append("/>");
		return buf.toString();
	}
	public static String makeEntryKeyed (String key, String[] strName, String[] strValue) {
		System.out.println(">>> makeEntryKeyed; "+key);
		StringBuffer buf = new StringBuffer();
		buf.append (makeOpenNode (cleanKey (key), strName[0], strValue[0])).append (NL);
		for (int cnt = 1; cnt < strName.length; cnt++) {
			String name = strName[cnt];
			String value = strValue[cnt];
			if (value == null) value = "";
			buf.append (makeEntry (name, value)).append (NL);
		}
		buf.append (makeClosedNode (cleanKey (key))).append (NL);
		System.out.println("<<< makeEntryKeyed; buf :"+buf);
		return buf.toString();
	}
	public static String makeQuotedEntry (String name, String value) {
		return cleanKey (name)+"=\""+value+"\"";
	}
	public static String makeQuotedEntry (String name, long value) {
		return cleanKey (name)+"=\""+value+"\"";
	}
	public static String makeCdata (String name) {
		return "<![CDATA["+cleanString (name)+"]]>";
	}

	public static String makeTabs (int tabs) {
		String str = "";
		for (int i=0; i<tabs; i++) str += "\t";
		return str;
	}
	public static String cleanString (String dirty) {
    	String str = dirty;
    	StringBuffer buf = new StringBuffer();
    	char ch;
    	for (int i=0; i<str.length(); i++) {
    		ch = str.charAt(i);
    		int ich = ch;
    		if (Character.isLetterOrDigit(ch) ||
    				ch == '!' || ch == '@' || ch == '#' || ch == '$' ||
					ch == '%' || ch == '^' || ch == '&' || ch == '*' ||
					ch == '(' || ch == ')' ||
					ch == '_' || ch == '-' || ch == '+' || ch == '=' || 
					ch == '{' || ch == '[' || ch == '}' || ch == ']' ||
					ch == '|' || ch == '\\' ||
					ch == ':' || ch == ';' || ch == '"' || ch == '\'' ||
					ch == '<' || ch == ',' || ch == '>' || ch == '.' ||
					ch == '?' || ch == '/' || ch == ' ' ||
					ich == 10 || ich == 13) {
    			buf.append(ch);
    		}
    		else
    			System.out.println("*** filtered out bad char "+ich+" at position "+i+" from :"+str+":");
    	}
    	return buf.toString();
    }
	private static String cleanKey (String key) {
		return Utils.replace (key, " ", "_");
	}
}
