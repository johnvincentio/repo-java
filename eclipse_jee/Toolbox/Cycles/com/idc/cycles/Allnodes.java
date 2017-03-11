package com.idc.cycles;

import java.util.ArrayList;

public class Allnodes {
	private ArrayList<JVNode> m_list;

	private int m_nPos = 0;
	public Allnodes() {
		m_list = new ArrayList<JVNode>();
	}
	public void add(JVNode node) {m_list.add(node);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public String getName() {return ((JVNode) m_list.get(m_nPos)).getName();}
	public void reset() {m_nPos = 0;}
	public JVNode getNode() {return ((JVNode) m_list.get(m_nPos));}
	public void show() {
		for (int i = 0; i < m_list.size(); i++) {
			JVNode node = (JVNode) m_list.get(i);
			node.show();
		}
	}
}
