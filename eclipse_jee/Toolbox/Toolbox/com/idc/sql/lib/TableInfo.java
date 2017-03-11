package com.idc.sql.lib;

/**
* @author John Vincent
*/

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.idc.trace.LogHelper;

public class TableInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String select;
	private String clause;
	private String order;
	private ArrayList<String> pk = new ArrayList<String>();
	private ArrayList<ColumnInfo> col = new ArrayList<ColumnInfo>();
	private IndexesInfo idx = new IndexesInfo();

	public TableInfo (String table, String select, String clause, String order) {
		name = table;
		this.select = select;
		this.clause = clause;
		this.order = order;
	}
	public String getName() {return name;}
	public ArrayList<String> getPklist() {return pk;}
	public boolean isExistPkList() {return pk.isEmpty() ? false : true;}
	public ArrayList<ColumnInfo> getCollist() {return col;}
	public IndexesInfo getIdxlist() {return idx;}

	public String getSelect() {return select;}
	public boolean isSelect() {return select != null && select.length() > 0 ? true : false;}

	public String getClause() {return clause;}
	public boolean isClause() {return clause != null && clause.length() > 0 ? true : false;}

	public void addPklist (ArrayList<String> list) {pk = list;}
	public void addColumnlist (ArrayList<ColumnInfo> list) {col = list;}
	public void addIndexlist (IndexesInfo indexesInfo) {idx = indexesInfo;}
	
	public ColumnInfo getColumn(String name) {
		ColumnInfo info;
		Iterator<ColumnInfo> iterator = col.iterator();
		while (iterator.hasNext()) {
			info = (ColumnInfo) iterator.next();
			if ((info.getName()).equals(name)) return info;
		}
		return null;
	}
	
	public String getOrderByClause() {
		if (order != null && order.length() > 0) return order;

		StringBuffer buf = new StringBuffer();
		String column;
		boolean first = true;
		Iterator<String> iterator = pk.iterator();
		while (iterator.hasNext()) {
			column = (String) iterator.next();
			if (first) {
				buf.append ("order by ");
				first = false;
			}
			else
				buf.append (", ");
			buf.append (column);
		}
		return buf.toString();
	}
	
	public void doShowInfo() {
		LogHelper.info("Table :"+name);
		doShowPrimaryKeys();
		doShowColumnInfo();
	}

	public void doShowPrimaryKeys() {
		String column;
		Iterator<String> iterator = pk.iterator();
		while (iterator.hasNext()) {
			column = (String) iterator.next();
			LogHelper.info("Primary key: "+column);
		}		
	}
	private void doShowColumnInfo() {
		ColumnInfo info;
		Iterator<ColumnInfo> iterator = col.iterator();
		while (iterator.hasNext()) {
			info = (ColumnInfo) iterator.next();
			info.doShowInfo();
		}		
	}
}

	