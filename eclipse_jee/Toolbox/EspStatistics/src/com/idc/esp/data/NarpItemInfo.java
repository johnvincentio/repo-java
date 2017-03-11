package com.idc.esp.data;

public class NarpItemInfo {
	private String account;

	public NarpItemInfo (String account) {
		this.account = account;
	}
	public String getAccount() {return account;}
	public String toString() {
		return "("+getAccount()+")";
	}
}
