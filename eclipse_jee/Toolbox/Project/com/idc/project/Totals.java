
package com.idc.project;


public class Totals {
	String uid;
	int origDur;
	int remDur;
	public Totals (String uid) {
		this.uid = uid;
		origDur = 0;
		remDur = 0;
	}
	public String getUid() {return uid;}
	public int getOrigDur() {return origDur;}
	public int getRemDur() {return remDur;}
	public void incrOrigDur(int num) {origDur += num;}
	public void incrRemDur(int num) {remDur += num;}
	public void reset() {origDur = remDur = 0;}
	public String toString() {
		return "("+getUid()+","+getOrigDur()+","+getRemDur()+")";
	}
}
