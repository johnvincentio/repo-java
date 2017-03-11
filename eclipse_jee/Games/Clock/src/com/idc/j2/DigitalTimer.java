package com.idc.j2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;

public class DigitalTimer extends Panel {
	private static final long serialVersionUID = 1;

	private int m_digits;

	private BasicStroke stroke = new BasicStroke(4, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_BEVEL);

	private long m_startTime = System.currentTimeMillis();

	public DigitalTimer(int digits) {
		m_digits = digits;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		long elapsedTime = (System.currentTimeMillis() - m_startTime) / 1000;
		if (elapsedTime > (long) Math.pow(10, m_digits) - 1) {
			m_startTime = System.currentTimeMillis();
			elapsedTime = 0;
		}
		// System.out.println(" elapsedTime "telapsedTime);
		String seconds = Long.toString(elapsedTime);
		// System.out.println(" seconds "tseconds);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(stroke);
		g2D.setPaint(Color.cyan);

		DigitalNumber num = new DigitalNumber(10, 10, 20, Color.cyan,
				Color.black);
		num.setSpacing(true, 8);
		num.setSpacing(true, 8);

		for (int counter = 0; counter < m_digits; counter++) {
			// System.out.println("counter "+counter+" seconds.length()
			// "+seconds.length()+" m_digits "+m_digits);
			int number;
			if (counter > seconds.length() - 1)
				number = -1;
			else
				number = Integer.parseInt(seconds.substring(counter,
						counter + 1));
			// System.out.println("counter "+counter+" number "tnumber);
			num.setLocation(20 + counter * 30, 10);
			num.drawNumber(number, g2D);
		}
	}
}
