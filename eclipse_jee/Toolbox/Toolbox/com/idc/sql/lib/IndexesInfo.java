package com.idc.sql.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class IndexesInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<IndexInfo> m_collection;

	public IndexesInfo () {m_collection = new ArrayList<IndexInfo> ();}

	public void add (String index, boolean unique, IndexItemInfo indexItemInfo) {
		IndexInfo indexInfo = getIndexInfo (index);
		if (indexInfo == null) {
			indexInfo = new IndexInfo (index, unique);
			indexInfo.add (indexItemInfo);
			m_collection.add (indexInfo);
		}
		else {
			indexInfo.add (indexItemInfo);
		}
	}
	public Iterator<IndexInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public IndexInfo getIndexInfo (String index) {
		for (Iterator<IndexInfo> iter = getItems(); iter.hasNext(); ) {
			IndexInfo indexInfo = (IndexInfo) iter.next();
			if (indexInfo.getIndexName().equals(index)) return indexInfo;
		}
		return null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((IndexInfo) m_collection.get(i)).toString());
		return "("+buf.toString()+")";
	}
}
