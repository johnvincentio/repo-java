package com.idc.explorer.abc7;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class NodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<NodeItemInfo> m_list = new ArrayList<NodeItemInfo>();
	
	public Iterator getItems() {return m_list.iterator();}
	public void add (NodeItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((NodeItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}

	public NodeItemInfo getNodeItemInfoAt (int index) {
		return m_list.get(index);
	}
	public int getIndexNodeItemInfo (NodeItemInfo child) {
		for (int num = 0; num < m_list.size(); num++) {
			NodeItemInfo item = (NodeItemInfo) m_list.get(num);
			if (item.getPath().equals(child.getPath())) return num;
		}
		return -1;
	}

	public void sort() {
		Collections.sort(m_list, new SortNodeAsc());
	}

	public class SortNodeAsc implements Comparator<NodeItemInfo> {
		public int compare(NodeItemInfo a, NodeItemInfo b) {
			return a.getName().compareTo (b.getName());
		}
	}
}
