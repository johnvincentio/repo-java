package com.idc.sudoku;

import java.util.ArrayList;
import java.util.Iterator;

public class Patterns {
	private ArrayList<Pattern> m_list;
	public Patterns() {m_list = new ArrayList<Pattern>();}

	public void add (Pattern item) {m_list.add(item);}
	public Iterator<Pattern> getPatterns() {return m_list.iterator();}
	
	public void showNumber() {
		System.out.println("Number of items "+m_list.size());
	}
	public void showList() {
		System.out.println("Number of items "+m_list.size());
		Pattern pattern;
		Iterator<Pattern> iter = getPatterns();
		while (iter.hasNext()) {
			pattern = (Pattern) iter.next();
			pattern.show();
		}
	}
}
