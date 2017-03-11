package com.idc.project;

import java.util.ArrayList;

public class Preds {
	private ArrayList<Pred> m_list = new ArrayList<Pred>();
	private int m_nPos = 0;

	public void add (Pred item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public void reset() {m_nPos = 0;}

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
