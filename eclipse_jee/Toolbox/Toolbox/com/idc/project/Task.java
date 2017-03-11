package com.idc.project;

public class Task {
	private String uid;
	private String id;
	private String task;
	private String dur;
	private String owner;
	private String start;
	private String finish;
	private String outline;
	private Preds predlist = new Preds();

	public Task() {}

	public String getUid() {return uid;}
	public String getId() {return id;}
	public String getTask() {return task;}
	public String getDur() {return dur;}
	public String getOwner() {return owner;}
	public String getStart() {return start;}
	public String getFinish() {return finish;}
	public String getOutline() {return outline;}
	
	public void setUid (String s) {this.uid = s;}
	public void setId (String id) {this.id = id;}
	public void setTask (String task) {this.task = task;}
	public void setDur (String dur) {this.dur = dur;}
	public void setOwner (String s) {this.owner = s;}
	public void setStart (String start) {this.start = start;}
	public void setFinish (String finish) {this.finish = finish;}
	public void setOutline (String s) {this.outline = s;}

	public Preds getPredlist() {return predlist;}
	public void show() {
		System.out.println("("+getUid()+","+getId()+","+getTask()+","+getDur()+
				","+getOwner()+","+getStart()+","+getFinish()+","+getOutline()+")");
		predlist.show();
	}
}
