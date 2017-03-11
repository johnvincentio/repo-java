package com.idc.cycles;

import java.util.ArrayList;
import java.util.Iterator;

public class NodeInfo {
	private ArrayList<NodeItemInfo> m_list = new ArrayList<NodeItemInfo>();
	public void add (NodeItemInfo node) {m_list.add (node);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public Iterator<NodeItemInfo> getItems() {return m_list.iterator();}
	public void show() {
		for (int i = 0; i < m_list.size(); i++) {
			NodeItemInfo node = (NodeItemInfo) m_list.get(i);
			node.show();
		}
	}
}
