package com.idc.solataire;

import com.idc.trace.Debug;

public class MovesTable {
	final static int MAXMOVES = 14;
	private int CurrentEntry = -1;
	private Move movelist[] = new Move[MAXMOVES];
	public MovesTable() {for (int i=0; i<MAXMOVES; i++) {movelist[i] = new Move();}}
	private int getCurrentEntry() {return CurrentEntry;}
	public boolean isFinished() {return (CurrentEntry+3 > MAXMOVES);}
	java.util.Vector vBadMovesList = new java.util.Vector (2500,250);

	public boolean addMove(int x1, int y1, int x2, int y2, int x3, int y3, int move) {
		CurrentEntry++;
		movelist[CurrentEntry].setXTo(x1);
		movelist[CurrentEntry].setYTo(y1);
		movelist[CurrentEntry].setXAdj(x2);
		movelist[CurrentEntry].setYAdj(y2);
		movelist[CurrentEntry].setXFrom(x3);
		movelist[CurrentEntry].setYFrom(y3);
		movelist[CurrentEntry].setMoveNumber(move);
		if (isListInBadList()) {
			Debug.println("Cannot Add "+getCurrentEntry()+" (F,A,T), Move: "+movelist[CurrentEntry].stringMove());
			removeCurrentMove();
			return false;
		}
		Debug.println("Adding "+getCurrentEntry()+" (From, Adj, To), Move: "+movelist[CurrentEntry].stringMove());
		return true;
	}
	public Peg getFromPeg() {return new Peg(movelist[CurrentEntry].getXFrom(),movelist[CurrentEntry].getYFrom());}
	public Peg getAdjPeg() {return new Peg(movelist[CurrentEntry].getXAdj(),movelist[CurrentEntry].getYAdj());}
	public Peg getToPeg() {return new Peg(movelist[CurrentEntry].getXTo(),movelist[CurrentEntry].getYTo());}
	public int getCurrentMoveNumber() {return movelist[CurrentEntry].getMoveNumber();}

	public void deleteLastMove () {
		Debug.println("Deleting CurrentEntry="+(CurrentEntry)+": "+movelist[CurrentEntry].stringMove());
		addFailedList();
		listAllFailedLists("Before delete move");
		removeCurrentMove();
	}
	public void removeCurrentMove() {
		movelist[CurrentEntry].setInit();
		CurrentEntry--;
	}
	public boolean isOkDeleteLastMove() {return (CurrentEntry > 0);}
	public void printMovesTable (String msg) {
		Debug.println(">>> printMovesTable; CurrentEntry="+(CurrentEntry)+" :"+msg);
		for (int i=0;i<=CurrentEntry; i++) {
			Debug.println((i)+": (F,A,T): "+movelist[i].stringMove());
		}
		Debug.println("<<< printMovesTable");
	}

	private class Move {
		private int xFrom;
		private int yFrom;
		private int xAdj;
		private int yAdj;
		private int xTo;
		private int yTo;
		private int moveNumber;
		public Move() {setInit();}
		public void setInit() {xFrom = yFrom = xAdj = yAdj = xTo = yTo = moveNumber = 0;}
		public void setXFrom (int i) {xFrom = i;}
		public void setYFrom (int i) {yFrom = i;}
		public void setXAdj (int i) {xAdj = i;}
		public void setYAdj (int i) {yAdj = i;}
		public void setXTo (int i) {xTo = i;}
		public void setYTo (int i) {yTo = i;}
		private void setMoveNumber (int i) {moveNumber = i;}
		public int getXFrom() {return xFrom;}
		public int getYFrom() {return yFrom;}
		public int getXAdj() {return xAdj;}
		public int getYAdj() {return yAdj;}
		public int getXTo() {return xTo;}
		public int getYTo() {return yTo;}
		private int getMoveNumber() {return moveNumber;}
/*
		public Peg getFromPeg() {return new Peg(xFrom,yFrom);}
		public Peg getAdjPeg() {return new Peg(xAdj,yAdj);}
		public Peg getToPeg() {return new Peg(xTo,yTo);}
*/		
		public String stringMove() {
			String str = "(" + xFrom + "," + yFrom + "), (" + xAdj + "," + yAdj +
			") ,(" + xTo + "," + yTo + ") ,"+moveNumber;
			return str;
		}
	}

	private class ItemsTable {
		private int ItemEntry;
		private Item itemlist[];
		private ItemsTable () {
			ItemEntry = CurrentEntry;
			itemlist = new Item[ItemEntry+1];
			for (int i=0; i<=ItemEntry; i++) {
				itemlist[i] = new Item(movelist[i].getXFrom(),movelist[i].getYFrom(),
				movelist[i].getXTo(),movelist[i].getYTo(),
				movelist[i].getMoveNumber());
			}
		}
		public int getItemsEntry() {return ItemEntry;}
		private class Item {
			private int xFrom;
			private int yFrom;
			private int xTo;
			private int yTo;
			int move;
			private Item (int xFrom, int yFrom, int xTo, int yTo, int move) {
				this.xFrom = xFrom;
				this.yFrom = yFrom;
				this.xTo = xTo;
				this.yTo = yTo;
				this.move = move;
			}
			public int getXFrom() {return xFrom;}
			public int getYFrom() {return yFrom;}
			public int getXTo() {return xTo;}
			public int getYTo() {return yTo;}
			public int getMove() {return move;}
			public String stringItem() {
				String str = "(" + xFrom + "," + yFrom + ") ,(" + xTo + "," + yTo + ") ,"+move;
				return str;
			}
		}
	}

	public void addFailedList () {
		ItemsTable mTable;
		if (CurrentEntry >= 0 && CurrentEntry < MAXMOVES) {
			Debug.println("Adding to FailedList; CurrentEntry="+(CurrentEntry));
			mTable = new ItemsTable();
			vBadMovesList.addElement(mTable);
		}
	}
	public void listAllFailedLists(String msg) {
		ItemsTable lTable;
		int nItems = vBadMovesList.size();
		Debug.println(">>> Listing failed moves lists; Total = "+nItems);
		Debug.println(msg);
		for (int i=0; i<nItems; i++) {
			lTable = (ItemsTable) vBadMovesList.elementAt(i);
			Debug.println("Listing failed moves list: "+i);
			for (int j=0; j<=lTable.getItemsEntry(); j++) {
				Debug.println(j+": "+lTable.itemlist[j].stringItem());
			}
			Debug.println("Listed failed moves list");
		}
		Debug.println("<<<Listing failed moves lists;");
	}
	public boolean isListInBadList() {
		boolean bFound = false;
		ItemsTable lTable;
		int nItems = vBadMovesList.size();
		Debug.println(">>> isListInBadList; Total = "+nItems);
		for (int i=0; i<nItems; i++) {
			lTable = (ItemsTable) vBadMovesList.elementAt(i);
			Debug.println("Checking moves set #"+i);
			if (compareLists(lTable)) {
				System.out.println("Found in badList");
				bFound = true;
				break;
			}
		}
		Debug.println("<<< isListInBadList; Found = "+bFound);
		return bFound;
	}
	private boolean compareLists (ItemsTable lTable) {
		int nItems = lTable.getItemsEntry();
		Debug.println("CompareLists: CurrentEntry "+CurrentEntry+" nItems "+nItems);
		if (nItems != CurrentEntry) return false;
		for (int i=0; i<=nItems; i++) {
			if (lTable.itemlist[i].getXFrom() != movelist[i].getXFrom()) return false;
			if (lTable.itemlist[i].getYFrom() != movelist[i].getYFrom()) return false;
			if (lTable.itemlist[i].getXTo() != movelist[i].getXTo()) return false;
			if (lTable.itemlist[i].getYTo() != movelist[i].getYTo()) return false;
			if (lTable.itemlist[i].getMove() != movelist[i].getMoveNumber()) return false;
		}
		return true;
	}
}
