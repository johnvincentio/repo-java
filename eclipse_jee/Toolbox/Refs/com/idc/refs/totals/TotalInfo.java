package com.idc.refs.totals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.ProjectItemInfo;

public class TotalInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<TotalItemInfo> m_collection = new ArrayList<TotalItemInfo>();

	public Iterator<TotalItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}

	public void add (ProjectItemInfo projectItemInfo) {
		if (projectItemInfo == null) return;
		TotalItemInfo totalItemInfo = getItem (projectItemInfo.getName());	
		if (totalItemInfo == null)
			m_collection.add (new TotalItemInfo (projectItemInfo));
		else
			totalItemInfo.setProject (true);
	}
	public void add (ClasspathItemInfo classpathItemInfo) {
		if (classpathItemInfo == null) return;
		TotalItemInfo totalItemInfo = getItem (classpathItemInfo.getName());	
		if (totalItemInfo == null)
			m_collection.add (new TotalItemInfo (classpathItemInfo));
		else
			totalItemInfo.setClasspath (true);
	}
	public void add (ManifestmfItemInfo manifestmfItemInfo) {
		if (manifestmfItemInfo == null) return;
		TotalItemInfo totalItemInfo = getItem (manifestmfItemInfo.getName());	
		if (totalItemInfo == null)
			m_collection.add (new TotalItemInfo (manifestmfItemInfo));
		else
			totalItemInfo.setManifest (true);
	}
	public void add (ModulemapsItemInfo modulemapsItemInfo) {
		if (modulemapsItemInfo == null) return;
		TotalItemInfo totalItemInfo = getItem (modulemapsItemInfo.getName());	
		if (totalItemInfo == null)
			m_collection.add (new TotalItemInfo (modulemapsItemInfo));
		else
			totalItemInfo.setModulemaps (true);
	}

	public TotalItemInfo getItem (String name) {
		Iterator<TotalItemInfo> iter = getItems();
		while (iter.hasNext()) {
			TotalItemInfo item = (TotalItemInfo) iter.next();
			if (item == null) continue;
			if (item.getName().equals(name)) return item;
		}
		return null;
	}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<TotalItemInfo> {
		public int compare(TotalItemInfo a, TotalItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((TotalItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
