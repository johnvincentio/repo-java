package com.idc.cycles;

import java.util.ArrayList;
import java.util.Iterator;

public class DefItemInfo {
	private String m_name;
	private ArrayList<String> m_list = new ArrayList<String>();

	public DefItemInfo (String name) {m_name = name;}
	public String getName() {return m_name;}
	public void add (String ref) {m_list.add (ref);}
	public void add (ArrayList<String> list) {m_list = list;}
	public Iterator<String> getItems() {return m_list.iterator();}

	public void show() {
		System.out.println("\nModule " + m_name);
		for (int i = 0; i < m_list.size(); i++) {
			String ref = (String) m_list.get(i);
			if (i > 0) System.out.print(",");
			System.out.print(ref);
		}
		System.out.println("\nModule " + m_name + " complete");
	}
}
