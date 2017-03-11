package com.idc.calendar;

import java.util.*;
import java.text.*;
import java.util.logging.*;

public class MyBook {
	private String m_strYear;
	private String m_strFile;
	private Locale locale = new Locale ("En","US");
	private Calendar m_myCal = Calendar.getInstance(locale);
	private Logger m_logger = Logger.getLogger("");
	private MyFile m_file;

	public MyBook(String strYear, String strFile) {
		m_strYear = strYear;
		m_strFile = strFile;
		m_file = new MyFile (m_strFile, m_logger);
	}
	public void makeBook() {
		int yyyy = Integer.parseInt(m_strYear);
		m_file.open();
		for (int mm=0; mm<12; mm++) { // set the next month
			m_myCal.set(yyyy,mm,1);
			int maxday = m_myCal.getActualMaximum (Calendar.DAY_OF_MONTH);
			m_logger.info("maxday "+maxday);
			newMonth();
			for (int dd=1; dd<=maxday; dd++) {
				m_myCal.set(yyyy,mm,dd);
				int jv = m_myCal.get (Calendar.DAY_OF_WEEK);
				m_logger.info ("dd "+dd+" dow "+jv+" date "+getFormattedDay());
				if (dd == 1 || jv == 2) {        // 1st of month or Monday
					newWeek();
					if (jv < 4 && ((maxday - dd) > 3)) newPage();
				}
				if (m_myCal.get (Calendar.MONTH) != mm) break;
				newDay();
			}
		}
		m_file.close();
	}
	private String getFormattedMonth() {
		DateFormat fmt = new SimpleDateFormat ("MMM yyyy" );
		return fmt.format (m_myCal.getTime());
	}
	private String getFormattedWeek() {
		DateFormat fmt = new SimpleDateFormat ("EEE, d MMM yyyy" );
		return fmt.format (m_myCal.getTime());
	}
	private String getFormattedDay() {
		DateFormat fmt = new SimpleDateFormat ("EEE, d MMM yyyy" );
		return fmt.format (m_myCal.getTime());
	}
	private void newMonth () {
		newPage();
		m_file.write(getFormattedMonth());
	}
	private void newWeek () {
		newPage();
		m_file.write("Week beginning "+getFormattedWeek());
	}
	private void newDay () {
		m_file.write(getFormattedDay());
		newLine(); newLine(); newLine(); newLine();
		newLine(); newLine(); newLine();
	}
	private void newPage () {m_file.newPage();}
	private void newLine () {m_file.newLine();}
}
