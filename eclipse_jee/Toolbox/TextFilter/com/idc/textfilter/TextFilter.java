package com.idc.textfilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idc.trace.LogHelper;

public class TextFilter {
	private TextFilterGUI m_app;
	public TextFilter (TextFilterGUI app) {m_app = app;}
	public boolean isExecutionStopped() {return m_app.getAppThread().getStopStatus();}

	private void handleProgressIndicator() {m_app.getAppInput().getProgressBar().setProgressBar();}
	private void addMessage () {m_app.getAppOutput().getMessagesArea().add();}
	private void addMessage (String msg) {m_app.getAppOutput().getMessagesArea().add(msg);}

	public void doTextFilter (Integer scenario, String text) {
		handleProgressIndicator();

		if (isExecutionStopped()) return;
		handleProgressIndicator();

		if (isExecutionStopped()) return;
		handleProgressIndicator();
		addMessage("Filtering text using "+scenario);

		if (scenario.intValue() == 1)
			doFilter1 (text);
		else if (scenario.intValue() == 2)
			doFilter2 (text);
		else if (scenario.intValue() == 3)
			doFilter3 (text);
		else if (scenario.intValue() == 4)
			doFilter4 (text);
		else if (scenario.intValue() == 5)
			doFilter5 (text);
		else if (scenario.intValue() == 6)
			doFilter6 (text);

		handleProgressIndicator();
		addMessage();
		handleProgressIndicator();
		LogHelper.info("exiting...");
	}

	private void doFilter1 (String text) {
		List<String[]> list = getList (text);
		for (Iterator<String[]> iter = list.iterator(); iter.hasNext(); ) {
			String[] sa = (String[]) iter.next();
			addMessage (sa[1]+" "+sa[3]);
		}
	}
	private void doFilter2 (String text) {
		List<String[]> list = getList (text);
		for (Iterator<String[]> iter = list.iterator(); iter.hasNext(); ) {
			String[] sa = (String[]) iter.next();
			addMessage ("select companyid, accountid, approved, active, accounttype, narpaccountid "+
					"from hercdb.companyaccounts "+
					"where account='"+sa[3]+"' and countrycode = 1;");
		}
	}
	private void doFilter3 (String text) {
		List<String[]> list = getList (text);
		for (Iterator<String[]> iter = list.iterator(); iter.hasNext(); ) {
			String[] sa = (String[]) iter.next();
			addMessage ("select companyid, accountid, approved, active, account, accounttype, narpaccountid "+
					"from hercdb.companyaccounts where account = '"+sa[3]+"' and narpaccountid = "+
					"(select accountid from hercdb.companyaccounts where account='"+sa[1]+"' and accounttype = 2 and countrycode = 1);");
		}
	}
	
	private void doFilter4 (String text) {
		List<String> list = getList2 (text);
		for (Iterator<String> iter = list.iterator(); iter.hasNext(); ) {
			String sa = (String) iter.next();
			addMessage (sa);
		}
	}
	private void doFilter5 (String text) {
		List<String[]> list = getList3 (text);
		for (Iterator<String[]> iter = list.iterator(); iter.hasNext(); ) {
			String[] sa = (String[]) iter.next();
			addMessage ("select companyid, accountid, approved, active, accounttype, narpaccountid "+
					"from hercdb.companyaccounts "+
					"where account='"+sa[0]+"' and countrycode = 1;");
			addMessage ("select companyid, accountid, approved, active, accounttype, narpaccountid "+
					"from hercdb.companyaccounts "+
					"where accountid="+sa[1]+" and countrycode = 1;");
		}
	}
	private void doFilter6 (String text) {
		List<String[]> list = getList4 (text);
		for (Iterator<String[]> iter = list.iterator(); iter.hasNext(); ) {
			String[] sa = (String[]) iter.next();
			addMessage ("select memberid, accountid, approved, approverid "+
					"from hercdb.memberaccounts "+
					"where memberid = "+sa[2]+" and accountid = "+sa[1]+";");
		}
	}


	private List<String[]> getList (String text) {
		List<String[]> list = new ArrayList<String[]>();
		String[] lines = text.split("\n");
		int count1 = lines.length;
		for (int num1=0; num1<count1; num1++) {
			String s1 = lines[num1].trim();
			if (s1.length() < 1) continue;
			int p1 = s1.indexOf("ADD");
			if (p1 < 0) continue;
			System.out.println("s1 :"+s1+":");
			int pe = s1.length();
			String s2 = s1.substring(p1+5);
			System.out.println("pe "+pe);
			System.out.println("s2 :"+s2+":");
			String s3 = s2.replace('\'', ' ').trim();
			String s4[] = s3.split(" ");
			list.add (s4);
		}
		return list;
	}

	/*
	No narp admins were found for Narp Local Account 2393229 Account id 1230034300103503865 Country 2 Company id 300242400073868341 Narp id 300242500073868341'                                                       
	*/
	private List<String> getList2 (String text) {
		List<String> list = new ArrayList<String>();
		String[] lines = text.split("\n");
		int count1 = lines.length;
		for (int num1=0; num1<count1; num1++) {
			String s1 = lines[num1].trim();
			if (s1.length() < 1) continue;
			int p1 = s1.indexOf("No narp admins were found");
			if (p1 < 0) continue;
			System.out.println("s1 :"+s1+":");
			int p2 = s1.indexOf("Narp id");
			String s2 = s1.substring(p2+7);
			System.out.println("s2 :"+s2+":");
			String s3 = s2.replace('\'', ' ').trim();
			if (! list.contains(s3)) list.add (s3);
		}
		return list;
	}

	/*
	Add Company Account :2846926: accountid 1230000100104878845'                                                       
	*/
	private List<String[]> getList3 (String text) {
		List<String[]> list = new ArrayList<String[]>();
		String[] lines = text.split("\n");
		int count1 = lines.length;
		for (int num1=0; num1<count1; num1++) {
			String s1 = lines[num1].replace(':', ' ').trim();
			if (s1.length() < 1) continue;
			int p2 = s1.indexOf("Add Company Account ");
			if (p2 < 0) continue;
			System.out.println("s1 :"+s1+":");
			int p3 = s1.indexOf("accountid");
			String s2 = s1.substring(p2+"Add Company Account ".length(), p3 - 1).trim();
			System.out.println("s2 :"+s2+":");
			String s3 = s1.substring(p3+9).replace('\'', ' ').trim();
			System.out.println("s3 :"+s3+":");
			String[] sa = {s2, s3};
			list.add (sa);
		}
		return list;
	}

	/*
	For Company Account 2846926: accountid 1230000100104878845 Add member account for memberid 300000100080871965'                                       
	 */
	private List<String[]> getList4 (String text) {
		List<String[]> list = new ArrayList<String[]>();
		String[] lines = text.split("\n");
		int count1 = lines.length;
		for (int num1=0; num1<count1; num1++) {
			String s1 = lines[num1].replace(':', ' ').replace('\'', ' ').trim();
			if (s1.length() < 1) continue;
			int p2 = s1.indexOf("For Company Account ");
			if (p2 < 0) continue;
			System.out.println("s1 :"+s1+":");
			int p3 = s1.indexOf("accountid");
			int p4 = s1.indexOf("Add member account for memberid");
			String s2 = s1.substring(p2+"For Company Account ".length(), p3 - 1).trim();	// account
			System.out.println("s2 :"+s2+":");
			String s3 = s1.substring(p3+9, p4 - 1).replace('\'', ' ').trim();						// accountid
			System.out.println("s3 :"+s3+":");
			String s4 = s1.substring(p4+"Add member account for memberid".length());		// memberid
			System.out.println("s4 :"+s4+":");
			String[] sa = {s2, s3, s4};
			list.add (sa);
		}
		return list;
	}
}
