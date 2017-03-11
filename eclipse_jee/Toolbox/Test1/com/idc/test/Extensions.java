package com.idc.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

public class Extensions {
	private ArrayList<Extension> m_list = new ArrayList<Extension>();
	public void add (Extension item) {m_list.add(item);}
	public Iterator<Extension> getItems() {return m_list.iterator();}
	public int getSize() {return m_list.size();}
	public boolean isMatchAndChecked (File file) {
		String str = file.getName();
		Iterator<Extension> iter = getItems();
		Extension item;
		while (iter.hasNext()) {
			item = (Extension) iter.next();
			if (item.isActive() && item.isMatch(str)) return true;
		}
		return false;
	}
}
