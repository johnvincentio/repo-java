package com.idc.coder.output;

import java.util.ArrayList;
import java.util.Iterator;

public class OutputInfo {

	private ArrayList<OutputItemInfo> m_collection = new ArrayList<OutputItemInfo> ();

	public void add (OutputItemInfo item) {if (item != null) m_collection.add (item);}
	public void add (String str) {
		if (str != null) add (new OutputItemInfo (str));
	}
	public void add (StringBuffer buf) {add (buf.toString());}
	public Iterator<OutputItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
