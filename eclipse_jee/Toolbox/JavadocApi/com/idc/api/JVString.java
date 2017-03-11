
package com.idc.api;

public class JVString {
	public static String replace(String source, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer();
    
		while ((e = source.indexOf(pattern, s)) >= 0) {
			buf.append(source.substring(s, e));
			buf.append(replace);
			s = e + pattern.length();
		}
		buf.append(source.substring(s));
		return buf.toString();
	}
}
