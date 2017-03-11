package com.idc.refs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ProjectInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<ProjectItemInfo> m_collection = new ArrayList<ProjectItemInfo> ();
	public void add (ProjectItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ProjectItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getName() {return name;}
	public void setName (String name) {this.name = name;}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<ProjectItemInfo> {
		public int compare(ProjectItemInfo a, ProjectItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((ProjectItemInfo) m_collection.get(i)).toString());
		return "("+getName()+"),"+"("+buf.toString()+")";
	}
}
