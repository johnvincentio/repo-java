package com.idc.cycles;

import java.util.ArrayList;

public class Allrefs {
	private ArrayList<Ref> m_list = new ArrayList<Ref>();

	private int m_nPos = 0;
	public void add(Ref ref) {m_list.add(ref);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public void reset() {m_nPos = 0;}
	public Ref getRef() {return ((Ref) m_list.get(m_nPos));}
	public void show() {
		System.out.println("Showing Allrefs\n");
		for (int i = 0; i < m_list.size(); i++) {
			Ref ref = (Ref) m_list.get(i);
			ref.show();
		}
		System.out.println("\nDone");
	}
}
