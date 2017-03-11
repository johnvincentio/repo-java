package com.idc.rda.phase2.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SectionInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<SectionItemInfo> m_collection = new ArrayList<SectionItemInfo>();

	public Iterator<SectionItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public SectionItemInfo getSectionItemInfo (String section) {
		Iterator<SectionItemInfo> iter = getItems();
		while (iter.hasNext()) {
			SectionItemInfo sectionItemInfo = iter.next();
			if (sectionItemInfo.getName().equals(section)) return sectionItemInfo;
		}
		SectionItemInfo sectionItemInfo =  new SectionItemInfo (section);
		m_collection.add (sectionItemInfo);
		return sectionItemInfo;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+buf.toString()+")";
	}
}
