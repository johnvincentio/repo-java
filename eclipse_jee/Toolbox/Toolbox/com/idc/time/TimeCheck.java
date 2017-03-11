package com.idc.time;

import java.io.*;
import java.util.*;

import com.idc.trace.LogHelper;

public class TimeCheck {
	private String m_strWorkDir;
	private long m_lTime;

	public TimeCheck() {
		setCheckTime();
		m_strWorkDir = System.getProperty("user.dir");
//		m_strWorkDir = "/tmp/test/work/filecompare";        // for now...
	}
	public static void main(String args[]) {
		TimeCheck app = new TimeCheck();
		app.doApp(args.length > 0 ? args[0] : "");
	}
	private void doApp (String arg) {
		String strDir = arg.trim();
		if (strDir == null || strDir.length() < 4)
		strDir = m_strWorkDir;
		File file = new File(strDir);
		if (! file.exists()) doUsageError();
		if (! file.isDirectory()) doUsageError();

		LogHelper.info("\nTimeCheck is beginning\n");
		LogHelper.info(" Check Directory: "+m_strWorkDir);
		doDirectory (new File(m_strWorkDir));
		LogHelper.info("\nTimeCheck is Complete");
	}
	private void doUsageError() {
		LogHelper.info("Usage: TimeCheck baseline_directory");
		System.exit(1);
	}
	private void doDirectory (File dir) {
//		LogHelper.info(">>> doDirectory; "+dir.getPath());
		if (! dir.isDirectory()) return;

		File file;
		File[] allFiles = dir.listFiles();
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isFile()) doFile(file);
		}
		for (int i=0; i<allFiles.length; i++) {
			file = allFiles[i];
			if (file.isDirectory()) doDirectory(file);
		}
//		LogHelper.info("<<< doDirectory");
	}
	private void doFile (File file) {
//		LogHelper.info(">>> doFile; "+file.getPath());
		if (! file.isFile()) return;
		long lValue = file.lastModified();
//		LogHelper.info("modified "+lValue);
		if (lValue > m_lTime)
			LogHelper.info("File "+file.getPath());
//		LogHelper.info("<<< doFile");
	}
	private void setCheckTime() {
		GregorianCalendar gregoriancalendar = new GregorianCalendar();
		gregoriancalendar.setTimeZone(TimeZone.getTimeZone("EST"));
		int year, date, month, hour, minute, second;
		year = 2005;
		month = 2;                // march
		date = 19;
		hour = 0;
		minute = 0;
		second = 0;
		gregoriancalendar.set(year, month, date, hour, minute, second);
		m_lTime = gregoriancalendar.getTime().getTime();
		LogHelper.info("m_lTime "+m_lTime);
	}
}
