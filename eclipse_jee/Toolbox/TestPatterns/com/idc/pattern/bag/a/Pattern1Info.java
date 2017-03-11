package com.idc.pattern.bag.a;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class Pattern1Info implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<DddItemInfo> m_collection;

	public Pattern1Info () {
		m_collection = new ArrayList<DddItemInfo> ();
	}
	public Pattern1Info (int capacity) {
		m_collection = new ArrayList<DddItemInfo> (capacity);
	}

	public Iterator<DddItemInfo> getItems() {return m_collection.iterator();}
	public void add (DddItemInfo item) {if (item != null) m_collection.add(item);}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((DddItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
