package com.idc.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class ScenarioInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private ArrayList<ScenarioItemInfo> m_list = new ArrayList<ScenarioItemInfo>();

	public Iterator getItems() {return m_list.iterator();}
	public void add (ScenarioItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public ScenarioItemInfo getScenarioItemInfo (String name) {
		for (Iterator iter = getItems(); iter.hasNext(); ) {
			ScenarioItemInfo item = (ScenarioItemInfo) iter.next();
			if (item.getName().equalsIgnoreCase(name)) return item;
		}
		return null;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((ScenarioItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
