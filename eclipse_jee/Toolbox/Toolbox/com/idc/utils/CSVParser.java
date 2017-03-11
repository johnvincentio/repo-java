
package com.idc.utils;

import java.util.ArrayList;

public class CSVParser {
	
	public static String[] parser (String line) {
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buf = new StringBuffer(line.trim());
		buf.append (',');
		
		StringBuffer obuf = new StringBuffer();
		boolean bOpenQuote = false;
		for (int cpos=0; cpos<buf.length(); cpos++) {
			if (buf.charAt(cpos) == ',') {
				if (! bOpenQuote) {
					list.add (obuf.toString());
					obuf = new StringBuffer();
				}
				else
					obuf.append (buf.charAt(cpos));
				continue;
			}
			if (buf.charAt(cpos) == '\'') {
				if (bOpenQuote)
					obuf.append ("''");
				else
					obuf.append ("'");
				continue;
			}
			if (buf.charAt(cpos) == '"') {
				if (bOpenQuote) {
					if (buf.charAt(cpos+1) == '"')
						obuf.append(buf.charAt(cpos++));
					else
						bOpenQuote = false;
				}
				else
					bOpenQuote = true;
				continue;
			}
			obuf.append(buf.charAt(cpos));
		}
		int size = list.size();
		String[] arrStr = new String[size];
		for (int x=0; x<size; x++) arrStr[x] = (String) list.get(x);
		return arrStr;
	}
	public static String cleanString (String str) {
		if (str == null || str.length() < 1) return "";
		StringBuffer buf = new StringBuffer(str.trim());
		if (buf.charAt(0) == '"') buf.deleteCharAt(0);
		if (buf.charAt(buf.length()-1) == '"') buf.deleteCharAt(buf.length()-1);
		return buf.toString();
	}
}
