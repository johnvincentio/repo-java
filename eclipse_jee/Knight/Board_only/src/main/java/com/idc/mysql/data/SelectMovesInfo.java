package com.idc.mysql.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SelectMovesInfo implements Serializable {
	private static final long serialVersionUID = 4440697114538469795L;

	private ArrayList<SelectMovesItemInfo> m_collection = new ArrayList<SelectMovesItemInfo>();

	public void add (SelectMovesItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<SelectMovesItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
