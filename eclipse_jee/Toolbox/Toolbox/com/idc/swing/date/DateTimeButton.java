package com.idc.swing.date;

import java.text.*;
import java.util.*;

public class DateTimeButton extends DateTypeButton implements DateType {
	private static final long serialVersionUID = 1;
	private static final DateFormat m_dateFormat = new SimpleDateFormat("MM-dd-yyyy kk:mm:ss");
	public DateTimeButton() {this(new Date());}
	public DateTimeButton(Date date) {super(DateType.DATE_TIME, m_dateFormat, date);}
}
