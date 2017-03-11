package com.idc.rda.phase1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idc.rda.Utils;

public class JiraInfo implements Serializable {
	private static final long serialVersionUID = 8734931842111857204L;

	private ArrayList<JiraItemInfo> m_collection = new ArrayList<JiraItemInfo>();

	private String spreadSheetPath;
	private boolean summary = false;
	private int sheets;

	public JiraInfo (String spreadSheetPath, final HSSFWorkbook workbook) {
		this.spreadSheetPath = spreadSheetPath;
		if (workbook.getSheetIndex("Summary (calculated)") > -1) summary = true;
		sheets = workbook.getNumberOfSheets();
		int fromIndex = workbook.getSheetIndex ("Begin");
		int toIndex = workbook.getSheetIndex ("End");
		for (int sheetNumber = fromIndex + 1; sheetNumber < toIndex; sheetNumber++) {
			JiraItemInfo jiraItemInfo = new JiraItemInfo (workbook, sheetNumber);
			int templateVersion = Utils.getTemplateVersion (workbook, sheetNumber);
			jiraItemInfo.setTemplateVersion (templateVersion);
			if (templateVersion == 100) 
				jiraItemInfo.setSections (JiraTemplates.getSections_100 (workbook, sheetNumber));
			else {
				System.err.println("unknown template version");
				System.exit(1);
			}
			System.out.println("jiraItemInfo "+jiraItemInfo);
			add (jiraItemInfo);
		}
	}

	public void add (JiraItemInfo item) {if (item != null) m_collection.add (item);}
	public Iterator<JiraItemInfo> getItems() {return m_collection.iterator();}
	public JiraItemInfo getJiraItemInfo (int sheetNumber) {
		Iterator<JiraItemInfo> iter = getItems();
		while (iter.hasNext()) {
			JiraItemInfo item = iter.next();
			if (item == null) continue;
			if (sheetNumber == item.getSheetNumber()) return item;
		}
		return null;
	}
	public int getSize() {return m_collection.size();}
	public boolean isNone() {return getSize() < 1;}

	public String getSpreadSheetPath() {return spreadSheetPath;}
	public boolean isSummary() {return summary;}
	public int getSheets() {return sheets;}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m_collection.size(); i++)
			buf.append (m_collection.get(i).toString());
		return "("+getSpreadSheetPath()+","+isSummary()+"),"+getSheets()+"),"+"("+buf.toString()+")";
	}
}
