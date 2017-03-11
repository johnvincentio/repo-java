
package com.idc.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

public class DirInfo {
	private ArrayList<DirItemInfo> m_list = new ArrayList<DirItemInfo>();
	public void add (DirItemInfo item) {m_list.add (item);}
	public Iterator<DirItemInfo> getItems() {return m_list.iterator();}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++){
			buf.append(((DirItemInfo) m_list.get(i)).toString());
		}
		return "("+buf.toString()+")";
	}
	public void sort() {
		Collections.sort(m_list, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<DirItemInfo> {
		public int compare(DirItemInfo a, DirItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}
}
