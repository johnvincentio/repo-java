package com.idc.pattern.patterns;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class JupiterInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Set<Object> m_collection = new HashSet<Object>();

	public JupiterInfo (int capacity) {m_collection = new HashSet<Object>(capacity);}

	public Iterator<Object> getItems() {return m_collection.iterator();}
	public void add (Object item) {if (item != null) m_collection.add (item);}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

//	protected Set<Object> getCollection() {return m_collection;}
	public boolean isExists (Object item) {return m_collection.contains (item);}

	public String toString() {return "("+m_collection+")";}
}
