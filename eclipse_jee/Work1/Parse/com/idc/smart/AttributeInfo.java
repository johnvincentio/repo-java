package com.idc.smart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class AttributeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<AttributeItemInfo> m_list = new ArrayList<AttributeItemInfo>();

	public Iterator getItems() {return m_list.iterator();}
	public void add (AttributeItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isFound (String name, String value) {
		for (Iterator iter = getItems(); iter.hasNext(); ) {
			AttributeItemInfo item = (AttributeItemInfo) iter.next();
			if (item.getName().equals(name) && item.getValue().equals(value)) return true;
		}
		return false;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((AttributeItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
	public void sort() {
		Collections.sort(m_list, new SortAttributeAsc());
	}

	public class SortAttributeAsc implements Comparator<AttributeItemInfo> {
		public int compare(AttributeItemInfo a, AttributeItemInfo b) {
			int cmp = a.getName().compareTo (b.getName());
			if (cmp != 0) return cmp; 
			return a.getValue().compareTo (b.getValue());
		}
	}
}
