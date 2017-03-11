package com.idc.refs.buckets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.idc.refs.data.ClasspathItemInfo;
import com.idc.refs.data.ManifestmfItemInfo;
import com.idc.refs.data.ModulemapsItemInfo;
import com.idc.refs.data.ProjectItemInfo;

public class SubBucketInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<SubBucketItemInfo> m_collection = new ArrayList<SubBucketItemInfo> ();

	public void add (ProjectItemInfo projectItemInfo) {
		if (projectItemInfo == null) return;
		SubBucketItemInfo subBucketItemInfo = getItem (projectItemInfo.getName());	
		if (subBucketItemInfo == null)
			m_collection.add (new SubBucketItemInfo (projectItemInfo));
		else
			subBucketItemInfo.incrementCounter (projectItemInfo);
	}
	public void add (ClasspathItemInfo classpathItemInfo) {
		if (classpathItemInfo == null) return;
		SubBucketItemInfo subBucketItemInfo = getItem (classpathItemInfo.getName());	
		if (subBucketItemInfo == null)
			m_collection.add (new SubBucketItemInfo (classpathItemInfo));
		else
			subBucketItemInfo.incrementCounter (classpathItemInfo);
	}
	public void add (ManifestmfItemInfo manifestmfItemInfo) {
		if (manifestmfItemInfo == null) return;
		SubBucketItemInfo subBucketItemInfo = getItem (manifestmfItemInfo.getName());	
		if (subBucketItemInfo == null)
			m_collection.add (new SubBucketItemInfo (manifestmfItemInfo));
		else
			subBucketItemInfo.incrementCounter (manifestmfItemInfo);
	}
	public void add (ModulemapsItemInfo modulemapsItemInfo) {
		if (modulemapsItemInfo == null) return;
		SubBucketItemInfo subBucketItemInfo = getItem (modulemapsItemInfo.getName());	
		if (subBucketItemInfo == null)
			m_collection.add (new SubBucketItemInfo (modulemapsItemInfo));
		else
			subBucketItemInfo.incrementCounter (modulemapsItemInfo);
	}

	public Iterator<SubBucketItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	private SubBucketItemInfo getItem (String name) {
		Iterator<SubBucketItemInfo> iter = getItems();
		while (iter.hasNext()) {
			SubBucketItemInfo item = (SubBucketItemInfo) iter.next();
			if (item == null) continue;
			if (item.getName().equals(name)) return item;
		}
		return null;
	}

	public void sort() {
		Collections.sort (m_collection, new SortItemsAsc());
	}
	private class SortItemsAsc implements Comparator<SubBucketItemInfo> {
		public int compare(SubBucketItemInfo a, SubBucketItemInfo b) {
			return a.getName().compareTo(b.getName());
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((SubBucketItemInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
