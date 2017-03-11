package com.idc.test;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
public class JVBuntInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Set<JVBuntItemInfo> m_collection = new HashSet<JVBuntItemInfo>();

	public Iterator<JVBuntItemInfo> getItems() {return m_collection.iterator();}
	public void add (JVBuntItemInfo item) {if (item != null) m_collection.add (item);}
	public void add (JVBuntInfo info) {if (info != null) m_collection.addAll (info.getCollection());}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	protected Set<JVBuntItemInfo> getCollection() {return m_collection;}
	public boolean isExists (JVBuntItemInfo item) {return m_collection.contains (item);}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("(");
		Iterator<JVBuntItemInfo> iter = m_collection.iterator();
		while (iter.hasNext()) {
			JVBuntItemInfo item = (JVBuntItemInfo) iter.next();
			if (item != null) buf.append (item);
		}
		buf.append (")");
		return buf.toString();
	}
}
