
package com.idc.appender;

import java.util.regex.*;

public class SplitString {
	private String[] m_splitStrings;
	private int m_nPos = 0;

	public void splitBuffer(String str, String div) {
		Pattern pattern = Pattern.compile(div);
		m_splitStrings = pattern.split(str);
	}
	public String getNext() {return m_splitStrings[m_nPos++];}
	public int getNextInt() {
		String strMsg = m_splitStrings[m_nPos++];
		Integer intNumber = new Integer(strMsg);
		return intNumber.intValue();
	}
	public int length() {return m_splitStrings.length;}
	public boolean hasNext() {return m_nPos < m_splitStrings.length;}
}
