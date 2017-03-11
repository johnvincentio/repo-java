package com.idc.webxml;

public class Servlet {
	private String name;
	private String servlet;
	private String sclass;
	public String getName() {return name;}
	public String getServlet() {return servlet;}
	public String getSclass() {return sclass;}
	public void setName (String name) {this.name = name;}
	public void setServlet (String servlet) {this.servlet = servlet;}
	public void setSclass (String sclass) {this.sclass = sclass;}
	public void show() {
		System.out.println("Name :"+name+":");
		System.out.println("Servlet :"+servlet+":");
		System.out.println("Class :"+sclass+":");
	}
}
