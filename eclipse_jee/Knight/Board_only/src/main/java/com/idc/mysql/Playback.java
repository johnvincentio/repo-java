package com.idc.mysql;

import java.util.Iterator;

import com.idc.knight.Board;
import com.idc.knight.Pair;
import com.idc.mysql.data.SelectMovesInfo;
import com.idc.mysql.data.SelectMovesItemInfo;
import com.idc.mysql.data.SelectSubTotalsInfo;
import com.idc.mysql.data.SelectTotalsItemInfo;

public class Playback {
	private DBMySQL m_dbMySQL = new DBMySQL();

	public static void main (String[] args) {
		(new Playback()).doTest();
	}
	private void doTest() {
		long id = 1402088571767L;
		SelectTotalsItemInfo selectTotalsItemInfo = m_dbMySQL.getSelectTotals (id);
		System.out.println("selectTotalsItemInfo "+selectTotalsItemInfo);
		SelectSubTotalsInfo selectSubTotalsInfo = m_dbMySQL.getSelectSubTotals (id);
		System.out.println("selectSubTotalsInfo "+selectSubTotalsInfo);
		SelectMovesInfo selectMovesInfo = m_dbMySQL.getSelectMoves (id, 1);
		System.out.println("selectMovesInfo "+selectMovesInfo);
		m_dbMySQL.close();

		try {
			Pair size = new Pair (selectTotalsItemInfo.getSizeXpos(), selectTotalsItemInfo.getSizeYpos());
			Board board = new Board();				// create the board
			board.setBoard (size);					// set board size

			int move_away_type = 1;
			Iterator<SelectMovesItemInfo> iter = selectMovesInfo.getItems();
			while (iter.hasNext()) {
				SelectMovesItemInfo item = iter.next();
				board.printBoard ("item "+item);
				if (item.getMoveCounter() == 0) {
					board.addFirstMove (item.getMoveCounter(), new Pair (item.getXpos(), item.getYpos()));		// add the initial position
				}
				else {
					board.moveForward (new Pair (item.getFromXpos(), item.getFromYpos()), move_away_type, new Pair());		// use move_away_type from the previous move
				}
				move_away_type = item.getMoveAwaytype();
			}
			board.printBoard ("Complete");
		}
		catch (Exception ex) {
			System.err.println ("Exception; "+ex.getMessage());
		}
	}
}
