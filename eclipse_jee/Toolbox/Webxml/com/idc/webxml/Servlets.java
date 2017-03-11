package com.idc.webxml;

import java.util.ArrayList;

public class Servlets {
	ArrayList<Servlet> m_list = new ArrayList<Servlet>();
	int m_nPos = 0;
	public void add(Servlet item) {m_list.add(item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public Servlet getServlet() {return (Servlet) m_list.get(m_nPos);}
	public void reset() {m_nPos = 0;}
	public void show() {
		if (! isEmpty()) {
			System.out.println("Showing all Servlets");
			for (int i=0; i<m_list.size(); i++) {
				Servlet item = (Servlet) m_list.get(i);
				item.show();
			}
		}
	}
}
