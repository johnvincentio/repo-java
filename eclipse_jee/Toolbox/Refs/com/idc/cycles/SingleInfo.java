package com.idc.cycles;

import java.util.ArrayList;

public class SingleInfo {
	private ArrayList<String> m_list = new ArrayList<String>();

	public void add (String item) {
//		System.out.println("---Singles::add");
		if (! isInList (item)) {
//			System.out.println("Actually adding :"+item+":");
			m_list.add (item);
		}
	}
	public ArrayList<String> getList() {return m_list;}
	public boolean isInList (String name) {
		for (int i = 0; i < m_list.size(); i++) {
			String item = (String) m_list.get(i);
			if (item.equals(name)) return true;
		}
		return false;
	}

	public void show() {
		System.out.println("\nSingles ");
		for (int i=0; i<m_list.size(); i++) {
			String item = (String) m_list.get(i);
			System.out.println(item);
		}
		System.out.println("\nSingles complete");
	}
}
