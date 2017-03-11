package com.idc.webxml;

public class JVString {
	private String m_string;
	public JVString(String str) {m_string = str;}

	public void replace(String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer buf = new StringBuffer();

		while ((e = m_string.indexOf(pattern, s)) >= 0) {
			buf.append(m_string.substring(s, e));
			buf.append(replace);
			s = e + pattern.length();
		}
		buf.append(m_string.substring(s));
		m_string = buf.toString();
	}
	public void insert(int nNumber) {
		StringBuffer buf = new StringBuffer();
		buf.append(nNumber).append(m_string);
		m_string = buf.toString();
	}
	public void insert(String str) {
		StringBuffer buf = new StringBuffer();
		buf.append(str).append(m_string);
		m_string = buf.toString();
	}
	public void initUpper() {
		StringBuffer buf = new StringBuffer();
		buf.append(m_string.substring(0, 1).toUpperCase());
		buf.append(m_string.substring(1));
		m_string = buf.toString();
	}
	public String getString() {return m_string;}
	public void dump(String msg) {
//		System.out.println("--- dump String; "+msg+" :"+m_string+":");
		StringBuffer buf = new StringBuffer(m_string);
		char chr;
		for (int i=0; i<buf.length(); i++) {
			chr = buf.charAt(i);
			int iv = chr;
			System.out.println("(i) "+i+" :"+chr+": :"+iv+":");
		}
	}
	public void removeNull() {
//		dump("before");
		StringBuffer ibuf = new StringBuffer(m_string);
		StringBuffer obuf = new StringBuffer();
		char chr;
		for (int i=0; i<ibuf.length(); i++) {
			chr = ibuf.charAt(i);
			int iv = chr;
			if (iv > 0) obuf.append(chr);
		}
		m_string = obuf.toString();
//		dump("after");
	}
	public boolean isEmpty(String value) {
		if (value == null) return true;
		if ("".equals(value.trim())) return true;
		return false;
	}

	public String getCapitalized(String str) {
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
				prevCh = ch;         // prevCh for next iteration is ch.
			}
			str = new String(data);
			return str;
		}
	}
