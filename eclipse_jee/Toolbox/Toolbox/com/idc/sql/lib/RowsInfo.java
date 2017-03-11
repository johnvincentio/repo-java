package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class RowsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<RowInfo> m_list = new ArrayList<RowInfo>();

	public Iterator<RowInfo> getItems() {return m_list.iterator();}
	public void add (RowInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((RowInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
