/********************************************************************
*			Copyright (c) 2006 The Hertz Corporation				*
*			  All Rights Reserved.  (Unpublished.)					*
*																	*
*		The information contained herein is confidential and		*
*		proprietary to The Hertz Corporation and may not be			*
*		duplicated, disclosed to third parties, or used for any		*
*		purpose not expressly authorized by it.  Any unauthorized	*
*		use, duplication, or disclosure is prohibited by law.		*
*																	*
*********************************************************************/

package com.idc.esp.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author John Vincent
 *
 * encapsulate member report flags
 */

public class MemberReports implements Serializable {
	private static final long serialVersionUID = 6843585355254912842L;
	private static final transient int MAX_REPORTS = 99;
	private StringBuffer reportFlags = new StringBuffer();
/*
 * Constructor used to build a MemberReports object for a new Member. The object will have all reports
 * set to disallowed.
 */
	public MemberReports() {
		this("");
		// New user set EquipmentPoint to Yes
		this.setAllowed(34);
	}
/*
 * Constructor used to build a MemberReports object from the object retrieved from
 * the data store.
 */
	public MemberReports (String flags) {
		reportFlags.append(flags.toUpperCase()).append(makeMax());
	}
/*
 * Constructor used to build the MemberReports object from a StringMap object.
 * This is commonly used throughout the application as a presentation layer class.
 * The Report Options selected by a user will be retrieved from the FormInfo object as
 * a StringMap, which extends HashMap.
 */
	public MemberReports (HashMap<String, Object> map) {	// used for getting flags from forminfo
		reportFlags = makeMax();
		String tmp;
		for (int i=1; i<=MAX_REPORTS; i++) {
			tmp = (String) map.get(Integer.toString(i));
			if (tmp == null) continue;
			if (tmp.length() < 1) continue;
			if (tmp.trim().equalsIgnoreCase("on"))
				setAllowed(i-1);
		}
	}
	
	/**
	 * Constructor Used from AT1.5.1. Uses StringMap for reports and boolean for KPI report.
	 * @param map		HashMap of reports
	 * @param bKpi		Kpi report on/off
	 */
	public MemberReports (HashMap<String, Object> map, boolean bKpi) {	// used for getting flags from forminfo
		reportFlags = makeMax();
		String tmp;
		for (int i=1; i<=MAX_REPORTS; i++) {
			if (i == 13) { if(bKpi) setAllowed(i-1); else setDisallowed(i-1); };
			tmp = (String) map.get(Integer.toString(i));
			if (tmp == null) continue;
			if (tmp.length() < 1) continue;
			if (tmp.trim().equalsIgnoreCase("on"))
				setAllowed(i-1);
		}
	}
	
	/**
	 * Constructor Used from AT1.4.2. Uses StringMap for reports and boolean for KPI report, and boolean for equipment point.
	 * @param map		HashMap of reports
	 * @param bKpi		Kpi report on/off
	 * @param boolean	equipment point
	 */
	public MemberReports (HashMap<String, Object> map, boolean bKpi, boolean equipmentPoint) {	// used for getting flags from forminfo
		reportFlags = makeMax();
		String tmp;
		for (int i=1; i<=MAX_REPORTS; i++) {
			if (i == 13) { if(bKpi) setAllowed(i-1); else setDisallowed(i-1); };
			if (i == 35) { if(equipmentPoint) setAllowed(i-1); else setDisallowed(i-1); };
			tmp = (String) map.get(Integer.toString(i));
			if (tmp == null) continue;
			if (tmp.length() < 1) continue;
			if (tmp.trim().equalsIgnoreCase("on"))
				setAllowed(i-1);
		}
	}
/*
 * Constructor used for M122, update member.
 * The HashMap are the values returned from the form (formInfo).
 * ApproverReports is the report object of the approver.
 * PreviousReports is the current report object of the member whose report is to be changed.
 * The resulting MemberReports object will be the previousReports with changes made for the following condition:
 * 		If approverRole flag is set, then resulting role flag is the appropriate boolean flag, thus the value from the form.
 * The resulting MemberReports object will be constructed from the form values, with changes made for the following condition:
 * 		If approverReports flag is not set, then resulting reports flag is the previousReports flag.
 */
	public MemberReports (HashMap<String, Object> map, MemberReports approverReports, MemberReports previousReports) {
		this (map);
		for (int i=0; i<=MAX_REPORTS; i++) {
			if (! approverReports.isAllowed(i)) setFlag (i, previousReports.isAllowed(i));
		}
	}
	
	/**
	 * Constructor used to update the report flag using MemberReports and reportIndex.
	 * 
	 * @param memberReports			MemberReports
	 * @param n 					report Index		
	 */
	public MemberReports (MemberReports memberReports, int n) {	
		this(memberReports.reportFlags.toString());
		this.setFlag (n, true);		
	}

/*
 * Method for writing the Member Reports information back to the datastore.
 */
	public String toDatabase() {return reportFlags.substring(0,MAX_REPORTS).toString();}

	public int countAllowed() {
		int allowed = 0;
		for (int i=0; i<=MAX_REPORTS; i++) {
			if (isAllowed(i)) allowed++;
		}
		return allowed;
	}
	public boolean isAllowed (int n) {
		if (n < 0 || n > MAX_REPORTS) return false;
		if (reportFlags.substring(n,n+1).equals("Y")) return true;
		return false;
	}
	private void setAllowed (int num) {reportFlags.replace(num, num+1, "Y");}
	private void setDisallowed (int num) {reportFlags.replace(num, num+1, "N");}
	private void setFlag (int num, boolean allowed) {
		if (allowed)
			setAllowed(num);
		else
			setDisallowed(num);
	}

	private StringBuffer makeMax() {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<MAX_REPORTS; i++) buf.append('N');
		return buf;
	}
	
	public String toString() {return "("+reportFlags+")";}
}
