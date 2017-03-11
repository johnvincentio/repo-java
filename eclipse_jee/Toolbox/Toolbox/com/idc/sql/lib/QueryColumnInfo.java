package com.idc.sql.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
* @author John Vincent
*/

public class QueryColumnInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sql;
	private int columns;
	private ArrayList<QueryColumnItemInfo> m_list = new ArrayList<QueryColumnItemInfo>();

	public QueryColumnInfo (String sql) {this.sql = sql;}

	public Iterator<QueryColumnItemInfo> getItems() {return m_list.iterator();}
	public void add (QueryColumnItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getSql() {return sql;}
	public int getColumns() {return columns;}
	public void setSql (String sql) {this.sql = sql;}
	public void setColumns (int columns) {this.columns = columns;}

	public QueryColumnItemInfo getQueryColumnItemInfo (int num) {
		QueryColumnItemInfo info;
		Iterator<QueryColumnItemInfo> iter = getItems();
		while (iter.hasNext()) {
			info = (QueryColumnItemInfo) iter.next();
			if (info.getColumn() == num) return info;
		}
		return null;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((QueryColumnItemInfo) m_list.get(i)).toString());
		return "("+getSql()+","+getColumns()+")\n"+buf.toString();
	}
}
