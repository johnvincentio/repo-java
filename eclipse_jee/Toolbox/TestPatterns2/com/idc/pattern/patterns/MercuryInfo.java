package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class MercuryInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private ArrayList<Object> m_collection;

	public MercuryInfo () {m_collection = new ArrayList<Object> ();}
	public MercuryInfo (int capacity) {m_collection = new ArrayList<Object> (capacity);}

	public void add (Object item) {if (item != null) m_collection.add(item);}
	public Iterator<Object> getItems() {return m_collection.iterator();}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String toString() {return "("+m_collection+")";}
}
