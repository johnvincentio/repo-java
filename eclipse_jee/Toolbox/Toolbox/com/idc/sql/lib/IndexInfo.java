package com.idc.sql.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class IndexInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<IndexItemInfo> m_collection;

	private String indexName;
	private boolean unique;

	public IndexInfo (String indexName, boolean unique) {
		m_collection = new ArrayList<IndexItemInfo> ();
		this.indexName = indexName;
		this.unique = unique;
	}

	public void add (IndexItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<IndexItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getIndexName() {return indexName;}
	public boolean isUnique() {return unique;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append(((IndexItemInfo) m_collection.get(i)).toString());
		return "("+getIndexName()+","+isUnique()+"("+buf.toString()+")";
	}
}
