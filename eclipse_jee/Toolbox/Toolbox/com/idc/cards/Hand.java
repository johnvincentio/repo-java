
package com.idc.cards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hand {
	private List<Card> m_list = new ArrayList<Card>();	// cards in a players hand
	public void add(Card card) {m_list.add(card);}
	public Iterator<Card> getItems() {return m_list.iterator();}
	public int getPoints() {
		int count = 0;
		for (int i=0; i<m_list.size(); i++)
			count += ((Card) m_list.get(i)).getCard();
		return count;
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<m_list.size(); i++)
			buf.append(((Card) m_list.get(i)).toString());
		return buf.toString();
	}
}

