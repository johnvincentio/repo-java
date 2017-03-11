package com.idc.sudoku.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Square extends JPanel {	// models a square in Sudoku
	private static final long serialVersionUID = 5831759966689776250L;

	final static int SQUARE_SIZE = 90;
	private int m_xCoord;
	private int m_yCoord;
	private JTextField m_optionsField;
	private Switches m_switches = new Switches();	// available options for this square
	private JFormattedTextField m_userField;
	private int m_nValue;			// value for this square
	private boolean m_bFixed;		// true if set initially
	
	private Font m_font;
	
	public Square (int i, int j) {
		m_xCoord = i;
		m_yCoord = j;
		m_nValue = 0;
		m_bFixed = false;
		m_font = new Font("TimesRoman", Font.PLAIN, 18);
		setOnePane();
	}
	public boolean isOccupied() {if (m_nValue < 1) return false; return true;}
	public boolean isFixed() {return m_bFixed;}
	
	public String getStringAllSwitches() {return m_switches.getStringAllSwitches();}
	public String getStringSetSwitches() {return m_switches.getStringSetSwitches();}
	public Switches getSwitches() {return m_switches;}
	public void setSwitches (Switches switches) {m_switches.set(switches);}
	public boolean setSwitchesOff (String pattern) {
		return m_switches.setSwitchesOff(pattern);
	}

	public int getIntSquareValue() {return m_nValue;}
	public void setStartIntSquareValue(int num) {
		if (num < 1) num = 0;
		m_bFixed = false;
		setIntSquareValue (num);
		if (num > 0) m_bFixed = true;
	}
	public void setIntSquareValue(int num) {m_nValue = num;}
	public String getStringSquareValue() {
		if (m_nValue < 1) return "";
		return Integer.toString(m_nValue);
	}

	public String getStringSquareUser() {return m_userField.getText();}
	public void setIntSquareUser(int num) {
		String str;
		if (num < 1)
			str = "";
		else
			str = Integer.toString(num);
		m_userField.setText(str);
	}
	public int getIntSquareUser() {
		if (getStringSquareUser().equals("")) return 0;
		return Integer.parseInt(getStringSquareUser());
	}

	public void redraw() {
//		LogHelper.debug (">>> Square::redraw");
		removeAll();
		if (isOccupied())
			setOnePane();
		else
			setTwoPanes();
		validate();
		repaint();
//		LogHelper.debug ("<<< Square::redraw");
	}

	private void setTwoPanes() {
//		LogHelper.debug (">>> Square::setTwoPanes");
		JPanel topPane = new JPanel();
		setLayout(new GridLayout(2,1,0,0));
//		setBorder(BorderFactory.createLoweredBevelBorder());
		
		topPane.setLayout(new GridLayout(1,1,0,0));
		m_optionsField = new JTextField(9);
		m_optionsField.setEditable(false);
		m_optionsField.setText(getStringSetSwitches());
		if (getSwitches().getCountSwitchesSet() == 1) {
			m_optionsField.setHorizontalAlignment(JTextField.CENTER);
			m_optionsField.setFont(m_font);
			m_optionsField.setBackground(new Color(200,255,255));
		}
		else {
			m_optionsField.setForeground(Color.black);
			m_optionsField.setBackground(new Color(200,200,200));
		}
		topPane.add(m_optionsField);

//		MyFormatter myFormatter = new MyFormatter();
//		myFormatter.setAllowsInvalid(false);

		JPanel midPane = new JPanel();
		midPane.setLayout(new GridLayout(1,1,0,0));
		m_userField = new JFormattedTextField();
		m_userField.setText(getStringSquareValue());
		m_userField.setHorizontalAlignment(JTextField.CENTER);
		m_userField.setFont(m_font);
		m_userField.setForeground(Color.black);
//		m_userField.setInputVerifier(new MyInputVerifier());
		midPane.add(m_userField);
		
		add(topPane);
		add(midPane);
//		LogHelper.debug ("<<< Square::setTwoPanes");
	}

	private void setOnePane() {
//		LogHelper.debug (">>> Square::setOnePane");
		setLayout(new GridLayout(1,1,0,0));
//		setBorder(BorderFactory.createLoweredBevelBorder());

		MyFormatter myFormatter = new MyFormatter();
		myFormatter.setAllowsInvalid(false);

		JPanel midPane = new JPanel();
		midPane.setLayout(new GridLayout(1,1,0,0));
		m_userField = new JFormattedTextField(myFormatter);
		m_userField.setColumns(1);
		m_userField.setText(getStringSquareValue());
		m_userField.setHorizontalAlignment(JTextField.CENTER);
		m_userField.setFont(m_font);
		if (isFixed())
			m_userField.setForeground(Color.red);
		else
			m_userField.setForeground(Color.blue);
//		m_userField.setInputVerifier(new MyInputVerifier());
		midPane.add(m_userField);
		add(midPane);
//		LogHelper.debug ("<<< Square::setOnePane");
	}
	
	public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
	public Dimension getMinimumSize() {return getPreferredSize();}
	public int getXCoord() {return this.m_xCoord;}
	public int getYCoord() {return this.m_yCoord;}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", x ").append(m_xCoord);
		buffer.append(", y ").append(m_yCoord);
		buffer.append(", options ").append(getStringAllSwitches());
		buffer.append(", value ").append(m_nValue);
		buffer.append(", userField ").append(getStringSquareUser());
		return buffer.toString();
	}
}
