package com.idc.sql.lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class Scenarios implements Serializable {
	private static final long serialVersionUID = 1;
	private ArrayList<Scenario> m_list = new ArrayList<Scenario>();

	public Iterator<Scenario> getItems() {return m_list.iterator();}
	public void add (Scenario item) {m_list.add(item);}
	public int getSize() {return m_list.size();}
	public boolean isNone() {return getSize() < 1;}

	public Scenario getScenario (String name) {
		Iterator<Scenario> iter = getItems();
		while (iter.hasNext()) {
			Scenario scenario = (Scenario) iter.next();
			if (scenario.getName().equalsIgnoreCase(name.trim())) return scenario;
		}
		return null;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((Scenario) m_list.get(i)).toString());
		return "("+buf.toString()+")";
	}
}