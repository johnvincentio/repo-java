package com.idc.rda.phase1;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.Utils;

public class JiraItemInfo implements Serializable {
	private static final long serialVersionUID = 1485001897373596984L;

	private int sheetNumber;
	private String jira;
	private int templateVersion;
	private HashMap<String, HashMap<String, String[]>> sections;

	public JiraItemInfo (HSSFWorkbook workbook, int sheetNumber) {
		this.sheetNumber = sheetNumber;
		this.jira = workbook.getSheetName(sheetNumber);
		System.out.println("sheetNumber "+sheetNumber);
		System.out.println("jira "+jira);
	}

	public int getSheetNumber() {return sheetNumber;}
	public String getJira() {return jira;}
	public int getTemplateVersion() {return templateVersion;}
	public HashMap<String, HashMap<String, String[]>> getSections() {return sections;}

	public void setTemplateVersion (int templateVersion) {this.templateVersion = templateVersion;}
	public void setSections (HashMap<String, HashMap<String, String[]>> map) {this.sections = map;}

	public String toString() {
		return "[("+getSheetNumber()+","+getJira()+","+getTemplateVersion()+"),("+Utils.traceMap2 (sections)+")]";
	}
}
