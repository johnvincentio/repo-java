package com.idc.refs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class NodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<NodeItemInfo> m_collection;
	public NodeInfo () {m_collection = new ArrayList<NodeItemInfo> ();}
	public void add (NodeItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<NodeItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public NodeItemInfo getItem (String name) {
		Iterator<NodeItemInfo> iter = getItems();
		while (iter.hasNext()) {
			NodeItemInfo item = (NodeItemInfo) iter.next();
			if (item == null) continue;
			if (item.getName().equals(name)) return item;
		}
		return null;
	}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<NodeItemInfo> {
		public int compare(NodeItemInfo a, NodeItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((NodeItemInfo) m_collection.get(i)).toString()).append("\n");
		return "("+buf.toString()+")";
	}
}
