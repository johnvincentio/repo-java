package com.idc.swing.date;

import java.text.*;
import java.util.*;

public class TimeButton extends DateTypeButton implements DateType {
	private static final long serialVersionUID = 1;
	private static final DateFormat m_dateFormat = new SimpleDateFormat("kk:mm:ss");
	public TimeButton() {this(new Date());}
	public TimeButton(Date date) {super(DateType.TIME, m_dateFormat, date);}
}
