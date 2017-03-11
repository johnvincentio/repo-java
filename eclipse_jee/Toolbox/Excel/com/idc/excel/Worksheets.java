
package com.idc.excel;

import java.util.ArrayList;

public class Worksheets {
	private ArrayList<Worksheet> m_list = new ArrayList<Worksheet>();
	private int m_nPos = 0;

	public void add (Worksheet item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public Worksheet getWorksheet() {return ((Worksheet) m_list.get(m_nPos));}
	public void reset() {m_nPos = 0;}

	public void show(String msg) {
		if (! isEmpty()) {
			System.out.println("Showing All Worksheets");
			for (int i=0; i<m_list.size(); i++) {
				Worksheet item = (Worksheet) m_list.get(i);
				item.show(msg);
			}
			System.out.println("Worksheets Done");
		}
	}
}
