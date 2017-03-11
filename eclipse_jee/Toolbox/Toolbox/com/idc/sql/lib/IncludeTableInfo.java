package com.idc.sql.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class IncludeTableInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<IncludeTableItemInfo> m_collection;

	public IncludeTableInfo () {m_collection = new ArrayList<IncludeTableItemInfo> ();}

	public void add (IncludeTableItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<IncludeTableItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public IncludeTableItemInfo getTable (String tableName) {
		for (Iterator<IncludeTableItemInfo> iter = getItems(); iter.hasNext(); ) {
			IncludeTableItemInfo item = (IncludeTableItemInfo) iter.next();
			if (item.getName().equalsIgnoreCase(tableName)) return item;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((IncludeTableItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
