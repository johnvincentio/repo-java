// File   : CardPileFreeCell.java
// Purpose: CardPile specialized to adding only one card.
// Author : Fred Swartz - February 27, 2007 - Placed in public domain.

package freecell;

/////////////////////////////////////////////////////////////// CardPileFreeCell
public class CardPileFreeCell extends CardPile {
    
    //================================================= rulesAllowAddingThisCard
    //... Accept card if pile is empty.
    @Override
    public boolean rulesAllowAddingThisCard(Card card) {
        //... Accept only if the current pile is empty.
        return size() == 0;
        
    }
}
