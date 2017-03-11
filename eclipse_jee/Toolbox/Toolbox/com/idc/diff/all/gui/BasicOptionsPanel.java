package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class BasicOptionsPanel extends JPanel {
	private static final long serialVersionUID = 1;

	private JCheckBox m_chkMakeReadWrite;
	public Options getOptions() {return new Options ();}

	public BasicOptionsPanel() {
		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Basic Options"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));
		paneOptions.setLayout (new GridLayout (1, 1, 0, 0));
		
		m_chkMakeReadWrite = new JCheckBox ("R/W Privs");
		m_chkMakeReadWrite.setToolTipText ("Make files R/W (so app can delete them).");
		paneOptions.add (m_chkMakeReadWrite);

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}

	public class Options {
		public boolean isMakeReadWrite() {return m_chkMakeReadWrite.isSelected();}
	}
}
