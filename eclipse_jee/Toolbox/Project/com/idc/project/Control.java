package com.idc.project;

import java.util.Iterator;
import java.io.File;
import java.util.Locale;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.idc.trace.JVOutputFile;

public class Control {
	private Locale locale = new Locale ("En", "US");
	private Calendar m_myCal = Calendar.getInstance(locale);
	private Project m_project;
	private static int SCENARIO = 2;

	public void work (String[] args) {
		String strFile, strReport1, strReport2, strReport3, strReport4;
		switch (SCENARIO) {
			case 2:
				strFile = "C:\\jv\\Herc\\Plan\\99\\HercProject.xml";
//				strFile = "C:\\jv\\Herc\\Plan\\99\\testt.xml";
				strReport1 = "C:\\jv\\Herc\\Plan\\99\\summary_report.html";
				strReport2 = "C:\\jv\\Herc\\Plan\\99\\tasks_report.html";
				strReport3 = "C:\\jv\\Herc\\Plan\\99\\totals_summary_report.html";
				strReport4 = "C:\\jv\\Herc\\Plan\\99\\totals_tasks_report.html";
				doit (strFile, strReport1, strReport2, strReport3, strReport4);
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
	private void doit(String strFile, String strReport1, String strReport2, 
			String strReport3, String strReport4) {
		 System.out.println("Loading xml file "+strFile);
		 makeData(strFile);
//		 m_project.show();
		 
		 System.out.println ("Making Summary Report");
		 makeSummaryReport("Summary Report", strReport1, true);

		 System.out.println ("Making Tasks Report");
		 makeSummaryReport("Tasks Report", strReport2, false);

		 calculateTotals();
		 System.out.println ("Making Summary Totals Report");
		 makeTotalsReport("Totals Summary Report", strReport3, true);

		 System.out.println ("Making Tasks Totals Report");
		 makeTotalsReport("Totals Tasks Report", strReport4, false);

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
		outputTH("Id"); outputTH("Task");outputTH("Dur.");outputTH("%Comp.");outputTH("Start");
		outputTH("End");outputTH("Owner");
		output("</tr>");

		Tasks tasks = m_project.getTasks();
		Iterator<Task> iter = tasks.getItems();
		while (iter.hasNext()) {
			Task task = (Task) iter.next();
			int level = task.getOutline();
			if (level > 0 && level < maxLevel) {
				output("<tr bgcolor="+str2+">");
				outputTD(task.getId());
				outputTD(task.getTask(),"left",200);
				outputTD(formatDuration(task.getOrigDur()));
				outputTD(formatPercentComplete(task.getPercentComplete()));
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

	private void calculateTotals() {
		Task task;
		int newLevel;
/*
 * 	set blank tasks to inactive
 */
		Tasks tasks = m_project.getTasks();
		Iterator<Task> iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (isBlankTask(task)) task.setInactive();
		}
//		tasks.show();
/*
 * 	calculate maximum summary level
 */
		int minlevel = 999;
		int maxlevel = -1;
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
			int level = task.getOutline();
			if (level < minlevel) minlevel = level;
			if (level > maxlevel) maxlevel = level;
		}
		System.out.println("minlevel,maxlevel "+minlevel+","+maxlevel);
/*
 * look for and note summary tasks
 */
//		tasks.show();
		Task prevTask = null;
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
			if ((prevTask != null) && (task.getOutline() > prevTask.getOutline())) {
				prevTask.setOrigDur(0);
				prevTask.setRemDur(0);
				prevTask.setPercentComplete(0);
				prevTask.setSummary();
			}
			prevTask = task;
		}
//		tasks.show();
/*
 * 	calculate remaining durations for non-summary tasks
 */
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
			if (task.isSummary()) continue;
			int pc = task.getPercentComplete();
			if (pc > 0) task.setRemDur((task.getOrigDur() * (100 - pc/JVxml.MULTIPLIER)) / 100);
		}	
//		tasks.show();
/*
 * 	calculate original and remaining durations for summary tasks
 */
		Totals[] totals = new Totals[maxlevel+1];
		Task updateTask;
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		int incr1;
		int incr2;
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
//			task.show();
			newLevel = task.getOutline();
//			System.out.println(" newLevel "+newLevel);
			if (task.isSummary()){
				if (totals[newLevel] != null) {	// add totals to the previous summary tasks
					for (int num=minlevel; num <= newLevel; num++) {
						if (totals[num] == null) continue;
						incr1 = totals[num].getOrigDur();
						incr2 = totals[num].getRemDur();
						updateTask = tasks.getTask(totals[num].getUid());
						updateTask.incrOrigDur(incr1);
						updateTask.incrRemDur(incr2);
						totals[num].reset();
					}
				}
				totals[newLevel] = new Totals(task.getUid());
				totals[newLevel+1] = null;
				continue;
			}
			for (int num=minlevel; num<maxlevel; num++) {
				if (totals[num] == null) continue;
				totals[num].incrOrigDur(task.getOrigDur());
				totals[num].incrRemDur(task.getRemDur());
			}
		}

//		tasks.show();
		for (int num=minlevel; num<maxlevel; num++) {
//			System.out.println("level "+num+","+totals[num].toString());
			if (totals[num] == null) continue;
			incr1 = totals[num].getOrigDur();
			incr2 = totals[num].getRemDur();
			updateTask = tasks.getTask(totals[num].getUid());
			updateTask.incrOrigDur(incr1);
			updateTask.incrRemDur(incr2);
		}
//		tasks.show();
/*
 * 	recalculate pc for summary tasks
*/
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
			if (! task.isSummary()) continue;
			int origDur = task.getOrigDur();
//			int remDur = task.getRemDur();
			if (origDur > 0) {
				task.setPercentComplete(calculatePercentComplete(task));
			}
			else {
				task.setRemDur(0);
				task.setPercentComplete(0);
			}
		}
//		tasks.show();
	}

	private void makeTotalsReport(String title, String file, boolean bSummary) {
		int maxLevel = 99;
		if (bSummary) maxLevel = 3;
		String str1 = "#BFBFBF";
		String str2 = "#DFDFDF";
		String str3 = "#FFFFFF";
		setFile (file);
		output("<html>");
		output("<head>");
		output("<title>"+title+"</title>");
		output("</head>");
		output("<h2>Project: "+formatString(m_project.getName())+"</h2>");
		output("<h2> Report: "+formatString(title)+"</h2>");

		output("<table border>");
		output("<tr bgcolor="+str1+">");
		outputTH("Id"); outputTH("Task");outputTH("Level");outputTH("Original Dur.");
		outputTH("Remaining Duration");
		outputTH("% Complete");
		output("</tr>");

		Tasks tasks = m_project.getTasks();
		Iterator<Task> iter = tasks.getItems();
		while (iter.hasNext()) {
			Task task = (Task) iter.next();
			if (! task.isActive()) continue;
			int level = task.getOutline();
			if (level > 0 && level < maxLevel) {
				if (task.isSummary())
					output("<tr bgcolor="+str2+">");
				else
					output("<tr bgcolor="+str3+">");
				outputTD(task.getId());
				outputTD(task.getTask(),"left",200);
				outputTD(formatNumber(task.getOutline()));
				outputTD(formatDuration(task.getOrigDur()));
				outputTD(formatDuration(task.getRemDur()));
				outputTD(formatPercentComplete(task.getPercentComplete()));
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
	private static String formatDuration (int num) {
		double d = num;
		d = d / JVxml.MULTIPLIER;
		return Double.toString(d);
	}
	private static String formatNumber (int num) {
		return Integer.toString(num);
	}
	private static String formatPercentComplete (int num) {
		double d = num;
		d = d / JVxml.MULTIPLIER;
		return Double.toString(d);
	}
	private String formatString (String s) {
		if (s == null || s.equals("")) return " ";
		return s;
	}
	private boolean isBlankTask (Task task) {
		if (task == null) return true;
		if (task.getUid() == null) return true;
		if (task.getId() == null) return true;
		if (task.getTask() == null) return true;
		return false;
	}
	private int calculatePercentComplete (Task task) {
		int remDur = task.getRemDur();
		int origDur = task.getOrigDur();
		if (remDur < 1) return 100 * JVxml.MULTIPLIER;
		if (remDur >= origDur) return 0;
		long num = origDur - remDur;
		num = (JVxml.MULTIPLIER * num) / origDur;
		num *= 100;
		int inum = (int) num;
		return inum;
	}
	private void makeData(String strFile) {
		 File file = new File (strFile);
		 if (! file.isFile()) return;
		 if (! file.exists()) return;
		 JVxml jvxml = new JVxml();
		 m_project = jvxml.parse(file);
	}
}


/*
		Totals[] totals = new Totals[maxlevel+1];
		prevLevel = -1;
		Task updateTask;
		tasks = m_project.getTasks();
		iter = tasks.getItems();
		while (iter.hasNext()) {
			task = (Task) iter.next();
			if (! task.isActive()) continue;
//			task.show();
			newLevel = task.getOutline();
			System.out.println("prevLevel "+prevLevel+" newLevel "+newLevel);
			if (prevLevel < 0 && newLevel != minlevel) {
				System.err.println("Trouble with newLevel = "+newLevel);
				System.exit(-1);
			}
			if (newLevel == prevLevel) {
				System.out.println("(1) prevLevel "+prevLevel+","+totals[prevLevel].toString());
				totals[prevLevel].incrOrigDur(task.getOrigDur());
				totals[prevLevel].incrRemDur(task.getRemDur());
				System.out.println("(2) prevLevel "+prevLevel+","+totals[prevLevel].toString());
				continue;
			}
			if (newLevel > prevLevel) {
				System.out.println("UID :"+task.getUid());
				totals[newLevel] = new Totals(task.getUid());
				prevLevel = newLevel;
				continue;
			}
			if (newLevel < prevLevel) {// update ALL previous summary tasks
				int incr1 = totals[prevLevel].getOrigDur();
				int incr2 = totals[prevLevel].getRemDur();
				System.out.println("incr1 "+incr1+" incr2 "+incr2);
				for (int num=minlevel; num<prevLevel; num++) {
					updateTask = tasks.getTask(totals[num].getUid());	// update previous summary task
					updateTask.incrOrigDur(incr1);
					updateTask.incrRemDur(incr2);
				}
				totals[prevLevel] = null;
				totals[newLevel] = new Totals(task.getUid());
				prevLevel = newLevel;
				continue;
			}
		}
		for (int num=minlevel; num<maxlevel; num++) {
			System.out.println("level "+num+","+totals[num].toString());
		}

*/