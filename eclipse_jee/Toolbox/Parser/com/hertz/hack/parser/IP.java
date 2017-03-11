package com.hertz.hack.parser;

public class IP {
	private String ip;
	public IP (String s1) {
		ip = s1;
	}
	public String getIp() {return ip;}
	public String toString() {
		return "("+ getIp()+")";
	}
}
