package com.idc.mysql.data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class SelectTotalsItemInfo implements Serializable {
	private static final long serialVersionUID = 2914850045506979962L;

	private static final SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

	private long id;
	private int sizeXpos;
	private int sizeYpos;
	private int startXpos;
	private int startYpos;
	private long solutions;
	private long totalMoves;
	private Timestamp startDate;
	private long timing;

	public SelectTotalsItemInfo (long id, int sizeXpos, int sizeYpos, int startXpos, int startYpos, long solutions, long totalMoves, Timestamp startDate, long timing) {
		this.id = id;
		this.sizeXpos = sizeXpos;
		this.sizeYpos = sizeYpos;
		this.startXpos = startXpos;
		this.startYpos = startYpos;
		this.solutions = solutions;
		this.totalMoves = totalMoves;
		this.startDate = startDate;
		this.timing = timing;
	}

	public long getId() {return  id;}
	public Timestamp getStartDate() {return startDate;}
	public int getSizeXpos() {return sizeXpos;}
	public int getSizeYpos() {return sizeYpos;}
	public int getStartXpos() {return startXpos;}
	public int getStartYpos() {return startYpos;}
	public long getSolutions() {return solutions;}
	public long getTotalMoves() {return totalMoves;}
	public long getTiming() {return timing;}

	public String toString() {
		return "("+id+","+getSizeXpos()+","+getSizeYpos()+","+getStartXpos()+","+getStartYpos()+","+getSolutions()+","+getTotalMoves()+sdf.format(startDate)+","+getTiming()+")";
	}
}
