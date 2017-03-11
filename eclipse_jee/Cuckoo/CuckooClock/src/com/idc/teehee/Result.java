package com.idc.teehee;

public class Result {
	private int m_type = 0;
	private int m_hours = -1;
	public void setNothing() {m_type = 0;}
	public void setQuarterHour() {m_type = 1;}
	public void setHalfHour() {m_type = 2;}
	public void setHour(int hours) {
		m_type = 3;
		m_hours = hours;
	}
	public boolean isNothing() {return m_type == 0;}
	public boolean isQuarterHour() {return m_type == 1;}
	public boolean isHalfHour() {return m_type == 2;}
	public boolean isHour() {return m_type == 3;}
	public int getHours() {return m_hours;}
}
