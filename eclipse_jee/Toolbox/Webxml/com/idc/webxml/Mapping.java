package com.idc.webxml;

public class Mapping {
	private String name;
	private String url;
	public String getName() {return name;}
	public String getUrl() {return url;}
	public void setName (String name) {this.name = name;}
	public void setUrl (String url) {this.url = url;}
	public void show() {
		System.out.println("Name "+name);
		System.out.println("url "+url);
	}
}
