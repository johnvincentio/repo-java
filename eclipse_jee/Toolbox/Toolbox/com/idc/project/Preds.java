package com.idc.project;

import java.util.ArrayList;
import java.util.Iterator;

public class Preds {
	private ArrayList<Pred> m_list = new ArrayList<Pred>();
	public void add (Pred item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public Iterator<Pred> getItems() {return m_list.iterator();}

	public void show() {
		if (! isEmpty()) {
			System.out.println("Showing All Preds");
			for (int i=0; i<m_list.size(); i++) {
				Pred item = (Pred) m_list.get(i);
				System.out.println (item.toString());
			}
			System.out.println("Done");
		}
	}
}
