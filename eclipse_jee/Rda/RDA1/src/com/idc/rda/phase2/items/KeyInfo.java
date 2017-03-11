package com.idc.rda.phase2.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class KeyInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private ArrayList<KeyItemInfo> m_collection = new ArrayList<KeyItemInfo>();
	public KeyInfo (String name) {this.name = name;}
	public String getName() {return name;}

	public void add (KeyItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<KeyItemInfo> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+getName()+"),"+"("+buf.toString()+")";
	}
}
