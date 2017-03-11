package com.idc.project;

public class Project {
	private String name;
	private String title;
	private String company;
	private String creationdate;
	private String lastsaved;
	private String startdate;
	private String finishdate;
	private Tasks tasks = new Tasks();

	public String getName() {return name;}
	public String getTitle() {return title;}
	public String getCompany() {return company;}
	public String getCreationdate() {return creationdate;}
	public String getLastsaved() {return lastsaved;}
	public String getStartdate() {return startdate;}
	public String getFinishdate() {return finishdate;}

	public void setName (String name) {this.name = name;}
	public void setTitle (String title) {this.title = title;}
	public void setCompany (String company) {this.company = company;}
	public void setCreationdate (String creationdate) {this.creationdate = creationdate;}
	public void setLastsaved (String lastsaved) {this.lastsaved = lastsaved;}
	public void setStartdate (String startdate) {this.startdate = startdate;}
	public void setFinishdate (String finishdate) {this.finishdate = finishdate;}
	public String toString() {
		return "("+getName()+","+getTitle()+","+getCompany()+","+getCreationdate()+","+getLastsaved()+","+getStartdate()+","+getFinishdate()+")";
	}
		 
	public Tasks getTasks() {return tasks;}
		 
	public void show() {
		 System.out.println("Show project");
		 System.out.println(toString());
		 tasks.show();
	}
}

