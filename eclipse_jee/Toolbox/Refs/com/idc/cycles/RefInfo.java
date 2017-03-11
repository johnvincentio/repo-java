package com.idc.cycles;

import java.util.ArrayList;
import java.util.Iterator;

public class RefInfo {
	private ArrayList<RefItemInfo> m_list = new ArrayList<RefItemInfo>();

	public void add (RefItemInfo refItemInfo) {m_list.add (refItemInfo);}
	public Iterator<RefItemInfo> getItems() {return m_list.iterator();}

	public void show() {
		System.out.println("Showing Allrefs\n");
		for (int i = 0; i < m_list.size(); i++) {
			RefItemInfo refItemInfo = (RefItemInfo) m_list.get(i);
			refItemInfo.show();
		}
		System.out.println("\nDone");
	}
}
