package com.idc.project;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import com.idc.trace.JVOutputFile;

public class Control {
	private Locale locale = new Locale ("En", "US");
	private Calendar m_myCal = Calendar.getInstance(locale);
	private Project m_project;
	private static int SCENARIO = 1;

	public void work (String[] args) {
		String strFile, strReport1, strReport2;
		switch (SCENARIO) {
			case 2:
				strFile = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\Luv\\1.xml";
				strReport1 = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\Luv\\summary_report.html";
				strReport2 = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\Luv\\tasks_report.html";
				doit (strFile, strReport1, strReport2);
				break;
			case 1:
				strFile = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\WS6_rev_2_0.xml";
				strReport1 = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\summary_report.html";
				strReport2 = "C:\\jv\\Docs\\Project Plans\\WS V6 Migration\\tasks_report.html";
				doit (strFile, strReport1, strReport2);
				break;
			default:
				break;
		}

	}
	private void doit(String strFile, String strReport1, String strReport2) {
		 System.out.println("Loading xml file "+strFile);
		 makeData(strFile);
//		 m_project.show();
		 
		 System.out.println ("Making Summary Report");
		 makeSummaryReport("Summary Report", strReport1, true);

		 System.out.println ("Making Tasks Report");
		 makeSummaryReport("Tasks Report", strReport2, false);
		 
		 System.out.println("All reports complete");
	}
	private void makeSummaryReport(String title, String file, boolean bSummary) {
		int maxLevel = 99;
		if (bSummary) maxLevel = 3;
		String str1 = "#DFDFDF";
		String str2 = "#FFFFFF";
		setFile (file);
		output("<html>");
		output("<head>");
		output("<title>"+title+"</title>");
		output("</head>");
		output("<h2>Project: "+formatString(m_project.getName())+"</h2>");
		output("<h2> Report: "+formatString(title)+"</h2>");

		output("<table border>");
		output("<tr bgcolor="+str1+">");
		outputTH("Id"); outputTH("Task");outputTH("Dur.");outputTH("Start");
		outputTH("End");outputTH("Owner");
		output("</tr>");

		Tasks tasks = m_project.getTasks();
		Iterator<Task> iter = tasks.getItems();
		while (iter.hasNext()) {
			Task task = (Task) iter.next();
			int level = Integer.parseInt (task.getOutline());
			if (level > 0 && level < maxLevel) {
				output("<tr bgcolor="+str2+">");
				outputTD(task.getId());
				outputTD(task.getTask(),"left",200);
				outputTD(formatDuration(task.getDur()));
				outputTD(formatDate(task.getStart()),"left");
				outputTD(formatDate(task.getFinish()),"left");
				outputTD(task.getOwner());
				output("</tr>");
			}
		}
		output("</table>");
		output("</html>");
		closeFile();
	}

	private void outputTH (String s) {output("<th nowrap align=left>"+s+"</th>");}
	private void outputTD (String s) {outputTD(s,"center");}
	private void outputTD (String s, String align) {
		if (s == null|| s.equals(""))
			output("<td nowrap align="+align+">&nbsp</td>");
		else
			output("<td nowrap align="+align+">"+s+"</td>");
	}
	private void outputTD (String s, String align, int width) {
		if (s == null|| s.equals(""))
			output("<td nowrap width="+width+" align="+align+">&nbsp</td>");
		else
			output("<td nowrap width="+width+" align="+align+">"+s+"</td>");
	}
	private void setFile (String s) {JVOutputFile.getInstance().setFile (s, false);}
	private void closeFile() {JVOutputFile.getInstance().close();}
	private void output (String s) {JVOutputFile.getInstance().println(s);}
	private String formatDate (String date) {
		if (date.length() < 3) return "";
		int i1 = date.indexOf('T');
		String sdate = date.substring(0,i1);
		i1 = sdate.lastIndexOf('-');
		String sDay = sdate.substring(i1+1);
		String s2 = sdate.substring(0,i1);
		i1 = s2.lastIndexOf('-');
		String sMonth = s2.substring(i1+1);
		String sYear = s2.substring(0,i1);
//		System.out.println("d,m,y "+sDay+","+sMonth+","+sYear);
		m_myCal.set(Integer.parseInt(sYear),Integer.parseInt(sMonth)-1,Integer.parseInt(sDay));
		DateFormat fmt = new SimpleDateFormat ("EEE, d MMM yyyy" );
		return fmt.format (m_myCal.getTime());
	}
	private String formatDuration (String dur) {
		if (dur.length() < 3) return "0";
		String s1 = dur.substring(2);
		int i1 = s1.indexOf('H');
		String s2 = s1.substring(0,i1);
		int iDur = Integer.parseInt(s2) / 8;
		return String.valueOf(iDur);
	}
	private String formatString (String s) {
		if (s == null || s.equals(""))
			return " ";
		else
			return s;
	}
	private void makeData(String strFile) {
		 File file = new File (strFile);
		 if (! file.isFile()) return;
		 if (! file.exists()) return;
		 JVxml jvxml = new JVxml();
		 m_project = jvxml.parse(file);
	}
}
