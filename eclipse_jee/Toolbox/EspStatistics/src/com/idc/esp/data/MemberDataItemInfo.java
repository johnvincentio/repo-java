package com.idc.esp.data;

public class MemberDataItemInfo {
	private String username;
	private long memberid;
	private long accountid;

	public MemberDataItemInfo (String username, long memberid, long accountid) {
		this.username = username;
		this.memberid = memberid;
		this.accountid = accountid;
	}

	public String getUsername() {return username;}
	public long getMemberid() {return memberid;}
	public long getAccountid() {return accountid;}

	public String toString() {
		return "("+getUsername()+","+getMemberid()+","+getAccountid()+")";
	}
}
