package com.idc.five.utils;

public class Utilities {

	public static String leadingSpacesPad (String str, int width) {
		int length = str.length();
		for (int i = 0; i < width - length; i++)
			str = " " + str;
		return str;
	}

	public static String leadingSpacesPad (int number, int width) {
		return leadingSpacesPad (String.valueOf(number), width);
	}

	public static String repeatString (String str, int repeat) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < repeat; i++) buf.append (str);
		return buf.toString();
	}
}
