package com.idc.esp.data;

public class AccountItemInfo {
	private String account;

	public AccountItemInfo (String account) {
		this.account = account;
	}
	public String getAccount() {return account;}
	public String toString() {
		return "("+getAccount()+")";
	}
}
