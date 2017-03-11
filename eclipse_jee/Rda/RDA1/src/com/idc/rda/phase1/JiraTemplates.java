package com.idc.rda.phase1;

import com.idc.rda.phase1.items.DefInfo;
import com.idc.rda.phase1.items.DefItemInfo;

public class JiraTemplates {

	public static DefInfo TestgetDefInfo () {
		DefInfo defInfo = new DefInfo();
		defInfo.add (new DefItemInfo ("Header", 2, 'B', 6, 'C'));
		defInfo.add (new DefItemInfo ("Vendor Change", 2, 'B', 12, 'D'));
		return defInfo;
	}
	public static DefInfo getDefInfo () {
		DefInfo defInfo = new DefInfo();
		defInfo.add (new DefItemInfo ("Header", 2, 'B', 6, 'C'));
		defInfo.add (new DefItemInfo ("Vendor Change", 2, 'B', 12, 'D'));
		defInfo.add (new DefItemInfo ("Irac Framework Change", 2, 'B', 9, 'D'));
		defInfo.add (new DefItemInfo ("Etrieve Change", 2, 'B', 5, 'D'));
		defInfo.add (new DefItemInfo ("Herc Required Server Change", 2, 'B', 6, 'D'));
		defInfo.add (new DefItemInfo ("Herc Required Firewall Change", 2, 'B', 6, 'D'));
		defInfo.add (new DefItemInfo ("Herc Required Interwoven Change", 2, 'B', 5, 'D'));
		defInfo.add (new DefItemInfo ("Herc Required LDAP Change", 2, 'B', 6, 'D'));
		defInfo.add (new DefItemInfo ("Herc Required Database Change", 2, 'B', 13, 'D'));
		defInfo.add (new DefItemInfo ("Herc Change to Vendor", 2, 'B', 12, 'D'));
		defInfo.add (new DefItemInfo ("Type of Herc change", 2, 'B', 11, 'D'));
		defInfo.add (new DefItemInfo ("Functionality", 2, 'B', 12, 'C'));
		defInfo.add (new DefItemInfo ("Component", 2, 'B', 17, 'C'));
		defInfo.add (new DefItemInfo ("Factors", 2, 'B', 7, 'D'));
		defInfo.add (new DefItemInfo ("Project Factors", 2, 'B', 12, 'E'));
		defInfo.add (new DefItemInfo ("Pre Development", 2, 'B', 5, 'E'));
		defInfo.add (new DefItemInfo ("Development", 2, 'B', 22, 'E'));
		defInfo.add (new DefItemInfo ("Deployment", 2, 'B', 8, 'E'));
		defInfo.add (new DefItemInfo ("Totals", 2, 'B', 5, 'E'));
		defInfo.add (new DefItemInfo ("High Level Estimates", 2, 'B', 7, 'E'));
		defInfo.add (new DefItemInfo ("Detailed Estimates", 2, 'B', 7, 'E'));
		defInfo.add (new DefItemInfo ("Architect", 2, 'B', 6, 'E'));
		defInfo.add (new DefItemInfo ("Deliverables", 4, 'B', 50, 'F'));
		defInfo.add (new DefItemInfo ("Comments", 2, 'B', 50, 'B'));
		return defInfo;
	}
}
