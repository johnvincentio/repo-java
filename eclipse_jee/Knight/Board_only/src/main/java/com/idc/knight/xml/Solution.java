package com.idc.knight.xml;

import com.idc.knight.Board;
import com.idc.knight.Pair;

public class Solution {
	private long solution;
	private Pair size;
	private Pair start;

	private Board board;
	private long currentSolutionTotalMoves;
	private long totalMoves;
	private long timing;

	public Solution (long solution, Pair size, Pair start, Board board, long currentSolutionTotalMoves, long totalMoves, long timing) {
		this.solution = solution;
		this.size = size;
		this.start = start;
		this.board = board;
		this.currentSolutionTotalMoves = currentSolutionTotalMoves;
		this.totalMoves = totalMoves;
		this.timing = timing;
	}

	public long getSolution() {return solution;}
	public String getStringSolution() {return Long.toString(solution);}
	public Pair getSize() {return size;}
	public Pair getStart() {return start;}
	public Board getBoard() {return board;}
	public long getCurrentSolutionTotalMoves() {return currentSolutionTotalMoves;}
	public long getTotalMoves() {return totalMoves;}
	public long getTiming() {return timing;}

	public int getMoveCounter (int row, int col) {
		int move_counter = -1;
		try {
			move_counter = getBoard().getMoveCounter (new Pair (row, col));
		}
		catch (Exception ex) {}
		return move_counter;
	}
}
