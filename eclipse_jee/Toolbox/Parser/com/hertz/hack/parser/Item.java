package com.hertz.hack.parser;

public class Item {
	String time;
	String ip;
	String data;
	public Item (String s1, String s2, String s3) {
		time = s1;
		ip = s2;
		data = s3;
	}
	public Item (Item item) {
		time = item.getTime();
		ip = item.getIp();
		data = item.getData();
	}
	public String getTime() {return time;}
	public String getIp() {return ip;}
	public String getData() {return data;}
	public String toString() {
		return "("+getTime() + "," + getIp()+","+getData()+")";
	}
	public String show() {
		return "("+getTime() + "," + getIp()+","+getData()+")";
	}
}
