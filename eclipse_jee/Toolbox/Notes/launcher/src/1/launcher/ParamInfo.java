package com.idc.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class ParamInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<ParamItemInfo> m_list = new ArrayList<ParamItemInfo>();

	public Iterator getItems() {return m_list.iterator();}
	public void add (ParamItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((ParamItemInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}

