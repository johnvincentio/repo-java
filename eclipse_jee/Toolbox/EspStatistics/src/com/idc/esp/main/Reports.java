package com.idc.esp.main;

import java.io.File;
import java.util.Iterator;

import com.idc.esp.data.AccountInfo;
import com.idc.esp.data.MemberProfileItemInfo;
import com.idc.esp.data.NarpInfo;
import com.idc.esp.data.ReportInfo;
import com.idc.esp.data.ReportItemInfo;
import com.idc.file.JVFile;

public class Reports {
	private File m_dataFile;

	public Reports (File m_dataFile) {
		this.m_dataFile = m_dataFile;
	}

	public void createReport (ReportInfo reportInfo) {
		System.out.println (">>> Reports::createReport");
		JVFile reportFile = new JVFile (this.m_dataFile);
		reportFile.open();
		StringBuffer buf = new StringBuffer();		
		
		applyColumn (buf, "Username");
		applyColumn (buf, "FirstName");
		applyColumn (buf, "LastName");
		applyColumn (buf, "BusinessName");
		applyColumn (buf, "Title");
		applyColumn (buf, "Email");
		applyColumn (buf, "Address1");
		applyColumn (buf, "Address2");
		applyColumn (buf, "City");
		applyColumn (buf, "CountryState");
		applyColumn (buf, "Role");
		applyColumn (buf, "Active");
		applyColumn (buf, "Approved");
		applyColumn (buf, "Release");
		applyColumn (buf, "Extend");
		applyColumn (buf, "Narps");
		applyColumn (buf, "Accounts", true);
		reportFile.writeNL (buf.toString());
		reportFile.writeNL();

		int count = 0;
		int count_no_accounts = 0;
		Iterator<ReportItemInfo> iter = reportInfo.getItems();
		while (iter.hasNext()) {
			ReportItemInfo reportItemInfo = iter.next();
			if (reportItemInfo == null) continue;
			MemberProfileItemInfo memberProfileItemInfo = reportItemInfo.getMemberProfileItemInfo();
			AccountInfo accountInfo = reportItemInfo.getAccountInfo();
			NarpInfo narpInfo = reportItemInfo.getNarpInfo();

			if (accountInfo.getSize() < 1) {
				if (memberProfileItemInfo.isActive() && memberProfileItemInfo.isApproved()) {
					System.out.println ("user :"+memberProfileItemInfo.getUsername()+ ": has no accounts ");
					count_no_accounts++;
				}
			}
			
			buf = null;
			buf = new StringBuffer();
			applyColumn (buf, memberProfileItemInfo.getUsername());
			applyColumn (buf, memberProfileItemInfo.getFirstName());
			applyColumn (buf, memberProfileItemInfo.getLastName());
			applyColumn (buf, memberProfileItemInfo.getBusinessName());
			applyColumn (buf, memberProfileItemInfo.getTitle());
			applyColumn (buf, memberProfileItemInfo.getEmail());
			applyColumn (buf, memberProfileItemInfo.getAddress1());
			applyColumn (buf, memberProfileItemInfo.getAddress2());
			applyColumn (buf, memberProfileItemInfo.getCity());
			applyColumn (buf, memberProfileItemInfo.getCountryState());
			applyColumn (buf, memberProfileItemInfo.getRoleString());
			applyColumn (buf, memberProfileItemInfo.getReportsActiveString());
			applyColumn (buf, memberProfileItemInfo.getReportsApprovalString());
			applyColumn (buf, memberProfileItemInfo.isRelease());
			applyColumn (buf, memberProfileItemInfo.isExtend());
			applyColumn (buf, narpInfo.getAccounts());
			applyColumn (buf, accountInfo.getAccounts(), true);
			reportFile.writeNL (buf.toString());
			count++;
		}
		reportFile.close();
		System.out.println ("Users without accounts = "+count_no_accounts);
		System.out.println ("<<< Reports::createReport; records reported "+count);
	}

	public static void applyColumn (StringBuffer buf, String data) {
		applyColumn (buf, data, false);
	}

	public static void applyColumn (StringBuffer buf, boolean bool) {
		String data = bool ? "Yes" : "No";
		applyColumn (buf, data, false);
	}

	public static void applyColumn (StringBuffer buf, String data, boolean last) {
		String str = "";
		if (data != null) str = data.replaceAll("\"", "\"\"");
		buf.append ("\"").append (str).append("\"");
		if (! last) buf.append (",");
	}
}
