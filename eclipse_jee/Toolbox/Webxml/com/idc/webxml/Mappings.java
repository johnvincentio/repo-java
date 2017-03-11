package com.idc.webxml;

import java.util.ArrayList;

public class Mappings {
	ArrayList<Mapping> m_list = new ArrayList<Mapping>();
	int m_nPos = 0;
	public void add(Mapping item) {m_list.add(item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public Mapping getMapping() {return (Mapping) m_list.get(m_nPos);}
	public void reset() {m_nPos = 0;}
	public void show() {
		if (! isEmpty()) {
			System.out.println("Showing all Mappings");
			for (int i=0; i<m_list.size(); i++) {
				Mapping item = (Mapping) m_list.get(i);
				item.show();
			}
		}
	}
}
