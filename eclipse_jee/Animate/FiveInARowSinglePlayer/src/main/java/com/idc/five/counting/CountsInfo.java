package com.idc.five.counting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class CountsInfo implements Serializable {
	private static final long serialVersionUID = 6900440777817996994L;

	private ArrayList<CountsItemInfo> m_collection = new ArrayList<CountsItemInfo> ();

	void add (CountsItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<CountsItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
