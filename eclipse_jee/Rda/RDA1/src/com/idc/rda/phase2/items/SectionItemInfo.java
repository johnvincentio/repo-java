package com.idc.rda.phase2.items;

import java.util.ArrayList;
import java.util.Iterator;

public class SectionItemInfo {
	private String name;
	private ArrayList<KeyInfo> m_collection = new ArrayList<KeyInfo>();

	public SectionItemInfo (String name) {this.name = name;}

	public KeyInfo getKeyInfo (String key) {
		Iterator<KeyInfo> iter = getItems();
		while (iter.hasNext()) {
			KeyInfo keyInfo = iter.next();
			if (keyInfo.getName().equals (key)) return keyInfo;
		}
		KeyInfo info = new KeyInfo (key);
		m_collection.add (info);
		return info;
	}

	public String getName() {return name;}
	public Iterator<KeyInfo> getItems() {return m_collection.iterator();}

	public String toString() {
		return "("+getName()+")";
	}
}
