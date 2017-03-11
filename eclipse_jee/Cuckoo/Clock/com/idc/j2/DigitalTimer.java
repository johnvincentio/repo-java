package com.idc.j2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;

public class DigitalTimer extends Panel {
	private static final long serialVersionUID = 1;
	private int m_digits;
	private BasicStroke m_stroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
	private long m_startTime = System.currentTimeMillis();

	public DigitalTimer (int digits) {m_digits = digits;}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		long elapsedTime = (System.currentTimeMillis() - m_startTime) / 1000;
		if (elapsedTime > (long) Math.pow (10, m_digits) - 1) {
			m_startTime = System.currentTimeMillis();
			elapsedTime = 0;
		}
//		System.out.println(" elapsedTime "+elapsedTime);
		String seconds = Long.toString(elapsedTime);
//		System.out.println(" seconds "+seconds);

		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(m_stroke);
		g2D.setPaint(Color.cyan);

		DigitalNumber num = new DigitalNumber(10, 10, 20, Color.cyan, Color.black);
		num.setSpacing(true, 8);
		num.setSpacing(true, 8);

		for (int counter = 0; counter < m_digits; counter++) {
//			System.out.println("counter "+counter+" seconds.length() "+seconds.length()+" m_digits "+m_digits);
			int number;
			if (counter > seconds.length() - 1)
				number = -1;
			else
				number = Integer.parseInt(seconds.substring(counter, counter+1));
//			System.out.println("counter "+counter+" number "+number);
			num.setLocation(20 + counter*30, 10);
			num.drawNumber(number, g2D);
		}
	}
}


/*
		for (int counter = 0; counter < seconds.length(); counter++) {
			String str = seconds.substring(counter, counter+1);
//			if (str == null || str.length() < 1) str = "0";
			System.out.println(" counter "+counter+" str :"+str+":");
			int jv = Integer.parseInt(str);
			num.setLocation(20 + counter*30, 10);
			num.drawNumber(jv, g2D);
		}

		int[] sec = new int[MAX_DIGITS];
		for (int counter = MAX_DIGITS; counter >= 0; counter--) {
			sec[counter] = (int) (elapsedTime / 10000);
		}

		int sec1 = (int) (elapsedTime / 10000);
		int sec2 = (int) (elapsedTime - sec1 * 10000) / 1000;
		int sec3 = (int) (elapsedTime - sec1 * 10000 - sec2 * 1000) / 100;
		int sec4 = (int) (elapsedTime - sec1 * 10000 - sec2 * 1000 - sec3 * 100) / 10;
		int sec5 = (int) (elapsedTime - sec1 * 10000 - sec2 * 1000 - sec3 * 100 - sec4 * 10);
		System.out.println("sec1 "+sec1+" sec2 "+sec2+" sec3 "+sec3+" sec4 "+sec4+" sec5 "+sec5);

		g2D.setStroke(stroke);
		g2D.setPaint(Color.cyan);

		num.setSpacing(true, 8);
		num.setSpacing(true, 8);

		num.setLocation(100, 10);
		num.drawNumber(sec1, g2D);
		num.setLocation(130, 10);
		num.drawNumber(sec2, g2D);
		num.setLocation(160, 10);
		num.drawNumber(sec3, g2D);
		num.setLocation(190, 10);
		num.drawNumber(sec4, g2D);
		num.setLocation(220, 10);
		num.drawNumber(sec5, g2D);
*/
