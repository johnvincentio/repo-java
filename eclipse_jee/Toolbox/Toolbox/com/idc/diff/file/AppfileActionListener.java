package com.idc.diff.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import com.idc.trace.LogHelper;

public class AppfileActionListener implements ActionListener {
	private Appfile m_appfile;
	public AppfileActionListener (Appfile appfile) {
		m_appfile = appfile;
	}
	public void actionPerformed (ActionEvent e) {
		LogHelper.info(">>> actionPerformed");
		Object source = e.getSource();
		if (source instanceof JTextField) {
			LogHelper.info("JTextField");
			JTextField jt = (JTextField) e.getSource();
			String data = jt.getText();
			m_appfile.handlePastedData (data);
		}
		LogHelper.info("<<< actionPerformed");
	}
}
