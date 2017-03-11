package com.idc.esp.main;

import java.util.Iterator;

import com.idc.esp.data.AccountDataInfo;
import com.idc.esp.data.AccountDataItemInfo;
import com.idc.esp.data.MemberDataInfo;
import com.idc.esp.data.MemberDataItemInfo;
import com.idc.esp.data.MemberProfileInfo;
import com.idc.esp.data.MemberProfileItemInfo;
import com.idc.esp.data.ReportInfo;
import com.idc.esp.data.ReportItemInfo;

public class Builder {

	public ReportInfo doBuilder (MemberProfileInfo allMemberProfileInfo, MemberDataInfo allMemberDataInfo) {
		System.out.println (">>> Builder::doBuilder; profiles "+allMemberProfileInfo.getSize()+" memberAccounts "+allMemberDataInfo.getSize());
		ReportInfo reportInfo = new ReportInfo();

		Iterator<MemberProfileItemInfo> iter = allMemberProfileInfo.getSortedItems();
		while (iter.hasNext()) {
			MemberProfileItemInfo item = iter.next();
			if (item == null) continue;
			String username = item.getUsername();
			MemberDataInfo memberDataInfo = allMemberDataInfo.getItems (username);
			ReportItemInfo reportItemInfo = new ReportItemInfo (item, memberDataInfo);
			reportInfo.add (reportItemInfo);
		}
		System.out.println ("<<< Builder::doBuilder; reportInfo "+reportInfo.getSize());
		return reportInfo;
	}

	public ReportInfo doBuilderAccounts (ReportInfo reportInfo, AccountDataInfo allAccountDataInfo) {
		System.out.println (">>> Builder::doBuilderAccounts; reportInfo "+reportInfo.getSize()+" Accounts "+allAccountDataInfo.getSize());
		Iterator<ReportItemInfo> iter1 = reportInfo.getItems();
		while (iter1.hasNext()) {
			ReportItemInfo reportItemInfo = iter1.next();
			if (reportItemInfo == null) continue;

			MemberDataInfo memberDataInfo = reportItemInfo.getMemberDataInfo();
			Iterator<MemberDataItemInfo> iter2 = memberDataInfo.getItems();
			while (iter2.hasNext()) {
				MemberDataItemInfo memberDataItemInfo = iter2.next();
				if (memberDataItemInfo == null) continue;
				AccountDataItemInfo accountDataItemInfo = allAccountDataInfo.getAccount (memberDataItemInfo.getAccountid());
				if (accountDataItemInfo == null) continue;
				reportItemInfo.addAccount (accountDataItemInfo.getAccount());

				if (accountDataItemInfo.getAccountType() != 3) continue;
				long narpid = accountDataItemInfo.getNarpAccountId();
				if (narpid < 1) continue;
				AccountDataItemInfo narpAccountDataItemInfo = allAccountDataInfo.getAccount (narpid);
				if (narpAccountDataItemInfo == null) continue;
				reportItemInfo.addNarp (narpAccountDataItemInfo.getAccount());
			}
		}
		System.out.println ("<<< Builder::doBuilderAccounts; reportInfo "+reportInfo.getSize());
		return reportInfo;
	}
}
