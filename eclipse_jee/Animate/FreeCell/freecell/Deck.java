// File: cards2/Deck.java
// Description: A Deck is a particular kind of CardPile with 52 Cards in it.
// Author: Fred Swartz - Feb 2007 - Placed in public domain.

package freecell;

//import java.util.*;

public class Deck extends CardPile {

    //============================================================== constructor
    /** Creates a new instance of Deck */
    public Deck() {
        for (Suit s : Suit.values()) {
            for (Face f : Face.values()) {
                Card c = new Card(f, s);
                c.turnFaceUp();
                this.push(c);
            }
        }
        shuffle();
    }
}
