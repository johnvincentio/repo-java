
package com.idc.excel;

import java.util.ArrayList;

public class Section {
	private String name;
	private String ds;
	private ArrayList<Task> m_list = new ArrayList<Task>();
	private int m_nPos = 0;

	public String getName() {return name;}
	public String getDs() {return ds;}
	public void setName(String str) {name = str;}
	public void setDs(String str) {ds = str;}
	public void add (Task item) {m_list.add (item);}
	public boolean isEmpty() {return m_list.isEmpty();}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public void getNext() {m_nPos++;}
	public Task getTask() {return ((Task) m_list.get(m_nPos));}
	public void reset() {m_nPos = 0;}

	private Totals totals = new Totals();
	public Totals getTotals() {return totals;}
	public void setTotals (Task task) {totals.setTotals(task);}
	public void show() {
		System.out.println("Section :"+getName()+" "+getDs());
		if (! isEmpty()) {
			System.out.println("Showing All Tasks");
			for (int i=0; i<m_list.size(); i++) {
				Task item = (Task) m_list.get(i);
				System.out.println (item.toString());
			}
			System.out.println("Tasks Done");
		}
	}
	public void showTotals(String msg) {
		System.out.println(">>> Section::showTotals; "+msg);
		System.out.println("Section :"+getName()+" "+getDs());		
		totals.show(msg);
		System.out.println("<<< Section::showTotals; "+msg);
	}
}
