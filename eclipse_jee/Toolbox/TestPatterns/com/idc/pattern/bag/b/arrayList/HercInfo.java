package com.idc.pattern.bag.b.arrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.pattern.bag.b.HercItemInfo;

public class HercInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<HercItemInfo> m_collection;

	public HercInfo () {
		m_collection = new ArrayList<HercItemInfo> ();
	}
	public HercInfo (int capacity) {
		m_collection = new ArrayList<HercItemInfo> (capacity);
	}

	public Iterator<HercItemInfo> getItems() {return m_collection.iterator();}
	public void add (HercItemInfo item) {if (item != null) m_collection.add(item);}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((HercItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
