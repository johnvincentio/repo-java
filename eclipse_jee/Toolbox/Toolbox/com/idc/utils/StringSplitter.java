package com.idc.utils;

import java.util.regex.*;

public class StringSplitter {

	private String[] m_splitStrings;
	private int m_nextSplit = 0;

	public StringSplitter (String strbuf, String div) {
		Pattern pattern = Pattern.compile(div);
		m_splitStrings = pattern.split(strbuf);
	}
	public int length() {return m_splitStrings.length;}
	public boolean hasNext() {return hasNext(1);}
	public boolean hasNext(int items) {
		int num = m_splitStrings.length - m_nextSplit;
		if (num < items) return false;
		return true;
	}
	public String getNext() {return m_splitStrings[m_nextSplit++];}
	public int getNextInt() {
		String strMsg = m_splitStrings[m_nextSplit++];
		Integer intNumber = new Integer(strMsg);
		return intNumber.intValue();
	}
}
