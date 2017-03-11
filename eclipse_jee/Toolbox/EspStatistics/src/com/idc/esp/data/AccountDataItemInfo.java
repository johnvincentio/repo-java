package com.idc.esp.data;

public class AccountDataItemInfo {
	private long accountid;
	private String approved;
	private String active;
	private String account;
	private int countryCode;
	private int accountType;
	private long narpAccountId;

	public AccountDataItemInfo (long accountid, String approved, String active, String account, int countryCode, int accountType, long narpAccountId) {
		this.accountid = accountid;
		this.approved = approved;
		this.active = active;
		this.account = account;
		this.countryCode = countryCode;
		this.accountType = accountType;
		this.narpAccountId = narpAccountId;
	}

	public long getAccountid() {return accountid;}
	public String getApproved() {return approved;}
	public String getActive() {return active;}
	public String getAccount() {return account;}
	public int getCountryCode() {return countryCode;}
	public int getAccountType() {return accountType;}
	public long getNarpAccountId() {return narpAccountId;}

	public String toString() {
		return "("+getAccountid()+","+getApproved()+","+getActive()+","+getAccount()+","+getCountryCode()+","+getAccountType()+","+getNarpAccountId()+")";
	}
}
