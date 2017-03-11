package com.idc.a1;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

class TextClock1 extends JFrame {
	private static final long serialVersionUID = 1;

	private JTextField _timeField; // set by timer listener

	public TextClock1() {
		// ... Set characteristics of text field that shows the time.
		_timeField = new JTextField(5);
		_timeField.setEditable(false);
		_timeField.setFont(new Font("sansserif", Font.PLAIN, 48));

		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		content.add(_timeField);

		this.setContentPane(content);
		this.setTitle("Text Clock 1");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);

		// ... Create timer which calls action listener every second..
		// Use full package qualification for javax.swing.Timer
		// to avoid potential conflicts with java.util.Timer.
		javax.swing.Timer t = new javax.swing.Timer(1000, new ClockListener());
		t.start();
	}

	class ClockListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ... Whenever this is called, get the current time and
			// display it in the textfield.
			Calendar now = Calendar.getInstance();
			int h = now.get(Calendar.HOUR_OF_DAY);
			int m = now.get(Calendar.MINUTE);
			int s = now.get(Calendar.SECOND);
			_timeField.setText("" + h + ":" + m + ":" + s);
			// ... The following is an easier way to format the time,
			// but requires knowing how to use the format method.
			// _timeField.setText(String.format("%1$tH:%1$tM:%1$tS", now));
		}
	}

	public static void main(String[] args) {
		JFrame clock = new TextClock1();
		clock.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clock.setVisible(true);
	}
}
