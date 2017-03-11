package com.idc.refs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ManifestmfInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<ManifestmfItemInfo> m_collection = new ArrayList<ManifestmfItemInfo> ();
	public void add (ManifestmfItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ManifestmfItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<ManifestmfItemInfo> {
		public int compare(ManifestmfItemInfo a, ManifestmfItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((ManifestmfItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
