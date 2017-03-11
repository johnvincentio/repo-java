package com.idc.cycles;

import java.util.ArrayList;

public class Ref {
	private ArrayList<String> m_list;
	private int m_nPos = 0;
	public Ref() {m_list = new ArrayList<String>();}
	public Ref(Ref ref) {m_list = new ArrayList<String>(ref.getList());}
	public void add(String ref) {m_list.add(ref);}
	private ArrayList<String> getList() {return m_list;}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public void reset() {m_nPos = 0;}
	public String getRef() {return ((String) m_list.get(m_nPos));}

	public void show() {
		System.out.println("\nRef ");
		for (int i = 0; i < m_list.size(); i++) {
			String ref = (String) m_list.get(i);
			if (i > 0) System.out.print(",");
			System.out.print(ref);
		}
		System.out.println("\nRef complete");
	}
}
