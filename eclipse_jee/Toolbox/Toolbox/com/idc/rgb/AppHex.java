package com.idc.rgb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.*;

public class AppHex implements ActionListener {

	private App m_app;
	private String m_strText;
	private JTextField m_valueText;
	private static Logger debug = Logger.getRootLogger();

	public AppHex(App app, String strText) {
		m_app = app;
		m_strText = strText;
	}
	public void setHexValue (String strValue) {
		m_valueText.setText(strValue);
	}
	public Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JLabel lbl = new JLabel(m_strText);
		pane.add(lbl,BorderLayout.NORTH);

		JPanel pane1c = new JPanel();
		pane1c.setLayout(new FlowLayout());
		m_valueText = new JTextField(6);
		m_valueText.addActionListener(this);
		pane1c.add(m_valueText);
		pane.add(pane1c,BorderLayout.SOUTH);
		return (pane);
	}
	public void actionPerformed (ActionEvent e) {
		debug.info (">>>AppHex::actionPerformed");
		Object source = e.getSource();
		if (source instanceof JTextField) {
			m_valueText.setText(makeString(m_valueText.getText(), '0', 6));
			m_app.setMySlider(m_valueText.getText());
			m_app.setMyRGB();
		}
		debug.info ("<<<AppHex::actionPerformed");
	}
	private String makeString (String strValue, char cChar, int nValue) {
		if (nValue < 1) return "";
		StringBuffer sb = new StringBuffer();
		for (int num=0; num < nValue - strValue.length(); num++)
			sb.append (cChar);
		sb.append(strValue);
		return sb.toString();
	}
}

