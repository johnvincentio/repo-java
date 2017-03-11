package com.idc.refs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ModulemapsInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<ModulemapsItemInfo> m_collection = new ArrayList<ModulemapsItemInfo> ();
	public void add (ModulemapsItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ModulemapsItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<ModulemapsItemInfo> {
		public int compare(ModulemapsItemInfo a, ModulemapsItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((ModulemapsItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
