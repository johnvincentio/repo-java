package com.idc.a7;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

class CardDemo1 extends JFrame {
	private static final long serialVersionUID = 1;

	private static Card[] _deck = new Card[52];

	public static void main(String[] args) {
		CardDemo1 window = new CardDemo1();
		window.setTitle("Card Demo 1");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new CardTable(_deck));
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public CardDemo1() {
		// ... ClassLoader is where to get images from this .jar file.
		ClassLoader cldr = this.getClass().getClassLoader();

		int n = 0; // Which card.
		int xPos = 0; // Where it should be placed initially.
		int yPos = 0;

		// ... Read in the cards using particular file name conventions.
		// Images for the backs and Jokers are ignored here.
		String suits = "shdc";
		String faces = "a23456789tjqk";
		for (int suit = 0; suit < suits.length(); suit++) {
			for (int face = 0; face < faces.length(); face++) {
				// ... Get the image from the images subdirectory.
				String imagePath = "cards/" + faces.charAt(face)
						+ suits.charAt(suit) + ".gif";
				URL imageURL = cldr.getResource(imagePath);
				ImageIcon img = new ImageIcon(imageURL);

				// ... Create a card and add it to the deck.
				Card card = new Card(img);
				card.moveTo(xPos, yPos);
				_deck[n] = card;

				// ... Update local vars for next card.
				xPos += 5;
				yPos += 4;
				n++;
			}
		}
	}
}
