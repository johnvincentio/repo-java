package com.idc.knight.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class SquareGui extends JPanel {
	private static final long serialVersionUID = -2556046304372925262L;

	private static final Font MY_EMPTY_FONT = new Font("TimesRoman", Font.PLAIN, 18);
	private static final Font MY_DEFAULT_FONT = new Font("TimesRoman", Font.PLAIN, 12);
	private static final Font MY_CURRENT_FONT = new Font("TimesRoman", Font.PLAIN, 60);

	private static final Color MY_FORWARD_COLOR = new Color (0, 0, 205);
	private static final Color MY_BACKWARD_COLOR = new Color (204, 0, 204);

	private final static int SQUARE_SIZE = 90;

	private int m_xCoord;
	private int m_yCoord;
	private JTextField m_userField = new JTextField(3);
	private int m_nMoveCounter;			// value for this square
	
	public SquareGui (int i, int j, SquareKeyAdapter squareKeyAdapter) {
		m_xCoord = i;
		m_yCoord = j;
		m_nMoveCounter = -1;

		setLayout (new GridLayout (1, 1, 0, 0));
	
		JPanel midPane = new JPanel();
		midPane.setLayout (new GridLayout (1, 1, 0, 0));
		m_userField.setHorizontalAlignment (JTextField.CENTER);

		m_userField.setEditable (false);
		m_userField.addKeyListener (squareKeyAdapter);
		setMoveCounter();
		midPane.add (m_userField);
		add (midPane);
	}
	public void setMoveCounter() {
		setMoveCounter (-1, 0);
	}
	public void setMoveCounter (int move_counter, int m_current_move_counter) {
//		System.out.println(">>>> SquareGui::setMoveCounter; i, j, move_counter = "+m_xCoord+","+m_yCoord+","+move_counter+" m_current_move_counter "+m_current_move_counter);

		m_nMoveCounter = move_counter;
		if (m_nMoveCounter > -1) {
			m_userField.setEnabled (true);
			m_userField.setText (Integer.toString (m_nMoveCounter));

			int difference = m_nMoveCounter - m_current_move_counter;
			if (difference < 0) difference = -difference;

			if (m_nMoveCounter == m_current_move_counter) {			// same
				m_userField.setForeground (Color.red);
				m_userField.setFont (MY_CURRENT_FONT);
			}
			else {
				int font_size = 32;
				if (m_nMoveCounter > m_current_move_counter && difference <= 5) {
					m_userField.setForeground (MY_FORWARD_COLOR);
					font_size += 2 * (5 - difference);
					m_userField.setFont (new Font("TimesRoman", Font.PLAIN, font_size));
				}
				else if (m_nMoveCounter < m_current_move_counter && difference <= 5) {
					m_userField.setForeground (MY_BACKWARD_COLOR);
					font_size += 2 * (5 - difference);
					m_userField.setFont (new Font("TimesRoman", Font.PLAIN, font_size));
				}
				else {
					m_userField.setForeground (Color.gray);
					m_userField.setFont (MY_DEFAULT_FONT);
				}
			}
		}
		else {
			String str = "(" + m_xCoord + "," + m_yCoord + "," + m_nMoveCounter + ")";
			m_userField.setEnabled (false);
			m_userField.setText (str);
			m_userField.setForeground (Color.gray);
			m_userField.setFont (MY_EMPTY_FONT);
		}
		repaint();
//		System.out.println("<<< SquareGui::setMoveCounter; i, j, move_counter = "+m_xCoord+","+m_yCoord+","+move_counter);
	}
	
	public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
	public Dimension getMinimumSize() {return getPreferredSize();}
	public int getXCoord() {return this.m_xCoord;}
	public int getYCoord() {return this.m_yCoord;}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(", x ").append(m_xCoord);
		buffer.append(", y ").append(m_yCoord);
		buffer.append(", move_counter ").append(m_nMoveCounter);
		return buffer.toString();
	}
}
