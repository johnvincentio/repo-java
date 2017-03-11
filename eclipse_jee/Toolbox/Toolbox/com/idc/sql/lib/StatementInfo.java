package com.idc.sql.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class StatementInfo implements Serializable {
	private static final long serialVersionUID = 1;
	private ArrayList<StatementItemInfo> m_list = new ArrayList<StatementItemInfo>();

	public Iterator<StatementItemInfo> getItems() {return m_list.iterator();}
	public void add (StatementItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(m_list.get(i).toString());
		return "("+buf.toString()+")";
	}
}