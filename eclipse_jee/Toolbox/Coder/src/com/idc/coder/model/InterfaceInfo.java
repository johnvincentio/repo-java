package com.idc.coder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class InterfaceInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<InterfaceItemInfo> m_collection = new ArrayList<InterfaceItemInfo> ();

	public void add (InterfaceItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<InterfaceItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public boolean isInterface() {
		if (isNone()) return false;
		return true;
	}

	public String getInterfaces() {
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for (InterfaceItemInfo item : m_collection) {
			if (! first) buf.append (", ");
			first = false;
			buf.append (item.getInterfaze().getSimpleName());
		}
		return buf.toString();		
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append ("(interface: [\n");
		for (InterfaceItemInfo item : m_collection) {
			buf.append ("\t\t");
			buf.append (item.toString());
			buf.append ("\n");
		}
		buf.append ("\t\t]\n\t)\n");
		return buf.toString();
	}
}
