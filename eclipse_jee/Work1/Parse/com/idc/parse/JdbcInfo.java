package com.idc.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class JdbcInfo implements Serializable {
	private ArrayList m_list = new ArrayList();
	private String classpath;

	public Iterator getItems() {return m_list.iterator();}
	public void add (JdbcItemInfo item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getClasspath() {return classpath;}
	public void setClasspath (String classpath) {this.classpath = classpath;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((JdbcItemInfo) m_list.get(i)).toString());
		return "("+getClasspath()+"),"+"("+buf.toString()+")";
	}
}
