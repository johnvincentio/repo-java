package com.idc.excel;

import java.io.File;

import com.idc.trace.JVOutputFile;

public class Control {
	private Workbook m_workbook;
	private int m_nUID = 1;
	private int m_nID = 1;
	private static int SCENARIO = 7;

	public void work () {
		String strFile, strProject, strNewProject, strStats, strName;
		switch (SCENARIO) {
			case 7:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\27Oct2006_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\27Oct2006_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\27Oct2006_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\27Oct2006_statistics.txt";
				strName = "Update of 27Oct2006 Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 6:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\10Oct2006_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\10Oct2006_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\10Oct2006_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\10Oct2006_statistics.txt";
				strName = "Update of 10Oct2006 Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 5:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\Job_Locations_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Job_Locations_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Job_Locations_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Job_Locations_statistics.txt";
				strName = "Job_Locations Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 4:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\Accounts_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Accounts_estimates_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Accounts_estimates.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Accounts_estimates_statistics.txt";
				strName = "Accounts Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 3:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\Dropdown_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Dropdown_estimates_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Dropdown_estimates_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Dropdown_estimates_statistics.txt";
				strName = "Dropdown Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 2:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\Omniture_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Omniture_estimates_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Omniture_estimates_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Omniture_estimates_statistics.txt";
				strName = "Omniture Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			case 1:
				strFile = "C:\\jv\\Herc\\Estimates\\99\\Herc_estimates.xml";
				strProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Herc_estimates_project.xml";
				strNewProject = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Herc_estimates_newproject.xml";
				strStats = "C:\\jv\\Herc\\Estimates\\99\\calculated\\Herc_estimates_statistics.txt";
				strName = "Herc Project Plan";
				doit (strFile, strProject, strNewProject, strStats, strName);
				break;
			default:
				return;
		}
	}
	private void doit(String strFile, String strProject, String strNewProject, String strStats, String strName) {
		System.out.println("Loading xml file "+strFile);
		makeData(strFile);
//		m_workbook.show();
		System.out.println ("Making Statistics");
		makeStatistics();

		setFile (strStats);
		System.out.println ("Making Statistics File");
		makeStatisticsFile (strName);
		System.out.println ("Making Task Statistics File");
		String[] strMatch = {"look","feel"};
		makeTaskStatisticsFile (strMatch);
		System.out.println ("Making Task Statistics File 2");
		String[] strMatch2 = {"visual","changes"};
		makeTaskStatisticsFile (strMatch2);
		closeFile();

		System.out.println ("Making Project Import File");
		makeProjectXML(strProject, strName);
		System.out.println("Project XML completed");

		System.out.println ("Making New Project Import File");
		resetIDs();
		makeNewProjectXML(strNewProject, strName);
		System.out.println("Project XML completed");

		System.out.println ("All tasks have been completed");
	}
	private void makeData(String strFile) {
		File file = new File (strFile);
		if (! file.isFile()) return;
		if (! file.exists()) return;
		JVxml jvxml = new JVxml();
		m_workbook = jvxml.parse(file);
	}
	private void setFile (String s) {JVOutputFile.getInstance().setFile (s, false);}
	private void closeFile() {JVOutputFile.getInstance().close();}
	private void output (String s) {JVOutputFile.getInstance().println(s);}
	private void makeStatistics() {
		Worksheets worksheets = m_workbook.getWorksheets();
		worksheets.reset();
		Worksheet worksheet;
		Sections sections;
		Section section;
		Task task;
//		String owner;
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
//			System.out.println("--- Next worksheet; name :"+worksheet.getWorksheetName()+":");
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
//				System.out.println("--- Next Section; "+section.getName()+" "+section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					section.setTotals(task);
					section.getNext();
				}
				sections.getNext();
			}
//			worksheet.showTotals("Control, worksheet - before setTotals");
			worksheet.setTotals();
//			worksheet.showTotals("Control, worksheet - after setTotals");
			worksheets.getNext();
		}
		m_workbook.setTotals();
	}
	private void makeStatisticsFile (String strName) {
		output ("");
		output ("Statistics for "+strName);
		output ("");
		outputTotals ("Totals for the Project", m_workbook.getTotals());
		output ("");

		Worksheets worksheets = m_workbook.getWorksheets();
		worksheets.reset();
		Worksheet worksheet;
		Sections sections;

		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
			outputTotals ("Totals for the Worksheet "+worksheet.getWorksheetName(),
					worksheet.getTotals());
			output ("");
//			System.out.println("name :"+worksheet.getWorksheetName()+":");
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				Section section = sections.getSection();
				System.out.println("Section; "+section.getName()+" "+section.getDs());
//				section.showTotals();
				sections.getNext();
			}
			worksheets.getNext();
		}
		output ("");
		output ("End of Statistics for "+strName);
		output ("");

	}
	private void makeTaskStatisticsFile (String strMatch[]) {
		output ("");
		output ("Task Statistics, matching: "+makeMatchStrings(strMatch));
		output ("");

		Worksheets worksheets = m_workbook.getWorksheets();
		worksheets.reset();
		Worksheet worksheet;
		Sections sections;
		Section section;
		Task task;
		Totals totals = new Totals();

		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
//			System.out.println("worksheet name :"+worksheet.getWorksheetName()+":");
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
//				System.out.println("section name :"+section.getName()+":");
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
//					System.out.println("task name :"+task.getName()+":");
					if (matchStrings (task, strMatch)) {
//						System.out.println("task; "+task.toString());
						totals.setTotals(task);
					}
					section.getNext();
				}
				sections.getNext();
			}
			worksheets.getNext();
		}
//		totals.show("totals for matching");
		outputTotals (makeMatchStrings(strMatch),totals);
	}
	private String makeMatchStrings (String[] match) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<match.length; i++) {
			if (i > 0) buf.append(",");
			buf.append(match[i]);
		}
		return buf.toString();
	}
	private boolean matchStrings(Task task, String match[]) {
		String activity = task.getName();
		if (activity == null || activity.length() < 1) return false;
		for (int i=0; i<match.length; i++) {
			if ((activity.toLowerCase()).indexOf((match[i].toLowerCase())) < 0) return false;
		}
		return true;
	}
	private void outputTotals (String msg, Totals totals) {
		output (msg);
		outputPart ("", totals.getAllTasks(), totals.getAllHours(), 
				totals.getAllQcrHours(), totals.getTotalHours());
		outputPart ("Frontend", totals.getTasksFront(), totals.getHoursFront(), 
				totals.getHoursQcrFront(), totals.getTotalHoursFront());
		outputPart ("Backend", totals.getTasksBack()+totals.getTasksOther(),
				totals.getHoursBack()+totals.getHoursOther(), 
				totals.getHoursQcrBack()+totals.getHoursQcrOther(),
				totals.getTotalHoursBack()+totals.getTotalHoursOther());
	}
	private void outputPart (String msg, int cnt1, int cnt2, int cnt3, int cnt4) {
		output ("");
		if (msg == null || msg.length() < 1)
			output ("\tTotals");
		else
			output ("\tTotals for "+msg);
		output ("\t\tNumber of tasks\t\t" + cnt1);
		outputDuration (2, "Number of hours", cnt4);
		outputDuration (3, "Number of task hours", cnt2);
		outputDuration (3, "Number of qcr hours", cnt3);
	}
	private void outputDuration (int tabs, String msg, int dur) {
		StringBuffer sbuf = new StringBuffer();
		for (int i=0; i<tabs; i++) sbuf.append("\t");
		sbuf.append(msg).append("\t\t");
		sbuf.append(dur).append("\t(days ");
		sbuf.append(makeDays(dur)).append(")");
		output (sbuf.toString());
	}
	private void makeProjectXML(String strFile, String strName) {
		setFile (strFile);
		output("<?xml version=\"1.0\"?>");
		output("<Project xmlns=\"http://schemas.microsoft.com/project\">");
		output (makeNode ("Company",m_workbook.getDocumentProperties().getCompany()));
		output (makeNode ("Author",m_workbook.getDocumentProperties().getAuthor()));
		output ("");
		output("<Tasks>");
		output ("");

		makeHeaderTask(strName);
		makeBlankTask();

		Worksheets worksheets = m_workbook.getWorksheets();
		worksheets.reset();
		Worksheet worksheet;
		Sections sections;
		Section section;
		Task task;
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
//			System.out.println("name :"+worksheet.getWorksheetName()+":");
			makeWorksheetTask(worksheet.getWorksheetName());
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
//				System.out.println("Section; "+section.getName()+" "+section.getDs());
				makeSummaryTask(section.getName(), section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					makeTask(task);
					section.getNext();
				}
				makeBlankTask();
				sections.getNext();
			}
			worksheets.getNext();
		}
		output("</Tasks>");
		output("</Project>");
		closeFile();
	}
	private void makeNewProjectXML(String strFile, String strName) {
		setFile (strFile);
		output("<?xml version=\"1.0\"?>");
		output("<Project xmlns=\"http://schemas.microsoft.com/project\">");
		output (makeNode ("Company",m_workbook.getDocumentProperties().getCompany()));
		output (makeNode ("Author",m_workbook.getDocumentProperties().getAuthor()));
		output ("");
		output("<Tasks>");
		output ("");

		makeHeaderTask(strName);

		Worksheet worksheet;
		Sections sections;
		Section section;
		Task task;
		String taskName;
		Worksheets worksheets = m_workbook.getWorksheets();

//	handle Most tasks

		makeBlankTask();
		makeWorksheetTask("General Unassigned Tasks");
		worksheets.reset();
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
			System.out.println("name :"+worksheet.getWorksheetName()+":");
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
				System.out.println("Section; "+section.getName()+" "+section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					taskName = task.getName();
					System.out.println("task :"+task.getName());
					if (! taskName.toLowerCase().startsWith("visual changes") && ! taskName.toLowerCase().startsWith("look and feel"))
						makeTask(task, section.getName());
					section.getNext();
				}
//				makeBlankTask();
				sections.getNext();
			}
			worksheets.getNext();
		}

//		 handle Look and Feel

		makeBlankTask();
		makeWorksheetTask("Look and Feel");
		worksheets.reset();
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
			System.out.println("name :"+worksheet.getWorksheetName()+":");
//			makeWorksheetTask(worksheet.getWorksheetName());
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
				System.out.println("Section; "+section.getName()+" "+section.getDs());
//				makeSummaryTask(section.getName(), section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					taskName = task.getName();
					System.out.println("task :"+task.getName());
					if (taskName.toLowerCase().startsWith("look and feel")) 
						makeTask(task, section.getName());
					section.getNext();
				}
//				makeBlankTask();
				sections.getNext();
			}
			worksheets.getNext();
		}

// handle Visual Changes

		makeBlankTask();
		makeWorksheetTask("Visual Changes");
		worksheets.reset();
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
			System.out.println("name :"+worksheet.getWorksheetName()+":");
//			makeWorksheetTask(worksheet.getWorksheetName());
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
				System.out.println("Section; "+section.getName()+" "+section.getDs());
//				makeSummaryTask(section.getName(), section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					taskName = task.getName();
					System.out.println("task :"+task.getName());
					if (taskName.toLowerCase().startsWith("visual changes")) 
						makeTask(task, section.getName());
					section.getNext();
				}
//				makeBlankTask();
				sections.getNext();
			}
			worksheets.getNext();
		}

//	 handle QCR

		makeWorksheetTask("QCR");
		worksheets.reset();
		while (worksheets.hasNext()) {
			worksheet = worksheets.getWorksheet();
			System.out.println("name :"+worksheet.getWorksheetName()+":");
//			makeWorksheetTask(worksheet.getWorksheetName());
			sections = worksheet.getSections();
			sections.reset();
			while (sections.hasNext()) {
				section = sections.getSection();
				System.out.println("Section; "+section.getName()+" "+section.getDs());
//				makeSummaryTask(section.getName(), section.getDs());
				section.reset();
				while (section.hasNext()) {
					task = section.getTask();
					taskName = task.getName();
					System.out.println("task :"+task.getName());
					makeQCRTask(task, section.getName());
					section.getNext();
				}
//				makeBlankTask();
				sections.getNext();
			}
			worksheets.getNext();
		}

// all done

		output("</Tasks>");
		output("</Project>");
		closeFile();
	}
	private void makeHeaderTask (String strName) {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name",strName));
		output (makeNode ("Type","1"));
		output (makeNode ("IsNull","0"));
		output (makeNode ("WBS","1"));
		output (makeNode ("OutlineNumber","1"));
		output (makeNode ("OutlineLevel","1"));
		output (makeNode ("DurationFormat","21"));
		output (makeNode ("FixedCostAccrual","3"));
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
	private void makeWorksheetTask (String strName) {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name",strName));
		output (makeNode ("Type","1"));
		output (makeNode ("IsNull","0"));
		output (makeNode ("WBS","1.1"));
		output (makeNode ("OutlineNumber","1.1"));
		output (makeNode ("OutlineLevel","2"));
		output (makeNode ("DurationFormat","21"));
		output (makeNode ("FixedCostAccrual","3"));
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
	private void makeSummaryTask (String strName, String strDs) {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		String strTmp = strName;
		if (strDs != null && ! strDs.equals("")) strTmp += " "+strDs;
		output (makeNode ("Name",strTmp));
		output (makeNode ("Type","1"));
		output (makeNode ("IsNull","0"));
		output (makeNode ("WBS","1.1.1"));
		output (makeNode ("OutlineNumber","1.1.1"));
		output (makeNode ("OutlineLevel","3"));
		output (makeNode ("DurationFormat","21"));
		output (makeNode ("FixedCostAccrual","3"));
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
	private void makeTask (Task task) {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name",task.getName()));
		output (makeNode ("WBS","1.1.1.1"));
		output (makeNode ("OutlineNumber","1.1.1.1"));
		output (makeNode ("Duration",makeDuration(task.getDur())));
		output (makeNode ("OutlineLevel","4"));
		output (makeNode ("DurationFormat","7"));
		output (makeNode ("FixedCostAccrual","3"));
		output (makeNode ("RemainingDuration",makeDuration(task.getDur())));
		output("<ExtendedAttribute>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("FieldID","188743731"));
		output (makeNode ("Value",task.getOwner()));
		output("</ExtendedAttribute>");
		output("</Task>");
		output ("");
		incrUID(); incrID();

		if (task.getQcr() == null || task.getQcr().equals("")) return;
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name","QCR - "+task.getName()));
		output (makeNode ("WBS","1.1.1.1"));
		output (makeNode ("OutlineNumber","1.1.1.1"));
		output (makeNode ("Duration",makeDuration(task.getQcr())));
		output (makeNode ("OutlineLevel","4"));
		output (makeNode ("DurationFormat","7"));
		output (makeNode ("FixedCostAccrual","3"));
		output (makeNode ("RemainingDuration",makeDuration(task.getQcr())));
		output("<ExtendedAttribute>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("FieldID","188743731"));
		output (makeNode ("Value",task.getOwner()));
		output("</ExtendedAttribute>");
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
	private void makeBlankTask() {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Type","0"));
		output (makeNode ("IsNull","1"));
		output("</Task>");
		output ("");
		output ("");
		incrUID(); incrID();
	}
	private String makeNode (String name, String value) {
		return "<"+name+">"+cleanValue(value)+"</"+name+">";
	}
	private String makeNode (String name, int value) {
		return makeNode (name, Integer.toString(value));
	}
	private String makeDuration (String strDur) {
//		System.out.println("makeDuration; strDur :"+strDur+":");
		if (strDur == null || strDur.equals(""))
			return "PT0HOMOS";
		double dbl = Double.parseDouble(strDur) * 8;
//		System.out.println("dbl "+dbl);
//		System.out.println("yup :"+Double.toString(dbl));
		return "PT"+Double.toString(dbl)+"HOMOS";
	}
	private String makeDays (int hours) {
		double dbl = hours / 8.0;
		return String.valueOf(dbl);
	}
	private String cleanValue (String value) {
//		System.out.println(">>> cleanValue; value :"+value+":");
		StringBuffer buf = new StringBuffer();
		char ch;
		for (int i=0; i<value.length(); i++) {
			ch = value.charAt(i);
//			System.out.println(" i "+i+" ch :"+ch+":");
			if (Character.isLetterOrDigit(ch) ||
					ch == '!' || ch == '@' || ch == '#' || ch == '$' ||
					ch == '%' || ch == '^' || ch == '&' || ch == '*' ||
					ch == '(' || ch == ')' || 
					ch == '_' || ch == '-' || ch == '+' || ch == '=' ||	
					ch == '{' || ch == '}' || ch == '[' || ch == ']' ||
					ch == '|' || ch == '\\' || 
					ch == ':' || ch == ';' || ch == '"' || ch == '\'' ||
					ch == '<' || ch == ',' || ch == '>' || ch == '.' ||
					ch == '?' || ch == '/' || ch == ' ') {
//				System.out.println(" (1) buf :"+buf.toString()+":");
//				System.out.println(" ch :"+ch+":");
				buf.append(ch);
//				System.out.println(" (2) buf :"+buf.toString()+":");
			}
			else
				System.out.println("*** filtered out bad char from :"+value+":");
		}
//		System.out.println("<<< cleanValue; buf :"+buf.toString()+":");
		return buf.toString();
	}
	private void incrUID() {m_nUID += 10;}
	private void incrID() {m_nID += 1;}
	private void resetIDs() {m_nUID = m_nID = 1;}
	private void makeTask (Task task, String sectionName) {
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name",sectionName + " - " + task.getName()));
		output (makeNode ("WBS","1.1.1"));
		output (makeNode ("OutlineNumber","1.1.1"));
		output (makeNode ("Duration",makeDuration(task.getDur())));
		output (makeNode ("OutlineLevel","3"));
		output (makeNode ("DurationFormat","7"));
		output (makeNode ("FixedCostAccrual","3"));
		output (makeNode ("RemainingDuration",makeDuration(task.getDur())));
		output("<ExtendedAttribute>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("FieldID","188743731"));
		output (makeNode ("Value",task.getOwner()));
		output("</ExtendedAttribute>");
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
	private void makeQCRTask (Task task, String sectionName) {
		if (task.getQcr() == null || task.getQcr().equals("")) return;
		output("<Task>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("ID",m_nID));
		output (makeNode ("Name",sectionName + " - " + task.getName()));
		output (makeNode ("WBS","1.1.1"));
		output (makeNode ("OutlineNumber","1.1.1"));
		output (makeNode ("Duration",makeDuration(task.getQcr())));
		output (makeNode ("OutlineLevel","3"));
		output (makeNode ("DurationFormat","7"));
		output (makeNode ("FixedCostAccrual","3"));
		output (makeNode ("RemainingDuration",makeDuration(task.getQcr())));
		output("<ExtendedAttribute>");
		output (makeNode ("UID",m_nUID));
		output (makeNode ("FieldID","188743731"));
		output (makeNode ("Value",task.getOwner()));
		output("</ExtendedAttribute>");
		output("</Task>");
		output ("");
		incrUID(); incrID();
	}
}








