package com.idc.a1;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JTextField;

class Clock extends JTextField {
	private static final long serialVersionUID = 1;

	javax.swing.Timer m_t;

	public Clock() {
		// ... Set some attributes.
		setColumns(6);
		setFont(new Font("sansserif", Font.PLAIN, 48));

		// ... Create a 1-second timer.
		m_t = new javax.swing.Timer(1000, new ClockTickAction());
		m_t.start(); // Start the timer
	}

	private class ClockTickAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ... Get the current time.
			Calendar now = Calendar.getInstance();
			int h = now.get(Calendar.HOUR_OF_DAY);
			int m = now.get(Calendar.MINUTE);
			int s = now.get(Calendar.SECOND);
			setText("" + h + ":" + m + ":" + s);
		}
	}
}
