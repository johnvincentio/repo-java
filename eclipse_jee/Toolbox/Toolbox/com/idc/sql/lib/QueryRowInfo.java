package com.idc.sql.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
* @author John Vincent
*/

public class QueryRowInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<QueryRowItemInfo> m_list = new ArrayList<QueryRowItemInfo>();
	private int rows;

	public QueryRowInfo() {this(0);}
	public QueryRowInfo (int rows) {this.rows = rows;}

	public Iterator<QueryRowItemInfo> getItems() {return m_list.iterator();}
	public void add (QueryRowItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public int getRows() {return rows;}
	public void setRows (int rows) {this.rows = rows;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((QueryRowItemInfo) m_list.get(i)).toString());
		return "("+getRows()+"),"+"("+buf.toString()+")";
	}
}
