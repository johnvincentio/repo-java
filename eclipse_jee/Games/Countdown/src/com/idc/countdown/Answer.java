
package com.idc.countdown;

public class Answer {
	private int[] m_anAnswer = new int[Constants.MAX_ARRAY];
	private int m_nTotal;
	public Answer() {}
	public void set (Answer ans) {
		for (int i=0; i<Constants.MAX_ARRAY; i++) m_anAnswer[i] = ans.get(i);
		m_nTotal = ans.getTotal();
	}
	public void set (int pos, int value) {m_anAnswer[pos] = value;}
	public int get (int pos) {return m_anAnswer[pos];}
	public void setTotal (int total) {m_nTotal = total;}
	public int getTotal() {return m_nTotal;}
	public String show() {
		StringBuffer buf = new StringBuffer();
		buf.append("("+m_nTotal+"); ");
		buf.append("((((("); buf.append(m_anAnswer[0]).append(Utils.getOperator(m_anAnswer[1])).append(m_anAnswer[2]).append(")");
		buf.append(Utils.getOperator(m_anAnswer[3])).append(m_anAnswer[4]).append(")");
		buf.append(Utils.getOperator(m_anAnswer[5])).append(m_anAnswer[6]).append(")");
		buf.append(Utils.getOperator(m_anAnswer[7])).append(m_anAnswer[8]).append(")");
		buf.append(Utils.getOperator(m_anAnswer[9])).append(m_anAnswer[10]).append(")");
		return buf.toString();
	}
}
