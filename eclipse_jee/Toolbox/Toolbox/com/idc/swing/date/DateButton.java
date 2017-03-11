package com.idc.swing.date;

import java.text.*;
import java.util.*;

public class DateButton extends DateTypeButton implements DateType {
	private static final long serialVersionUID = 1;
	private static final DateFormat m_dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	public DateButton() {this(new Date());}
	public DateButton(Date date) {super(DateType.DATE, m_dateFormat, date);}
}

