
package com.idc.excel;

import java.util.ArrayList;

public class Sections {
	private ArrayList<Section> m_list = new ArrayList<Section>();
	private int m_nPos = 0;

	public void add (Section item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public Section getSection() {return ((Section) m_list.get(m_nPos));}
	public void reset() {m_nPos = 0;}

	public void show(String msg) {
		if (! isEmpty()) {
			System.out.println("Showing All Sections");
			for (int i=0; i<m_list.size(); i++) {
				Section item = (Section) m_list.get(i);
				item.show();
				item.showTotals(msg);
			}
			System.out.println("Sections Done");
		}
	}
}
