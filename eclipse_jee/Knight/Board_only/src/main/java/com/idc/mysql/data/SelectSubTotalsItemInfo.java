package com.idc.mysql.data;

import java.io.Serializable;

public class SelectSubTotalsItemInfo implements Serializable {
	private static final long serialVersionUID = -5611265776624922127L;

	private long id;
	private long solutions;
	private long totalMoves;
	private long timing;

	public SelectSubTotalsItemInfo (long id, long solutions, long totalMoves, long timing) {
		this.id = id;
		this.solutions = solutions;
		this.totalMoves = totalMoves;
		this.timing = timing;
	}

	public long getId() {return id;}
	public long getSolutions() {return solutions;}
	public long getTotalMoves() {return totalMoves;}
	public long getTiming() {return timing;}

	public String toString() {
		return "("+getId()+","+getSolutions()+","+getTotalMoves()+","+getTiming()+")";
	}
}
