package com.idc.cycles;

import java.util.Iterator;

public class NodeItemInfo {
	private String m_name;
	private int m_level;
	private NodeInfo m_nodeInfo;

	public NodeItemInfo (String name, int level) {
		m_name = name;
		m_level = level;
		m_nodeInfo = new NodeInfo();
	}
	public String getName() {return m_name;}
	public int getLevel() {return m_level;}
	public void add (NodeItemInfo nodeItemInfo) {m_nodeInfo.add (nodeItemInfo);}
	public void add (DefItemInfo defItemInfo, int level) {
		Iterator<String> iter = defItemInfo.getItems();
		while (iter.hasNext()) {
			String str = (String) iter.next();
			add (new NodeItemInfo (str, level));
		}
	}
	public NodeInfo getNodeInfo() {return m_nodeInfo;}

	public void show() {
		for (int i = 0; i < m_level; i++)
			System.out.print("\t");
		System.out.println(m_level + " " + m_name);
		m_nodeInfo.show();
	}
}
