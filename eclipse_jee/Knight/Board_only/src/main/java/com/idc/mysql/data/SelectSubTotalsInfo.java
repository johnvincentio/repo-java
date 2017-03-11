package com.idc.mysql.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SelectSubTotalsInfo implements Serializable {
	private static final long serialVersionUID = -5338899143485102456L;

	private ArrayList<SelectSubTotalsItemInfo> m_collection;

	public SelectSubTotalsInfo () {m_collection = new ArrayList<SelectSubTotalsItemInfo> ();}

	public void add (SelectSubTotalsItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<SelectSubTotalsItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
