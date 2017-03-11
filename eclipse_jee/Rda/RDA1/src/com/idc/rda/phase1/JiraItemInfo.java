package com.idc.rda.phase1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.Utils;
import com.idc.rda.phase1.items.AlphaInfo;
import com.idc.rda.phase1.items.DefInfo;
import com.idc.rda.phase1.items.DefItemInfo;

public class JiraItemInfo implements Serializable {
	private static final long serialVersionUID = 1485001897373596984L;

	private String m_spreadSheetPath;
//	private HSSFWorkbook m_workbook;
	private int m_sheets;
	private DefInfo m_defInfo;
	private int m_jira;
	public String getSpreadSheetPath() {return m_spreadSheetPath;}
	public int getSheets() {return m_sheets;}
	public DefInfo getDefInfo() {return m_defInfo;}
	public int getJira() {return m_jira;}

	private ArrayList <AlphaInfo> list = new ArrayList <AlphaInfo>();
	public ArrayList <AlphaInfo> getList() {return list;}

	public JiraItemInfo (String spreadSheetPath, final HSSFWorkbook workbook) throws Exception  {
		m_spreadSheetPath = spreadSheetPath;
//		m_workbook = workbook;
		m_sheets = workbook.getNumberOfSheets();
//		handleHeader();
		m_defInfo = JiraTemplates.getDefInfo();
		m_jira = Utils.getValidatedInt (workbook, "Header",  4, 'B', "Jira Number", 4, 'C', true);

		Iterator<DefItemInfo> iter = m_defInfo.getItems();
		while (iter.hasNext()) {
			DefItemInfo defItemInfo = iter.next();
			AlphaInfo alphaInfo = Utils.parseSectionToAlphaInfo (workbook, defItemInfo.getName(), defItemInfo.getFromRow(), defItemInfo.getFromCol(), 
							defItemInfo.getToRow(), defItemInfo.getToCol());
			System.out.println("alphaInfo "+alphaInfo);
			list.add (alphaInfo);
		}
	}
}
/*
	private void handleHeader() throws Exception {
		String sheetName = "Header";
		m_headerItemInfo = new HeaderItemInfo (
				Utils.getValidatedInt (m_workbook, sheetName,  3, 'B', "Template Version", 3, 'C', true),
				Utils.getValidatedInt (m_workbook, sheetName,  5, 'B', "Jira Number", 5, 'C', true),
				Utils.getValidatedInt (m_workbook, sheetName,  6, 'B', "SR Number", 6, 'C', true),
				Utils.getValidatedString (m_workbook, sheetName,  7, 'B', "Description", 7, 'C', true));
		System.out.println("m_headerItemInfo "+m_headerItemInfo);
	}
	
	private AlphaInfo m_vendorChange;
	private AlphaInfo m_iracFrameworkChange;
	private AlphaInfo m_etrieveChange;
	private AlphaInfo m_hercRequiredServerChange;
	private AlphaInfo m_HercRequiredFirewallChange;
	private AlphaInfo m_HercRequiredInterwovenChange;
	private AlphaInfo m_HercRequiredLDAPChange;
	private AlphaInfo m_HercRequiredDatabaseChange;
	private AlphaInfo m_HercChangeToVendor;
	private AlphaInfo m_TypeOfHercChange;
	private AlphaInfo m_Functionality;
	private AlphaInfo m_Component;
	private AlphaInfo m_Factors;
	private AlphaInfo m_ProjectFactors;
	private AlphaInfo m_PreDevelopment;
	private AlphaInfo m_Development;
	private AlphaInfo m_Deployment;
	private AlphaInfo m_Totals;
	private AlphaInfo m_HighLevelEstimates;
	private AlphaInfo m_DetailedEstimates;
	private AlphaInfo m_Architect;
	private AlphaInfo m_Deliverables;
	private AlphaInfo m_Comments;
		m_vendorChange = Utils.parseSectionToAlphaInfo (workbook, "Vendor Change", 2, 'B', 12, 'D');
		m_iracFrameworkChange = Utils.parseSectionToAlphaInfo (workbook, "Irac Framework Change", 2, 'B', 9, 'D');
		m_etrieveChange = Utils.parseSectionToAlphaInfo (workbook, "Etrieve Change", 2, 'B', 5, 'D');
		m_hercRequiredServerChange = Utils.parseSectionToAlphaInfo (workbook, "Herc Required Server Change", 2, 'B', 6, 'D');
		m_HercRequiredFirewallChange = Utils.parseSectionToAlphaInfo (workbook, "Herc Required Firewall Change", 2, 'B', 6, 'D');
		m_HercRequiredInterwovenChange = Utils.parseSectionToAlphaInfo (workbook, "Herc Required Interwoven Change", 2, 'B', 5, 'D');
		m_HercRequiredLDAPChange = Utils.parseSectionToAlphaInfo (workbook, "Herc Required LDAP Change", 2, 'B', 6, 'D');
		m_HercRequiredDatabaseChange = Utils.parseSectionToAlphaInfo (workbook, "Herc Required Database Change", 2, 'B', 13, 'D');
		m_HercChangeToVendor = Utils.parseSectionToAlphaInfo (workbook, "Herc Change to Vendor", 2, 'B', 12, 'D');
		m_TypeOfHercChange = Utils.parseSectionToAlphaInfo (workbook, "Type of Herc change", 2, 'B', 11, 'D');
		m_Functionality = Utils.parseSectionToAlphaInfo (workbook, "Functionality", 2, 'B', 12, 'C');
		m_Component = Utils.parseSectionToAlphaInfo (workbook, "Component", 2, 'B', 17, 'C');
		m_Factors = Utils.parseSectionToAlphaInfo (workbook, "Factors", 2, 'B', 7, 'D');
		m_ProjectFactors = Utils.parseSectionToAlphaInfo (workbook, "Project Factors", 2, 'B', 13, 'E');
		m_PreDevelopment = Utils.parseSectionToAlphaInfo (workbook, "Pre Development", 2, 'B', 5, 'G');
		m_Development = Utils.parseSectionToAlphaInfo (workbook, "Development", 2, 'B', 22, 'G');
		m_Deployment = Utils.parseSectionToAlphaInfo (workbook, "Deployment", 2, 'B', 8, 'G');
		m_Totals = Utils.parseSectionToAlphaInfo (workbook, "Totals", 2, 'B', 5, 'G');
		m_HighLevelEstimates = Utils.parseSectionToAlphaInfo (workbook, "High Level Estimates", 2, 'B', 7, 'G');
		m_DetailedEstimates = Utils.parseSectionToAlphaInfo (workbook, "Detailed Estimates", 2, 'B', 7, 'G');
		m_Architect = Utils.parseSectionToAlphaInfo (workbook, "Architect", 2, 'B', 6, 'G');
		m_Deliverables = Utils.parseSectionToAlphaInfo (workbook, "Deliverables", 4, 'B', 50, 'E');
		m_Comments = Utils.parseSectionToAlphaInfo (workbook, "Comments", 2, 'B', 50, 'E');
*/
