
package com.idc.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.idc.trace.LogHelper;

public class Deck {
	private List<Card> m_list = new ArrayList<Card>(); // make a deck of cards
	public Deck() {
		for (int i=1; i<5; i++) {
			for (int j=1; j<14; j++)
				m_list.add(new Card(i,j));
		}
	}
	public Card takeCard() {			// take a card from the deck
		Collections.shuffle(m_list);
		Card myCard = new Card((Card) m_list.get(0));	// take a card
		m_list.remove(0);						// remove card from the deck
		LogHelper.info("Deck:get; "+myCard.toString());
		return myCard;			// return the card
	}
	public boolean isEmpty() {return m_list.isEmpty();}
}

