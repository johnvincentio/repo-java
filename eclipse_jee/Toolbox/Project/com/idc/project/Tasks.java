package com.idc.project;

import java.util.ArrayList;
import java.util.Iterator;

public class Tasks {
	private ArrayList<Task> m_list = new ArrayList<Task>();

	public void add (Task item) {m_list.add (item);}
	public Iterator<Task> getItems() { return m_list.iterator(); }
	public int size() {return m_list.size();}
	public boolean isEmpty() {return size() < 1;}

	public void deleteTask (Task task) {
	    Iterator<Task> iterator = m_list.iterator();
	    Task item;
	    int ind = 0;
	    while (iterator.hasNext()) {
	        item = (Task) iterator.next();
	        if (item.getUid().equals(task.getUid())) m_list.remove(ind);
	        ind++;
	    }
	}
	public Task getTask (String uid) {
	    Iterator<Task> iterator = m_list.iterator();
	    Task item;
	    while(iterator.hasNext()) {
	        item = (Task) iterator.next();
	        if(item.getUid().equals(uid)) return item;
	    }
	    return null;
	}

	public void show() {
		System.out.println("Showing All Tasks\n");
		for (int i=0; i<m_list.size(); i++) {
			Task item = (Task) m_list.get(i);
			item.show();
		}
		System.out.println("\nDone");
	}
}
