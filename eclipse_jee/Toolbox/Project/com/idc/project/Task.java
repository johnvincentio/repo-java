package com.idc.project;

public class Task {
	private String uid;
	private String id;
	private String task;
	private int origDur;
	private int remDur;
	private int percentComplete;
	private String owner;
	private String start;
	private String finish;
	private int outline;
	private Preds predlist = new Preds();
	
	private boolean active = true;
	private boolean summary = false;

	public Task() {}

	public String getUid() {return uid;}
	public String getId() {return id;}
	public String getTask() {return task;}
	public int getOrigDur() {return origDur;}
	public int getRemDur() {return remDur;}
	public int getPercentComplete() {return percentComplete;}
	public String getOwner() {return owner;}
	public String getStart() {return start;}
	public String getFinish() {return finish;}
	public int getOutline() {return outline;}
	
	public boolean isActive() {return active;}
	public boolean isSummary() {return summary;}
	
	public void setUid (String s) {this.uid = s;}
	public void setId (String id) {this.id = id;}
	public void setTask (String task) {this.task = task;}
	public void setOrigDur (int origDur) {this.origDur = origDur;}
	public void setRemDur (int remDur) {this.remDur = remDur;}
	public void setPercentComplete (int percentComplete) {this.percentComplete = percentComplete;}
	public void setOwner (String s) {this.owner = s;}
	public void setStart (String start) {this.start = start;}
	public void setFinish (String finish) {this.finish = finish;}
	public void setOutline (int n) {this.outline = n;}

	public void setInactive() {active = false;}
	public void setSummary() {summary = true;}

	public void incrOrigDur (int incr) {this.origDur = origDur + incr;}
	public void incrRemDur (int incr) {this.remDur = remDur + incr;}

	public Preds getPredlist() {return predlist;}
	public void show() {
		if (isActive())
			System.out.println("("+getUid()+","+getId()+","+getTask()+
				","+getOrigDur()+","+getRemDur()+","+getPercentComplete()+
				","+getOwner()+","+getStart()+","+getFinish()+","+getOutline()+
				","+isSummary()+")");
		predlist.show();
	}
}
