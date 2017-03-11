package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;


public class RowInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<DataInfo> m_list = new ArrayList<DataInfo>();

	public Iterator<DataInfo> getItems() {return m_list.iterator();}
	public void add (DataInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((DataInfo) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
