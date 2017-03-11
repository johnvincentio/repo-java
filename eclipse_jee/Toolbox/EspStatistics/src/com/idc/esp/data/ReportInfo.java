package com.idc.esp.data;

import java.util.ArrayList;
import java.util.Iterator;

public class ReportInfo {
	private ArrayList<ReportItemInfo> m_collection = new ArrayList<ReportItemInfo> ();

	public void add (ReportItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ReportItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
