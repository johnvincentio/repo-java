package com.idc.webxml;

public class Webapp {
	private String display;
	private Servlets servlets;
	private Mappings mappings;
	private Welcome welcome;
	public void setDisplay (String s) {display = s;}
	public void setServlets (Servlets items) {servlets = items;}
	public void setMappings (Mappings items) {mappings = items;}
	public void setWelcome (Welcome items) {welcome = items;}
	public String getDisplay() {return display;}
	public Servlets getServlets() {return servlets;}
	public Mappings getMappings() {return mappings;}
	public Welcome getWelcome() {return welcome;}
	public void show() {
		System.out.println("Display "+display);
		if (servlets != null) servlets.show();
		if (mappings != null) mappings.show();
		if (welcome != null) welcome.show();
	}
}
