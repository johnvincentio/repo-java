package com.idc.project;

import java.util.ArrayList;
import java.util.Iterator;

public class Tasks {
	private ArrayList<Task> m_list = new ArrayList<Task>();
	public Iterator<Task> getItems() {return m_list.iterator();}
	public void add (Task item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}

	public void show() {
		System.out.println("Showing All Tasks\n");
		for (int i=0; i<m_list.size(); i++) {
			Task item = (Task) m_list.get(i);
			item.show();
		}
		System.out.println("\nDone");
	}
}
