package com.idc.scrabble.server;

import com.idc.scrabble.utils.Debug;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

//
// handle the letters in the bag
//
public class TilesBag {
	private final int m_iaNumberTiles[] = 		// tiles of each letter
		{2, 9, 2, 2, 4, 12, 2, 3, 2, 9, 1,
//       '' A  B  C  D   E  F  G  H  I  J   	    
         1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4,
//       K  L  M  N  O  P  Q  R  S  T  U 
         2, 2, 1, 2, 1};
//       V  W  X  Y  Z

	private List m_tilesBag;		// bag of tiles

	public TilesBag() {			// set up the bag if tiles
		Debug.println(">>> TilesBag::TilesBag");
		m_tilesBag = new ArrayList();
		for (int i = 0; i<m_iaNumberTiles.length; i++) {
			for (int j = 0; j< m_iaNumberTiles[i]; j++)
				addLetterToBag(i);
		}
		Debug.println("<<< TilesBag::TilesBag");
	}
	public int getSize() {return m_tilesBag.size();}

	public void addLetterToBag (int letter) {	// add letter to tile bag
//		Debug.println(">>> TilesBag::addLetterToBag");
		m_tilesBag.add(new Integer(letter));
		Collections.shuffle(m_tilesBag);	// give it a shuffle
//		Debug.println("<<< TilesBag::addLetterToBag");
	}
	private void removeLetterFromBag (int nRemove) {
//		Debug.println(">>> TilesBag::removeletterFromBag; "+nRemove);
		int letter;
		for (int i=0; i<m_tilesBag.size(); i++) {
			letter = ((Integer) m_tilesBag.get(i)).intValue();
			if (letter == nRemove) {
				m_tilesBag.remove(i);	// remove from tile bag
				Debug.println("Removed letter "+letter);
				Collections.shuffle(m_tilesBag);	// give it a shuffle
				break;
			}
		}
//		Debug.println("<<< TilesBag::removeletterFromBag");
	}
	public int getNextLetterFromBag() {	// get next letter
//		Debug.println(">>> TilesBag::getNextletterFromBag");
		int letter = -1;
		if (m_tilesBag.size() > 0) {
			letter = ((Integer) m_tilesBag.get(0)).intValue();
			m_tilesBag.remove(0);	// remove from tile bag
		}
//		Debug.println("<<< TilesBag::getNextletterFromBag;"+letter);
		return letter;	// always return a letter, even a null one
	}
	public List getLettersFromBag (int howmany) {	// bulk loader!
//		Debug.println(">>> TilesBag::getLettersFromBag");
		List listLetters = new ArrayList();
		int elements = m_tilesBag.size();
		if (elements < howmany) howmany = elements;
//		Debug.println("howmany "+howmany);
		if (howmany > 0) {
			for (int i=0; i<howmany; i++)
				listLetters.add(m_tilesBag.get(i));
			for (int i=0; i<howmany; i++)
				m_tilesBag.remove(i);
		}
//		Debug.println("<<< TilesBag::getLettersFromBag");
		return listLetters;
	}
}

