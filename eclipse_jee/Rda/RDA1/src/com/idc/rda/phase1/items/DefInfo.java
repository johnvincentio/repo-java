package com.idc.rda.phase1.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class DefInfo implements Serializable {
	private static final long serialVersionUID = 6578783398592037152L;

	private ArrayList<DefItemInfo> m_collection = new ArrayList<DefItemInfo>();

	public void add (DefItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<DefItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((DefItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}


