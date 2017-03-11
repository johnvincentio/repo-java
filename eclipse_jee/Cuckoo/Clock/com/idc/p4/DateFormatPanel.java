package com.idc.p4;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JPanel;

/**
 * This JPanel subclass uses the DateFormat class to display the current time.
 */
class DateFormatPanel extends JPanel {
	private static final long serialVersionUID = 1;

	DateFormat fDateFormat = null;

	boolean fFirstPass = true;

	int fMsgX = 0, fMsgY = 0;

	Font fFont = new Font("Serif", Font.BOLD, 24);

	/** Get the DateFormat object with the default time style. * */
	DateFormatPanel() {
		fDateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT);
	} // ctor

	/** Draw the time string on the panel center. * */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Get current date object
		Date now = new Date();

		// Format the time string.
		String date_out = fDateFormat.format(now);

		// Use our choice for the font.
		g.setFont(fFont);

		// Do the size and placement calculations only for the
		// first paint (assumes the applet window is never resized.)
		if (fFirstPass) {

			// Get measures needed to center the message
			FontMetrics fm = g.getFontMetrics();

			// How many pixels wide is the string
			int msg_width = fm.stringWidth(date_out);

			// Use the string width to find the starting point
			fMsgX = getSize().width / 2 - msg_width / 2;

			// How far above the baseline can the font go?
			int ascent = fm.getMaxAscent();

			// How far below the baseline?
			int descent = fm.getMaxDescent();

			// Use the vertical height of this font to find
			// the vertical starting coordinate
			fMsgY = getSize().height / 2 - descent / 2 + ascent / 2;
		}
		g.drawString(date_out, fMsgX, fMsgY);
		fFirstPass = false;
	} // paintComponent

} // class DateFormatPanel
