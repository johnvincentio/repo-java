package com.idc.esp.main;

import java.io.File;

import com.idc.esp.data.AccountDataInfo;
import com.idc.esp.data.MemberDataInfo;
import com.idc.esp.data.MemberProfileInfo;
import com.idc.esp.data.ReportInfo;

/*
jira449_1.csv:
select narp.accountid, narp.ACCOUNT, narp.countryCode, narp.ACCOUNTTYPE, narp.narpAccountId from hercdb.CompanyAccounts narp where narp.ACCOUNTTYPE = 2;

jira449_2.csv:
select narp.accountid, narp.ACCOUNT, narp.countryCode, narp.ACCOUNTTYPE, 
ca.accountid, ca.ACCOUNT, ca.countryCode, ca.ACCOUNTTYPE, ca.narpAccountId 
from hercdb.CompanyAccounts narp, hercdb.CompanyAccounts ca 
where narp.ACCOUNTTYPE = 2 
and ca.narpAccountId = narp.accountid 
order by narp.accountid, ca.accountid;

jira449_3.csv:
select m.username, m.memberid, ma.accountid 
from HERCDB.MEMBERS m, HERCDB.MEMBERACCOUNTS ma 
where m.memberid = ma.memberid 
order by m.username;

jira449_4.csv:
select accountid, approved, active, account, countryCode, accountType, narpAccountId 
from hercdb.CompanyAccounts 
order by accountid;

*/

/*
"2804376 4271363 9847003"

"macosta@pompanomasonry.com",200000200206155757,200000400206155757
accountid = 200000400206155757
is a Narp member
200000300206155757,"1667",1,2,200000400206155757,"2804376",1,3,200000300206155757
200000400206155757,"A","A","2804376",1,3,200000300206155757
Narp is 1667

"macosta@pompanomasonry.com",200000200206155757,200000500206155757
accountid = 200000500206155757
is a Narp member
200000500206155757,"A","A","4271363",1,3,200000300206155757
200000300206155757,"A","A","1667",1,2,0
Narp is 1667

"macosta@pompanomasonry.com",200000200206155757,200000600206155757
accountid = 200000600206155757
200000600206155757,"A","A","9847003",1,3,200000300206155757
is a narp member
200000300206155757 
200000300206155757,"A","A","1667",1,2,0
narp is 1667

*/

public class StatisticsMain {
	private static final String LDAP_FILE = "input.ldif" ;
	private static final String MEMBERS_FILE = "jira449_3.csv";
	private static final String ACCOUNTS_FILE = "jira449_4.csv";
	private static final String REPORT_FILE = "report.csv";
	
	public static void main (String args[]) {
		(new StatisticsMain()).doTask1 ("C:\\work\\esp_statistics\\101");
	}

	private void doTask1 (String workDir) {
		System.out.println(">>> StatisticsMain::doTask1");
		LDAPReader ldapReader = new LDAPReader (new File (workDir + "\\" + LDAP_FILE));
		MemberProfileInfo allMemberProfileInfo = ldapReader.readFile();

		MembersReader membersReader = new MembersReader (new File (workDir + "\\" + MEMBERS_FILE));
		MemberDataInfo allMemberDataInfo = membersReader.readFile();

		AccountsReader accountsReader = new AccountsReader (new File (workDir + "\\" + ACCOUNTS_FILE));
		AccountDataInfo allAccountDataInfo = accountsReader.readFile();

		Builder builder = new Builder();
		ReportInfo reportInfo = builder.doBuilder (allMemberProfileInfo, allMemberDataInfo);
		reportInfo = builder.doBuilderAccounts (reportInfo, allAccountDataInfo);

		Reports reports = new Reports (new File (workDir + "\\" + REPORT_FILE));
		reports.createReport (reportInfo);
		System.out.println("<<< StatisticsMain::doTask1");
	}
}
