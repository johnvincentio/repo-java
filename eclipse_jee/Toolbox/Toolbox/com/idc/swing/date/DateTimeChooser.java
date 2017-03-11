package com.idc.swing.date;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class DateTimeChooser extends JDialog implements ItemListener,
		MouseListener, FocusListener, KeyListener, ActionListener {
	private static final long serialVersionUID = 1;
	private static final String[] MONTHS = new String[] {"January",
		"February","March","April","May","June","July","August",
		"September","October","November","December"};
		private static final String[] DAYS = new String[] {"Sun","Mon",
			"Tue","Wed","Thu","Fri","Sat"};

/** Text color of the days of the weeks, used as column headers in the calendar. */
		private static final Color WEEK_DAYS_FOREGROUND = Color.black;

/** Text color of the days' numbers in the calendar. */
		private static final Color DAYS_FOREGROUND = Color.blue;

/** Background color of the selected day in the calendar. */
		private static final Color SELECTED_DAY_FOREGROUND = Color.white;

/** Text color of the selected day in the calendar. */
		private static final Color SELECTED_DAY_BACKGROUND = Color.blue;

/** Empty border, used when the calendar does not have the focus. */
		private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(1,1,1,1);

/** Border used to highlight the selected day when the calendar has the focus. */
		private static final Border FOCUSED_BORDER = BorderFactory.createLineBorder(Color.yellow,1);

/** First year that can be selected. */
		private static final int FIRST_YEAR = 1900;

/** Last year that can be selected. */
		private static final int LAST_YEAR = 2100;

/** Auxiliary variable to compute dates. */
		private GregorianCalendar m_calendar;

/** Calendar, as a matrix of labels. The first row represents the
first week of the month, the second row, the second week, and
so on. Each column represents a day of the week, the first is
Sunday, and the last is Saturday. The label's text is the
number of the corresponding day. */
		private JLabel[][] m_days;

/** Day selection control. It is just a panel that can receive the
focus. The actual user interaction is driven by the
<code>DateChooser</code> class. */
		private FocusablePanel m_daysGrid;

		private JComboBox m_month;
		private JComboBox m_year;
		private JComboBox m_hour;
		private JComboBox m_minute;
		private JComboBox m_second;

		private JButton m_ok;
		private JButton m_cancel;

/** Day of the week (0=Sunday) corresponding to the first day of
the selected month. Used to calculate the position, in the
calendar ({@link #days}), corresponding to a given day. */
		private int m_offset;

/** Last day of the selected month. */
		private int m_lastDay;

/** Selected day. */
		private JLabel m_day;

/** <code>true</code> if the "Ok" button was clicked to close the
dialog box, <code>false</code> otherwise. */
		private boolean m_bOkClicked;

		private int m_nType;
		public DateTimeChooser(int nType, Dialog owner, String title) {
			super(owner, title, true);
			m_nType = nType;
			construct();
		}
		public DateTimeChooser(int nType, Dialog owner) {
			super(owner, true);
			m_nType = nType;
			construct();
		}
		public DateTimeChooser(int nType, Frame owner, String title) {
			super(owner, title, true);
			m_nType = nType;
			construct();
		}
		public DateTimeChooser(int nType, Frame owner) {
			super(owner, true);
			m_nType = nType;
			construct();
		}

/**
* Custom panel that can receive the focus. Used to implement the
* calendar control.
**/
		private static class FocusablePanel extends JPanel {
			private static final long serialVersionUID = 1;
/**
* Constructs a new <code>FocusablePanel</code> with the given
* layout manager.
*
* @param layout layout manager
**/
		public FocusablePanel( LayoutManager layout ) {
			super( layout );
		}

/**
* Always returns <code>true</code>, since
* <code>FocusablePanel</code> can receive the focus.
*
* @return <code>true</code>
**/
		public boolean isFocusTraversable() {
			return true;
		}
	}

	private void construct() {
		System.out.println(">>> DateTimeChooser:construct");
		m_calendar = new GregorianCalendar();
		Container dialog = getContentPane();

		JPanel topPane = new JPanel();
		m_hour = new JComboBox();
		for (int i=0; i<=23; i++) m_hour.addItem(Integer.toString(i));
		m_hour.addItemListener(this);
		topPane.add(new JLabel("Hour"));
		topPane.add(m_hour);

		m_minute = new JComboBox();
		for (int i=0; i<=59; i++) m_minute.addItem(Integer.toString(i));
		m_minute.addItemListener(this);
		topPane.add(new JLabel("Minute"));
		topPane.add(m_minute);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_second = new JComboBox();
		for (int i=0; i<=59; i++) m_second.addItem(Integer.toString(i));
		m_second.addItemListener(this);
		topPane.add(new JLabel("Second"));
		topPane.add(m_second);
		if (m_nType == DateType.DATE_TIME || m_nType == DateType.TIME)
		dialog.add("North", topPane);

		m_month = new JComboBox(MONTHS);
		m_month.addItemListener(this);

		m_year = new JComboBox();
		for (int i=FIRST_YEAR; i<=LAST_YEAR; i++) m_year.addItem(Integer.toString(i));
		m_year.addItemListener(this);

		m_days = new JLabel[7][7];
		for (int i=0; i<7; i++) {
			m_days[0][i] = new JLabel(DAYS[i],JLabel.RIGHT);
			m_days[0][i].setForeground(WEEK_DAYS_FOREGROUND);
		}
		for (int i=1; i<7; i++)
			for (int j=0; j<7; j++) {
				m_days[i][j] = new JLabel(" ",JLabel.RIGHT);
				m_days[i][j].setForeground(DAYS_FOREGROUND);
				m_days[i][j].setBackground(SELECTED_DAY_BACKGROUND);
				m_days[i][j].setBorder(EMPTY_BORDER);
				m_days[i][j].addMouseListener(this);
			}
		m_ok = new JButton("Ok");
		m_ok.addActionListener( this );
		m_cancel = new JButton("Cancel");
		m_cancel.addActionListener( this );

		JPanel monthYear = new JPanel();
		monthYear.add(m_month);
		monthYear.add(m_year);

		m_daysGrid = new FocusablePanel(new GridLayout(7,7,5,0));
		m_daysGrid.addFocusListener(this);
		m_daysGrid.addKeyListener(this);
		for (int i=0; i<7; i++)
			for (int j=0; j<7; j++)
				m_daysGrid.add(m_days[i][j]);
		m_daysGrid.setBackground(Color.white);
		m_daysGrid.setBorder(BorderFactory.createLoweredBevelBorder());
		JPanel daysPanel = new JPanel();
		daysPanel.add(m_daysGrid);

		midPane.add(monthYear,BorderLayout.NORTH);
		midPane.add(daysPanel,BorderLayout.CENTER);

		if (m_nType == DateType.DATE_TIME || m_nType == DateType.DATE)
			dialog.add("Center", midPane);

		JPanel lowPane = new JPanel();
		lowPane.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		buttons.add(m_ok);
		buttons.add(m_cancel);
		lowPane.add(buttons,BorderLayout.CENTER);
		dialog.add("South", lowPane);

		pack();
		setResizable( false );
		System.out.println("<<< DateTimeChooser:construct");
	}

/**
* Sets the selected day. The day is specified as the label
* control, in the calendar, corresponding to the day to select.
*
* @param newDay day to select
**/
	private void setSelected(JLabel newDay) {
		System.out.println(">>> DateTimeChooser:setSelected (JLabel)");
		if (m_day != null ) {
			m_day.setForeground(DAYS_FOREGROUND);
			m_day.setOpaque(false);
			m_day.setBorder(EMPTY_BORDER);
		}
		m_day = newDay;
		m_day.setForeground(SELECTED_DAY_FOREGROUND);
		m_day.setOpaque(true);
		if (m_daysGrid.hasFocus())
			m_day.setBorder(FOCUSED_BORDER);
		System.out.println("<<< DateTimeChooser:setSelected");
	}

/**
* Sets the selected day. The day is specified as the number of
* the day, in the month, to selected. The function compute the
* corresponding control to select.
*
* @param newDay day to select
**/
	private void setSelected(int newDay) {
		System.out.println("--- DateTimeChooser:setSelected (int)");
		setSelected(m_days[(newDay+m_offset-1)/7+1][(newDay+m_offset-1)%7]);
	}

/**
* Updates the calendar. This function updates the calendar panel
* to reflect the month and year selected. It keeps the same day
* of the month that was selected, except if it is beyond the last
* day of the month. In this case, the last day of the month is
* selected.
**/
	private void update() {
		System.out.println(">>> update");
		int iday = getSelectedDay();
		for (int i=0; i<7; i++) {
			m_days[1][i].setText(" ");
			m_days[5][i].setText(" ");
			m_days[6][i].setText(" ");
		}
		m_calendar.set(Calendar.DATE, 1);
		m_calendar.set(Calendar.MONTH, m_month.getSelectedIndex()+Calendar.JANUARY);
		m_calendar.set(Calendar.YEAR, m_year.getSelectedIndex()+FIRST_YEAR);

		m_offset = m_calendar.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY;
		m_lastDay = m_calendar.getActualMaximum(Calendar.DATE);
		for (int i=0; i<m_lastDay; i++)
			m_days[(i+m_offset)/7+1][(i+m_offset)%7].setText( String.valueOf(i+1));
		if (iday != -1) {
			if (iday > m_lastDay)
			iday = m_lastDay;
			setSelected(iday);
		}
		System.out.println("<<< update");
	}

/**
* Called when the "Ok" button is pressed. Just sets a flag and
* hides the dialog box.
**/
	public void actionPerformed(ActionEvent e) {
		System.out.println(">>> DateTimeChooser:actionPerformed");
		if (e.getSource() == m_ok)
			m_bOkClicked = true;
		setVisible(false);
		System.out.println("<<< DateTimeChooser:actionPerformed");
	}

/**
* Called when the calendar gains the focus. Just re-sets the
* selected day so that it is redrawn with the border that
* indicate focus.
**/
	public void focusGained(FocusEvent e) {
		System.out.println(">>> DateTimeChooser:focusGained");
		setSelected(m_day);
		System.out.println("<<<DateTimeChooser:focusGained");
	}

/**
* Called when the calendar loses the focus. Just re-sets the
* selected day so that it is redrawn without the border that
* indicate focus.
**/
	public void focusLost(FocusEvent e) {
		System.out.println(">>> DateTimeChooser:focusLost");
		setSelected(m_day);
		System.out.println("<<< DateTimeChooser:focusLost");
	}

/**
* Called when a new month or year is selected. Updates the calendar
* to reflect the selection.
**/
	public void itemStateChanged(ItemEvent e) {
		System.out.println(">>> DateTimeChooser:itemStateChanged");
		update();
		System.out.println("<<< DateTimeChooser:itemStateChanged");
	}

/**
* Called when a key is pressed and the calendar has the
* focus. Handles the arrow keys so that the user can select a day
* using the keyboard.
**/
	public void keyPressed(KeyEvent e) {
		int iday = getSelectedDay();
		switch ( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				if ( iday > 1 )
					setSelected( iday-1 );
				break;
			case KeyEvent.VK_RIGHT:
				if ( iday < m_lastDay )
					setSelected( iday+1 );
				break;
			case KeyEvent.VK_UP:
				if ( iday > 7 )
					setSelected( iday-7 );
				break;
			case KeyEvent.VK_DOWN:
				if ( iday <= m_lastDay-7 )
					setSelected( iday+7 );
				break;
		}
	}

/**
* Called when the mouse is clicked on a day in the
* calendar. Selects the clicked day.
**/
	public void mouseClicked(MouseEvent e) {
		System.out.println(">>> DateTimeChooser:mouseClicked");
		JLabel day = (JLabel)e.getSource();
		if ( !day.getText().equals(" ") )
			setSelected( day );
		m_daysGrid.requestFocus();
		System.out.println("<<< DateTimeChooser:mouseClicked");
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	private int getSelectedDay() {
		System.out.println(">>> DateTimeChooser:getSelectedDay");
		if (m_day == null) return -1 ;
		try {
			return Integer.parseInt(m_day.getText());
		} catch (NumberFormatException e) {}
		System.out.println("<<< DateTimeChooser:getSelectedDay");
		return -1;
	}

	public Date select(Date pDateTime) {
		System.out.println(">>> DateTimeChooser:select");
		m_calendar.setTime(pDateTime);
		int nDay = m_calendar.get(Calendar.DATE);
		int nMonth = m_calendar.get(Calendar.MONTH);
		int nYear = m_calendar.get(Calendar.YEAR);
		int nHour = m_calendar.get(Calendar.HOUR_OF_DAY);
		int nMinute = m_calendar.get(Calendar.MINUTE);
		int nSecond = m_calendar.get(Calendar.SECOND);

		System.out.println("day "+nDay);
		System.out.println("month "+nMonth);
		System.out.println("year "+nYear);
		System.out.println("hour "+nHour);
		System.out.println("minute "+nMinute);
		System.out.println("second "+nSecond);

		m_year.setSelectedIndex(nYear-FIRST_YEAR);
		m_month.setSelectedIndex(nMonth-Calendar.JANUARY);
		setSelected(nDay);
		m_hour.setSelectedIndex(nHour);
		m_minute.setSelectedIndex(nMinute);
		m_second.setSelectedIndex(nSecond);
		m_bOkClicked = false;
		setVisible(true);
		if (m_bOkClicked) {
			m_calendar.set(Calendar.DATE, getSelectedDay());
			System.out.println("m_month "+m_month.getSelectedIndex());
			System.out.println("m_year "+m_year.getSelectedIndex());
			System.out.println("m_day "+getSelectedDay());
			System.out.println("m_hour "+m_hour.getSelectedIndex());
			System.out.println("m_minute "+m_minute.getSelectedIndex());
			System.out.println("m_second "+m_second.getSelectedIndex());

			m_calendar.set(Calendar.MONTH, m_month.getSelectedIndex()+Calendar.JANUARY);
			m_calendar.set(Calendar.YEAR, m_year.getSelectedIndex()+FIRST_YEAR);
			m_calendar.set(Calendar.HOUR_OF_DAY, m_hour.getSelectedIndex());
			m_calendar.set(Calendar.MINUTE, m_minute.getSelectedIndex());
			m_calendar.set(Calendar.SECOND, m_second.getSelectedIndex());
		}
		else {
			return null;
		}
		return m_calendar.getTime();
	}
	public Date select() {return select(new Date());}
}
