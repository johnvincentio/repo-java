package com.idc.scrabble.server;

import com.idc.scrabble.utils.Debug;
import com.idc.scrabble.utils.Constants;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Scorer {
	private Game m_game;
	private int m_board[][];		// value of letter on the board
	private boolean m_bBoardIsStillEmpty = true;	// first move rules

	private final int m_iaValue[] = 			// value of tiles by letter
		{0, 1, 3, 3, 2, 1, 4, 2, 4, 1, 8,
//       '' A  B  C  D  E  F  G  H  I  J   	    
         5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1,
//       K  L  M  N  O  P   Q  R  S  T  U 
         4, 4, 8, 4, 10};
//       V  W  X  Y  Z
	private final int m_MultiplierTypesByPosition[] = 	// also in ClientImages.java
	       {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4,	// 0 = no multiplier
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,	// 1 = double letter
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,	// 2 = double word
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,	// 3 = triple letter
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,	// 4 = triple word
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,	// 5 = centre - double word
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			4, 0, 0, 1, 0, 0, 0, 5, 0, 0, 0, 1, 0, 0, 4,
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,
  			4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4};

	public Scorer (Game game) {
		m_game = game;
		m_board = new int[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
		for (int i=0; i<Constants.BOARD_SIZE; i++) {
			for (int j=0; j<Constants.BOARD_SIZE; j++) {
				m_board[i][j] = -1;	// empty square
			}
		}
	}
	public int getBoard(int i, int j) {return m_board[i][j];}
	public void setBoardNotStillEmpty() {m_bBoardIsStillEmpty = false;}
	private boolean isThisOccupied (int x, int y, int tmp2[][]) {
		if ((x >= 0) && (x < Constants.BOARD_SIZE) &&
			(y >= 0) && (y < Constants.BOARD_SIZE) &&
			tmp2[x][y] > -1) return true;
		return false;
	}
//
//  Validate the client move.
//
	public boolean validateThisUserMove(int tmpBoard[][], int place) {
		Debug.println(">>> Scorer::validateThisUserMove()");
		List changedLetters = new ArrayList();
		for (int i=0; i<Constants.BOARD_SIZE; i++) {
			for (int j=0; j<Constants.BOARD_SIZE; j++) {
				if (tmpBoard[i][j] != m_board[i][j])
					changedLetters.add(
						new LetterSlave(i,j,m_board[i][j],tmpBoard[i][j],
							m_MultiplierTypesByPosition[i*Constants.BOARD_SIZE+j]));
			}
		}
		if (changedLetters.size() < 1) {
			m_game.sendBadMoveMessage (place,"You Did Not Move - Try Again");
			Debug.println("<<< Scorer::BADMOVE - no move");
			return false;
		}
		for (int i=0; i<changedLetters.size(); i++)
			Debug.println(((LetterSlave)changedLetters.get(i)).toString());

		int cx, cy;			 //	ensure letters are in a line
		boolean bHorizWord = true;
		boolean bVertWord = true;
		int nMinX = 99; int nMaxX = 0;
		int nMinY = 99; int nMaxY = 0;
		LetterSlave slave = (LetterSlave)changedLetters.get(0);
		int sx = slave.getX(); int sy = slave.getY();
		for (int i=0; i<changedLetters.size(); i++) {
			slave = (LetterSlave)changedLetters.get(i);
			cx = slave.getX(); cy = slave.getY();
			if (cx < nMinX) nMinX = cx;
			if (cy < nMinY) nMinY = cy;
			if (cx > nMaxX) nMaxX = cx;
			if (cy > nMaxY) nMaxY = cy;
			Debug.println("i "+i+"; sx "+sx+" sy "+sy+" cx "+cx+" cy "+cy);
			if (sx != cx) bHorizWord = false;	// not the same row
			if (sy != cy) bVertWord = false;	// not the same column
		}
		Debug.println("bHoriz "+bHorizWord+" bVert "+bVertWord);
		if ((! bHorizWord) && (! bVertWord)) {	// letters not in a line
			m_game.sendBadMoveMessage (place,"Letters must be placed in a line!");
			Debug.println("<<< Scorer::BADMOVE letters not aligned");
			return false;
		}
		Debug.println("min(x,y) ("+nMinX+","+nMinY+")");
		Debug.println("max(x,y) ("+nMaxX+","+nMaxY+")");

		boolean bValid = true;
		if (changedLetters.size() > 1) { // ensure letters are contiguous
			int i = nMinX; int j = nMinY;
			if (bHorizWord) {
				for (j=nMinY; j<=nMaxY; j++)
					if (tmpBoard[i][j] == -1) bValid = false;
			} else {
				for (i=nMinX; i<=nMaxX; i++)
					if (tmpBoard[i][j] == -1) bValid = false;
		}	}
		Debug.println("Is it a Contiguous word "+bValid);
		if (! bValid) {
			m_game.sendBadMoveMessage (place,"Letters must be contiguous!");
			Debug.println("<<< Scorer::BADMOVE letters not contiguous");
			return false;
		}

		bValid = false;
		if (! m_bBoardIsStillEmpty) {	// word must be stuck to a letter
			Debug.println("check word is appended to another");
			for (int i=0; i<changedLetters.size(); i++) {
				slave = (LetterSlave)changedLetters.get(i);
				cx = slave.getX(); cy = slave.getY();
				if (isThisOccupied (cx, cy-1, m_board) ||
						isThisOccupied (cx, cy+1, m_board) ||
						isThisOccupied (cx-1, cy, m_board) ||
						isThisOccupied (cx+1, cy, m_board)) {
					bValid = true;
					break;
				}
			}
			Debug.println("Is it stuck to another word "+bValid);
			if (! bValid) {
				m_game.sendBadMoveMessage (place,"Word must be attached to another");
				Debug.println("<<< Scorer::BADMOVE; Word must be attached to another");
				return false;
			}
		}
//
// Make a list of all words on the board.
// if word was already there, it must have been OK. Thus, only new words
// could create problems. 
// Placing a word can create more than one new word!
//

		Debug.println("Making a list of words on the board");
		List oldWordsBoardList = new ArrayList();
		List newWordsBoardList = new ArrayList();
		makeWordsBoardList (oldWordsBoardList, m_board);
		makeWordsBoardList (newWordsBoardList, tmpBoard);
		traceAllWords ("Old list",oldWordsBoardList);
		traceAllWords ("New list",newWordsBoardList);
		rationaliseWordsList (newWordsBoardList, oldWordsBoardList);
		traceAllWords ("word list to be scored",newWordsBoardList);
//
//	Score the words. Each letter has a value, except for blanks which
//	score zero. Check for word and letter multipliers.
//
		Debug.println("Making a list of words to be scored");
		String strWord;		 	// score each word in the list
		int scoreTotal = 0;
		List wordsToBeScored = new ArrayList();
		WordSlave wordSlave;
		ScoreSlave scoreSlave;		// put words into ScoreSlave format
		for (int i=0; i<newWordsBoardList.size(); i++) {
			wordSlave = (WordSlave)newWordsBoardList.get(i);
			scoreSlave = new ScoreSlave (wordSlave, tmpBoard);
			wordsToBeScored.add(scoreSlave);
		}
		Debug.println("Scoring each word in turn");
		for (int i=0; i<wordsToBeScored.size(); i++) {
			scoreSlave = (ScoreSlave)wordsToBeScored.get(i);
			scoreSlave.traceScoreSlave("Score this word");
			scoreTotal += scoreThisWord (scoreSlave);
		}
		Debug.println("Score is "+scoreTotal);
		Debug.println("Changed Letters "+changedLetters.size());
		if (changedLetters.size() >= 7) scoreTotal += 50;
		Debug.println("Total Score is "+scoreTotal);
		m_game.addToPlayerScore (place, scoreTotal);	// increment players score

		Debug.println("Disable any used multipliers");
		for (int i=0; i<wordsToBeScored.size(); i++) {
			scoreSlave = (ScoreSlave)wordsToBeScored.get(i);
			disableMultipliersThisWord (scoreSlave);
		}

//
//	Done with checking - It is a valid move.
//		Update the board and the user's rack.
//			Update the user's score.	
//
		for (int i=0; i<Constants.BOARD_SIZE; i++) {	// update the board
			for (int j=0; j<Constants.BOARD_SIZE; j++)
				m_board[i][j] = tmpBoard[i][j];
		}
		int letter;
		for (int i=0; i<changedLetters.size(); i++) { //update the rack
			letter = ((LetterSlave)changedLetters.get(i)).getNewLetter();
			Debug.println("letter "+letter);
			m_game.replaceRackLetter(false,place,letter);
		}
		Debug.println("<<< Scorer::OK MOVE");
		return true;
	}
//
//	handle scoring the word
//
	private int scoreThisWord (ScoreSlave scoreSlave) {
		Debug.println(">>>scoreThisWord "+scoreSlave.getWord());
		WordSlave wordSlave = scoreSlave.getWordSlave();
		LetterSlave letterSlave;
		int nTotal = 0;
		int nLetterValue, nLetterNumber, nMulti;
		int nWordMulti = 1;
		for (int i=0; i<scoreSlave.getWordLength(); i++) {	// handle letter values
			letterSlave = scoreSlave.getLetterSlave(i);
			nLetterNumber = letterSlave.getNewLetter();
			nLetterValue = m_iaValue[nLetterNumber];
			nMulti = letterSlave.getScorer();	// no dice, must turn off after use
			Debug.println("num "+nLetterNumber+" Value "+nLetterValue+" multi "+nMulti);
			if (nMulti == 1) nLetterValue *= 2;		// double letter
			if (nMulti == 2) nWordMulti *= 2;		// double word
			if (nMulti == 3) nLetterValue *= 3;		// triple letter
			if (nMulti == 4) nWordMulti *= 3;		// triple word
			if (nMulti == 5) nWordMulti *= 2;		// centre - double word
			nTotal += nLetterValue;
			Debug.println("Letter scored "+nLetterValue);
		}
		nTotal *= nWordMulti;
		Debug.println("<<< scoreThisWord, value "+nTotal);
		return nTotal;
	}
	private void disableMultipliersThisWord (ScoreSlave scoreSlave) {
		Debug.println(">>>disableMultipliersThisWord "+scoreSlave.getWord());
		WordSlave wordSlave = scoreSlave.getWordSlave();
		LetterSlave letterSlave;
		int nX, nY, num;
		for (int i=0; i<scoreSlave.getWordLength(); i++) {	// handle letter values
			letterSlave = scoreSlave.getLetterSlave(i);
			nX = letterSlave.getX();
			nY = letterSlave.getY();
			num = nX * Constants.BOARD_SIZE + nY;
			m_MultiplierTypesByPosition[num] = 0;	// disable multiplier
		}
		Debug.println("<<< disableMultipliersThisWord");
	}
//
//	handle the word lists
//	
	private void rationaliseWordsList (List newList, List oldList) {
		String oldWord, newWord;		 // remove old words from new list
		for (int i=0; i<oldList.size(); i++) {
			oldWord = ((WordSlave)oldList.get(i)).getWord();
			Debug.println ("Word to be removed is :"+oldWord);
			for (int j=0; j<newList.size(); j++) {
				newWord = ((WordSlave)newList.get(j)).getWord();
				if (newWord.equals(oldWord))
					newList.remove(j);	// remove from list
			}
		}
	}
	private void traceAllWords (String msg, List wordsList) {
		Debug.println("Trace words in list; "+msg);
		for (int i=0; i<wordsList.size(); i++) {
			Debug.println ("Word is; "+((WordSlave)wordsList.get(i)).toString());
		}
	}
	private void makeWordsBoardList (List wordsList, int iaBoard[][]) {
		parseBoard (true, wordsList, iaBoard);
		parseBoard (false, wordsList, iaBoard);
	}
	private void parseBoard (boolean bHoriz, List wordsList, int iaBoard[][]) {
		WordSlave wordSlave = null;
		boolean bFoundWord = false;
		int row, col, num;
		for (row=0; row<Constants.BOARD_SIZE; row++) {
			bFoundWord = false;
			for (col=0; col<Constants.BOARD_SIZE; col++) {
				if (bHoriz)				 // look for horiz words
					num = iaBoard[row][col];
				else					 // look for vertical words
					num = iaBoard[col][row];
				if (num > -1) {			// found a letter
					if (bFoundWord) {	// attach to current word
						wordSlave.addChar(num);
					} else {			// new word
						if (bHoriz)		// careful, row and col are switched!
							wordSlave = new WordSlave(true,row,col,num);
						else
							wordSlave = new WordSlave(false,col,row,num);	// here...
						bFoundWord = true;
					}
				}
				else {					 // not occupied
					if (bFoundWord && wordSlave.isValidWord()) 	// add word to list
						wordsList.add(wordSlave);
					bFoundWord = false;
				}
			}
		}
		if (bFoundWord && wordSlave.isValidWord()) 	// add word to list
			wordsList.add(wordSlave);
	}
//
//	Slave classes
//
	private class ScoreSlave {		// help to calculate the score
		private WordSlave m_wordSlave;
		private int m_newBoard[][];
		private List m_letterSlaves = new ArrayList();
		public ScoreSlave (WordSlave slave, int newBoard[][]) {
			m_wordSlave = slave;
			m_newBoard = newBoard;
			constructLetters();
		}
		public LetterSlave getLetterSlave(int i) {return (LetterSlave)m_letterSlaves.get(i);}
		public WordSlave getWordSlave() {return m_wordSlave;}
		public String getWord() {return m_wordSlave.getWord();}
		public int getWordLength() {return m_letterSlaves.size();}
		private void constructLetters() {
			int buflen = m_wordSlave.getLength();
			int bx = m_wordSlave.getX();
			int by = m_wordSlave.getY();
			int cx = bx;
			int cy = by;
			for (int i=0; i<buflen; i++) {
				if (m_wordSlave.isHoriz())
					cy = by + i;
				else
					cx = bx + i;
				m_letterSlaves.add(
					new LetterSlave(cx,cy,m_board[cx][cy],m_newBoard[cx][cy],
						m_MultiplierTypesByPosition[cx*Constants.BOARD_SIZE+cy]));
			}
		}
		public void traceScoreSlave (String msg) {
			Debug.println("traceScoreSlave; "+msg);
			Debug.println("WordSlave; "+m_wordSlave.toString());
			LetterSlave slave;
			for (int i=0; i<m_letterSlaves.size(); i++)
				Debug.println("LetterSlave; "+
							((LetterSlave)m_letterSlaves.get(i)).toString());
		}
	}
	private class WordSlave {		// model a word on the board
		private boolean m_bHorizontal;
		private int m_nX;
		private int m_nY;
		private StringBuffer m_buffer;
		public WordSlave (boolean bHoriz, int x, int y, int letter) {
			m_bHorizontal = bHoriz;
			m_nX = x;
			m_nY = y;
			m_buffer = new StringBuffer();
			addChar(letter);
		}
		public int getX() {return m_nX;}
		public int getY() {return m_nY;}
		public int getLength() {return m_buffer.length();}
		public StringBuffer getBuffer() {return m_buffer;}
		public boolean isHoriz() {return m_bHorizontal;}
		public boolean isValidWord() {
			if (getLength() < 2) return false;
			return true;
		}
		private void addChar (int letter) {m_buffer.append(getLetterChar(letter));}
		private String getWord() {return m_buffer.toString();}
		private char getLetterChar (int letter) {
			return (char) (letter + 96);}
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("bHoriz ").append(m_bHorizontal);
			buf.append(" X ").append(m_nX).append(",Y ").append(m_nY);
			buf.append(" Word :").append(getWord()).append(":");
			return buf.toString();
		}
	}
	private class LetterSlave {		// helps validate get it done!
		private int m_nX;
		private int m_nY;
		private int m_nOldLetter;
		private int m_nNewLetter;
		private int m_nScorer;
		private LetterSlave (int x, int y, int nOld, int nNew, int score) {
			m_nX = x;
			m_nY = y;
			m_nOldLetter = nOld;
			m_nNewLetter = nNew;
			m_nScorer = score;
		}
		public int getX() {return m_nX;}
		public int getY() {return m_nY;}
		public int getOldLetter() {return m_nOldLetter;}
		public int getNewLetter() {return m_nNewLetter;}
		public int getScorer() {return m_nScorer;}

		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("X ").append(m_nX).append(",Y ").append(m_nY);
			buf.append(" old ").append(m_nOldLetter);
			buf.append(" new ").append(m_nNewLetter);
			buf.append(" score ").append(m_nScorer);
			return buf.toString();
		}
	}
}

