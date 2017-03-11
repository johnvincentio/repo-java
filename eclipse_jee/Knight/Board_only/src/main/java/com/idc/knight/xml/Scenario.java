package com.idc.knight.xml;

import java.util.Date;

import com.idc.knight.Pair;

public class Scenario {
	private Pair size;
	private Pair start;
	private long startTime;
	private long endTime;
	private long solutions;
	private long totalMoves;
	private long elapsedTime;
	public Scenario (Pair size, Pair start) {
		this.size = size;
		this.start = start;
		startTime = (new Date()).getTime();
	}

	public Pair getSize() {return size;}
	public Pair getStart() {return start;}
	public long getStartTime() {return startTime;}
	public long getEndTime() {return endTime;}
	public long getSolutions() {return solutions;}
	public long getTotalMoves() {return totalMoves;}
	public long getElapsedTime() {return elapsedTime;}

	public void setEndTime() {
		this.endTime = (new Date()).getTime();
		this.elapsedTime = endTime - startTime;
	}
	public void setSolutions(long solutions) {this.solutions = solutions;}
	public void setTotalMoves(long totalMoves) {this.totalMoves = totalMoves;}
}
