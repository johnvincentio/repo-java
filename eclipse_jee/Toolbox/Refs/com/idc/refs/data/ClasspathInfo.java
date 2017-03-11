package com.idc.refs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ClasspathInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<ClasspathItemInfo> m_collection = new ArrayList<ClasspathItemInfo> ();
	public void add (ClasspathItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ClasspathItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<ClasspathItemInfo> {
		public int compare(ClasspathItemInfo a, ClasspathItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((ClasspathItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
