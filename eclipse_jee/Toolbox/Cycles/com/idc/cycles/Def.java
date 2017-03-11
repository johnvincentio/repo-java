package com.idc.cycles;

import java.util.ArrayList;

public class Def {
	private String m_name;

	private ArrayList<String> m_list;

	private int m_nPos = 0;
	public Def (String name) {
		m_name = name;
		m_list = new ArrayList<String>();
	}
	public String getName() {return m_name;}
	public void add(String ref) {m_list.add(ref);}
	public void add(ArrayList<String> list) {m_list = list;}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public void reset() {m_nPos = 0;}
	public String getRef() {return ((String) m_list.get(m_nPos));}

	public void show() {
		System.out.println("\nModule " + m_name);
		for (int i = 0; i < m_list.size(); i++) {
			String ref = (String) m_list.get(i);
			if (i > 0)
				System.out.print(",");
			System.out.print(ref);
		}
		System.out.println("\nModule " + m_name + " complete");
	}
}
/*
	private String getRef(String name) {
		String current;
		reset();
		while (hasNext()) {
			current = (String) m_list.get(m_nPos);
			if (current.equals(name)) {
				return current;
			}
			getNext();
		}
		return null;
	}
*/