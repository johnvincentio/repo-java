
package com.idc.countdown;

public class Numbers {
	private int[] m_naNumbers = new int[Constants.MAX_NUMBERS];
	private int m_nTarget;
	private int m_nPos = 0;
	public Numbers() {}
	public void set (int num) {m_naNumbers[m_nPos++] = num;}
	public int get (int pos) {return m_naNumbers[pos];}
	public void setTarget (int num) {m_nTarget = num;}
	public int getTarget() {return m_nTarget;}
	public String show() {
		StringBuffer buf = new StringBuffer();
		buf.append("("+m_nTarget+") ");
		for (int i=0; i<Constants.MAX_NUMBERS; i++) {
			buf.append(m_naNumbers[i]);
			if (i < Constants.MAX_NUMBERS - 1)
				buf.append(",");
		}
		return buf.toString();
	}
}