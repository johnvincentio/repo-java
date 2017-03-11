package com.idc.swing.date;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

public class DemoDateTime extends JFrame implements PropertyChangeListener {
	private static final long serialVersionUID = 1;
	private DateTimeButton startDateTimeButton;
	private DateTimeButton endDateTimeButton;
	private DateButton startDateButton;
	private DateButton endDateButton;
	private TimeButton startTimeButton;
	private TimeButton endTimeButton;

	private DemoDateTime() {
		super("DateChooser demo");
		addWindowListener (new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}
		);
		startDateTimeButton = new DateTimeButton();
		startDateTimeButton.addPropertyChangeListener("date", this);
		endDateTimeButton = new DateTimeButton();
		endDateTimeButton.addPropertyChangeListener("date", this);

		startDateButton = new DateButton();
		startDateButton.addPropertyChangeListener("date", this);
		endDateButton = new DateButton();
		endDateButton.addPropertyChangeListener("date", this);

		startTimeButton = new TimeButton();
		startTimeButton.addPropertyChangeListener("date", this);
		endTimeButton = new TimeButton();
		endTimeButton.addPropertyChangeListener("date", this);

		JPanel topPane = new JPanel();
		topPane.setLayout(new GridLayout(2,2));
		topPane.add(new JLabel("Start date/time"));
		topPane.add(startDateTimeButton);
		topPane.add(new JLabel("End date/time"));
		topPane.add(endDateTimeButton);

		JPanel midPane = new JPanel();
		midPane.setLayout(new GridLayout(2,2));
		midPane.add(new JLabel("Start date"));
		midPane.add(startDateButton);
		midPane.add(new JLabel("End date"));
		midPane.add(endDateButton);

		JPanel lowPane = new JPanel();
		lowPane.setLayout(new GridLayout(2,2));
		lowPane.add(new JLabel("Start time"));
		lowPane.add(startTimeButton);
		lowPane.add(new JLabel("End time"));
		lowPane.add(endTimeButton);

		getContentPane().setLayout(new GridLayout(3,1));
		getContentPane().add(topPane);
		getContentPane().add(midPane);
		getContentPane().add(lowPane);
		pack();
		setResizable(false);
	}
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof DateTimeButton) {
			DateTimeButton db = (DateTimeButton)e.getSource();
			if (db == startDateTimeButton)
				System.out.print("Start date time changed: ");
			else
				System.out.print("End date time changed: ");
			System.out.println(db.getDateTime());
		}
		else if (e.getSource() instanceof DateButton) {
			DateButton db = (DateButton)e.getSource();
			if (db == startDateButton)
				System.out.print("Start date changed: ");
			else
				System.out.print("End date changed: ");
			System.out.println(db.getDateTime());
		}
		else if (e.getSource() instanceof TimeButton) {
			TimeButton db = (TimeButton)e.getSource();
			if (db == startTimeButton)
				System.out.print("Start time changed: ");
			else
				System.out.print("End time changed: ");
			System.out.println(db.getDateTime());
		}
	}
	public static void main(String[] args) {(new DemoDateTime()).setVisible(true);}
}
