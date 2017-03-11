package com.idc.coder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ConstructorInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<ConstructorItemInfo> m_collection = new ArrayList<ConstructorItemInfo> ();

	public void add (ConstructorItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<ConstructorItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("(Constructors: [\n");
		for (ConstructorItemInfo item : m_collection) {
			buf.append ("\t\t");
			buf.append (item.toString());
			buf.append ("\n");
		}
		buf.append ("\t\t]\n\t)\n");
		return buf.toString();
	}
}
