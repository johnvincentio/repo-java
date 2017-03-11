
package com.idc.appender;

import java.util.ArrayList;
import java.util.Iterator;

public class Rows {
	private ArrayList<Row> m_list = new ArrayList<Row>();
	public Iterator<Row> getItems() {return m_list.iterator();}
	public void add(Row item) {m_list.add(item);}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++) {
			buf.append(((Row) m_list.get(i)).toString());
		}
		return "("+buf.toString()+")";
	}
}
