package com.idc.rgb;

import javax.swing.*;
import java.awt.*;
import org.apache.log4j.*;

public class Apprgb extends JPanel {
	private static final long serialVersionUID = 1;
	private int m_red;
	private int m_green;
	private int m_blue;

	private static Logger debug = Logger.getRootLogger();

	public Apprgb (int r, int g, int b) {
		setBorder(BorderFactory.createLoweredBevelBorder());
		setMyRGB(r, g, b);
	}
	public void setMyRGB (int r, int g, int b) {
		debug.info(">>>Apprgb::setMyRGB");
		m_red = r; m_green = g; m_blue = b; setMyRGB();
		debug.info("<<<Apprgb::setMyRGB");
	}
	private void setMyRGB() {
		debug.info(">>>Apprgb::setMyRGB");
		setBackground(new Color(m_red, m_green, m_blue));
		debug.info("<<<Apprgb::setMyRGB");
	}
	public Dimension getPreferredSize() {return new Dimension(300, 300);}
	public Dimension getMinimumSize() {return getPreferredSize();}
}

