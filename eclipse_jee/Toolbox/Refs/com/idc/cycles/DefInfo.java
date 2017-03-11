package com.idc.cycles;

import java.util.ArrayList;
import java.util.Iterator;

public class DefInfo {
	private ArrayList<DefItemInfo> m_list = new ArrayList<DefItemInfo>();
	public void add (DefItemInfo defItemInfo) {m_list.add (defItemInfo);}
	public Iterator<DefItemInfo> getItems() {return m_list.iterator();}

	public DefItemInfo getDef (String name) {
		Iterator<DefItemInfo> iter = getItems();
		while (iter.hasNext()) {
			DefItemInfo current = (DefItemInfo) iter.next();
			if (current.getName().equals(name)) return current;
		}
		return null;
	}

	public void show() {
		System.out.println("Showing Alldefs\n");
		for (int i = 0; i < m_list.size(); i++) {
			DefItemInfo defItemInfo = (DefItemInfo) m_list.get(i);
			defItemInfo.show();
		}
		System.out.println("\nDone");
	}
}
