package com.idc.refs.buckets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class BucketInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<BucketItemInfo> m_collection = new ArrayList<BucketItemInfo> ();

	public void add (BucketItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<BucketItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public BucketItemInfo getItem (String name) {
		Iterator<BucketItemInfo> iter = getItems();
		while (iter.hasNext()) {
			BucketItemInfo item = (BucketItemInfo) iter.next();
			if (item == null) continue;
			if (item.getName().equals(name)) return item;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((BucketItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
