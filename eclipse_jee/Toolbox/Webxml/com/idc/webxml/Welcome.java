package com.idc.webxml;

import java.util.ArrayList;

public class Welcome {
	ArrayList<String> m_list = new ArrayList<String>();
	int m_nPos = 0;
	public void add(String item) {m_list.add(item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public String getFile() {return (String) m_list.get(m_nPos);}
	public void reset() {m_nPos = 0;}
	public void show() {
		if (! isEmpty()) {
			System.out.println("Showing all Welcome files");
			for (int i=0; i<m_list.size(); i++) {
				String item = (String) m_list.get(i);
				System.out.println("Welcome file "+item);
			}
		}
	}
}
