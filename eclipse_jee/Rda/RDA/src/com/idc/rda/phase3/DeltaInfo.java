package com.idc.rda.phase3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class DeltaInfo implements Serializable {
	private static final long serialVersionUID = -8555662465062329539L;

	private ArrayList<DeltaItemInfo> m_collection = new ArrayList<DeltaItemInfo>();
	private String subSection;
	public DeltaInfo (String subSection) {this.subSection = subSection;}

	public void add (DeltaItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<DeltaItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getSubSection() {return subSection;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+getSubSection()+"),"+"("+buf.toString()+")";
	}
}