package com.idc.esp.data;

public class ReportItemInfo {
	private MemberProfileItemInfo memberProfileItemInfo;
	private MemberDataInfo memberDataInfo;
	private AccountInfo accountInfo = new AccountInfo();
	private NarpInfo narpInfo = new NarpInfo();

	public ReportItemInfo (MemberProfileItemInfo memberProfileItemInfo, MemberDataInfo memberDataInfo) {
		this.memberProfileItemInfo = memberProfileItemInfo;
		this.memberDataInfo = memberDataInfo;
	}

	public MemberProfileItemInfo getMemberProfileItemInfo() {return memberProfileItemInfo;}
	public MemberDataInfo getMemberDataInfo() {return memberDataInfo;}
	public AccountInfo getAccountInfo() {return accountInfo;}
	public NarpInfo getNarpInfo() {return narpInfo;}

	public void addAccount (String account) {
		accountInfo.add (new AccountItemInfo(account));
	}
	public void addNarp (String account) {
		narpInfo.add (new NarpItemInfo(account));
	}

	public String toString() {
		return "("+getMemberProfileItemInfo()+","+getMemberDataInfo()+","+getAccountInfo()+","+getNarpInfo()+")";
	}
}
