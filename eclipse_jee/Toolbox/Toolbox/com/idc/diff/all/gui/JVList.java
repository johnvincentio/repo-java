package com.idc.diff.all.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JVList {
	private List<String> m_list = new ArrayList<String>();
	public void add(String item) {m_list.add(item);}
	public Iterator<String> getItems() {return m_list.iterator();}
	public boolean isInlist(String str) {
		Iterator<String> iter = getItems();
		while (iter.hasNext()) {
			String tmp = (String) iter.next();
			if (str.equals(tmp)) return true;
		}
		return false;
	}
}
