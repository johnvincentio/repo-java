package com.idc.c2;

import java.util.Calendar;

public class Calculate {
	private Calendar m_calendar = Calendar.getInstance();  // Current time.
	private Result m_result = new Result();
	private int m_update_interval;
	public Calculate (int update_interval) {m_update_interval = update_interval;}

	public Result handle (long currentTime) {
		m_calendar.setTimeInMillis (currentTime);
		int hours   = m_calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = m_calendar.get(Calendar.MINUTE);
		int seconds = m_calendar.get(Calendar.SECOND);
//		int millis  = m_calendar.get(Calendar.MILLISECOND);
//		System.out.println("("+hours+","+minutes+","+seconds+","+millis+")");

		if (seconds < 0 || seconds > m_update_interval)
			m_result.setNothing();
		else if (minutes == 15 || minutes == 45)
			m_result.setQuarterHour();
		else if (minutes == 30)
			m_result.setHalfHour();
		else if (minutes == 0) {
			if (hours == 0) hours = 24;
			m_result.setHour (hours);
		}
		return m_result;
	}
}
