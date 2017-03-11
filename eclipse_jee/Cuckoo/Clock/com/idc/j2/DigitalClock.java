package com.idc.j2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DigitalClock extends Panel {
	private static final long serialVersionUID = 1;

	BasicStroke stroke = new BasicStroke(4, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_BEVEL);

	String hour1, hour2;

	String minute1, minute2;

	String second1, second2;

	String mill1, mill2, mill3;

	int hr1, hr2;

	int min1, min2;

	int sec1, sec2;

	int mll1, mll2, mll3;

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		DigitalNumber num = new DigitalNumber(10, 10, 20, Color.cyan,
				Color.black);
		GregorianCalendar c = new GregorianCalendar();

		String hour = String.valueOf(c.get(Calendar.HOUR));
		String minute = String.valueOf(c.get(Calendar.MINUTE));
		String second = String.valueOf(c.get(Calendar.SECOND));
		String milliSecond = String.valueOf(c.get(Calendar.MILLISECOND));

		if (hour.length() == 2) {
			hour1 = hour.substring(0, 1);
			hour2 = hour.substring(1, 2);
		} else {
			hour1 = "0";
			hour2 = hour.substring(0, 1);
		}

		if (minute.length() == 2) {
			minute1 = minute.substring(0, 1);
			minute2 = minute.substring(1, 2);
		} else {
			minute1 = "0";
			minute2 = minute.substring(0, 1);
		}

		if (second.length() == 2) {
			second1 = second.substring(0, 1);
			second2 = second.substring(1, 2);
		} else {
			second1 = "0";
			second2 = second.substring(0, 1);
		}

		if (milliSecond.length() == 3) {
			mill1 = milliSecond.substring(0, 1);
			mill2 = milliSecond.substring(1, 2);
			mill3 = milliSecond.substring(2, 3);
		} else if (milliSecond.length() == 2) {
			mill1 = "0";
			mill2 = milliSecond.substring(0, 1);
			mill3 = milliSecond.substring(1, 2);
		} else {
			mill1 = "0";
			mill2 = "0";
			mill3 = milliSecond.substring(0, 1);
		}

		hr1 = Integer.parseInt(hour1);
		hr2 = Integer.parseInt(hour2);
		min1 = Integer.parseInt(minute1);
		min2 = Integer.parseInt(minute2);
		sec1 = Integer.parseInt(second1);
		sec2 = Integer.parseInt(second2);
		mll1 = Integer.parseInt(mill1);
		mll2 = Integer.parseInt(mill2);

		g2D.setStroke(stroke);
		g2D.setPaint(Color.cyan);

		num.setSpacing(true, 8);
		num.setSpacing(true, 8);
		if (hr1 == 0 & hr2 == 0) {
			num.drawNumber(1, g2D);
			num.setLocation(40, 10);
			num.drawNumber(2, g2D);
		} else {
			if (!(hr1 == 0)) {
				num.drawNumber(hr1, g2D);
			}
			num.setLocation(40, 10);
			num.drawNumber(hr2, g2D);
		}
		num.setLocation(70, 10);
		num.drawNumber(DigitalNumber.DOTS, g2D);
		num.setLocation(100, 10);
		num.drawNumber(min1, g2D);
		num.setLocation(130, 10);
		num.drawNumber(min2, g2D);
		num.setLocation(160, 10);
		num.drawNumber(DigitalNumber.DOTS, g2D);
		num.setLocation(190, 10);
		num.drawNumber(sec1, g2D);
		num.setLocation(220, 10);
		num.drawNumber(sec2, g2D);
		/*
		 * num.setLocation(250,10); num.drawNumber(DigitalNumber.DOTS,g2D);
		 * num.setLocation(280,10); num.drawNumber(mll1,g2D);
		 * num.setLocation(310,10); num.drawNumber(mll2,g2D);
		 */
		g2D.setPaint(Color.cyan);
		if ((c.get(Calendar.AM_PM)) == Calendar.AM) {
			g2D.drawString("AM", 260, 20);
		} else {
			g2D.drawString("PM", 260, 20);
		}

		String dayOfweek = "";
		switch (c.get(Calendar.DAY_OF_WEEK)) {
		case (Calendar.SUNDAY):
			dayOfweek = "Sunday, ";
			break;
		case (Calendar.MONDAY):
			dayOfweek = "Monday, ";
			break;
		case (Calendar.TUESDAY):
			dayOfweek = "Tuesday, ";
			break;
		case (Calendar.WEDNESDAY):
			dayOfweek = "Wednesday, ";
			break;
		case (Calendar.THURSDAY):
			dayOfweek = "Thursday, ";
			break;
		case (Calendar.FRIDAY):
			dayOfweek = "Friday, ";
			break;
		case (Calendar.SATURDAY):
			dayOfweek = "Saturday, ";
			break;
		}
		String month = "";
		switch (c.get(Calendar.MONTH)) {
		case (Calendar.JANUARY):
			month = "January ";
			break;
		case (Calendar.FEBRUARY):
			month = "February ";
			break;
		case (Calendar.MARCH):
			month = "March ";
			break;
		case (Calendar.APRIL):
			month = "April ";
			break;
		case (Calendar.MAY):
			month = "May ";
			break;
		case (Calendar.JUNE):
			month = "June ";
			break;
		case (Calendar.JULY):
			month = "July ";
			break;
		case (Calendar.AUGUST):
			month = "August ";
			break;
		case (Calendar.SEPTEMBER):
			month = "September ";
			break;
		case (Calendar.OCTOBER):
			month = "October ";
			break;
		case (Calendar.NOVEMBER):
			month = "November ";
			break;
		case (Calendar.DECEMBER):
			month = "December ";
			break;
		}
		int day = c.get(Calendar.DAY_OF_MONTH);
		int year = c.get(Calendar.YEAR);
		Font font = new Font("serif", Font.PLAIN, 24);
		g2D.setFont(font);
		g2D.drawString(dayOfweek + month + day + ", " + year, 10, 80);
	}

}
