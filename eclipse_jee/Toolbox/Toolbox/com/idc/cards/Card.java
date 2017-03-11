
package com.idc.cards;

public class Card {
	int m_nSuit;
	int m_nCard;
	public Card (int suit, int card) {
		m_nSuit = suit;
		m_nCard = card;
	}
	public Card (Card card) {
		m_nSuit = card.getSuit();
		m_nCard = card.getCard();
	}
	public int getSuit() {return m_nSuit;}
	public int getCard() {return m_nCard;}
	public String toString() {
		return "("+getSuitName(getSuit())+" "+getCard()+")";
	}
	private String getSuitName(int suit) {
		switch(suit) {
			case 1: return "Spade";
			case 2: return "Heart";
			case 3: return "Diamond";
			case 4: return "Club";
			default:
		}
		return "yuk";
	}
}

