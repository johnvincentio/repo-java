package com.hertz.hack.parser;

import java.util.ArrayList;

public class Data {
	ArrayList<Item> m_list = new ArrayList<Item>();	// cards in a players hand
	int m_nPos = 0;
	public Data() {}

	public void add(Item item) {m_list.add(item);}
	public boolean hasNext() {return m_nPos < m_list.size();}
	public Item getNext() {return (Item) m_list.get(m_nPos++);}
	public void show() {
		for (int i=0; i<m_list.size(); i++)
			System.out.println(((Item) m_list.get(i)).toString());
	}
}
