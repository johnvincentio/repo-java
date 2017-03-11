package com.idc.swing.date;

import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

public class DateTypeButton extends JButton {
	private static final long serialVersionUID = 1;
	private Date m_dateTime;
	private DateTimeChooser m_dateTimeChooser;
	private DateFormat m_dateFormat;
	public DateTypeButton(int nType, DateFormat dateFormat, Date date) {
		super(dateFormat.format(date));
		m_dateFormat = dateFormat;
		m_dateTime = date;
		String str = " ";
		if (nType == DateType.DATE_TIME) str = "Select Date/Time";
		else if (nType == DateType.DATE) str = "Select Date";
		else if (nType == DateType.TIME) str = "Select Time";
		m_dateTimeChooser = new DateTimeChooser(nType, (JFrame) null, str);
	}
	public Date getDateTime() {return m_dateTime;}
	protected void fireActionPerformed(ActionEvent e) {
		System.out.println(">>> DateTypeButton:fireActionPerformed");
		Date newDate = m_dateTimeChooser.select(m_dateTime);
		if (newDate != null) setDateTime(newDate);
		System.out.println("<<< DateTypeButton:fireActionPerformed");
	}
	public void setDateTime(Date date) {
		System.out.println(">>> DateTypeButton:setDateTime");
		Date old = m_dateTime;
		m_dateTime = date;
		setText(m_dateFormat.format(date));
		firePropertyChange("date", old, date);
		System.out.println("<<< DateTypeButton:setDateTime");
	}
}
