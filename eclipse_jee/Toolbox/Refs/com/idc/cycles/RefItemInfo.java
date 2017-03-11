package com.idc.cycles;

import java.util.ArrayList;
import java.util.Iterator;

public class RefItemInfo {
	private ArrayList<String> m_list;
	public RefItemInfo() {m_list = new ArrayList<String>();}
	public RefItemInfo(RefItemInfo ref) {m_list = new ArrayList<String> (ref.getList());}
	public void add(String ref) {m_list.add(ref);}
	private ArrayList<String> getList() {return m_list;}
	public Iterator<String> getItems() {return m_list.iterator();}

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
