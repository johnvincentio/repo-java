package com.idc.rda.phase2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class AlphaInfo implements Serializable {
	private static final long serialVersionUID = -8284222847909803216L;

	private String section;
	private ArrayList<AlphaItemInfo> m_collection = new ArrayList<AlphaItemInfo>();

	public AlphaInfo (String section) {this.section = section;}

	public String getSection() {return section;}
	public void add (AlphaItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<AlphaItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "(AlphaInfo: "+getSection()+"),"+"\n("+buf.toString()+")";
	}
}
