package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

public class MessageOptionsPanel extends JPanel {
	private static final long serialVersionUID = 1;

	private JCheckBox m_chkShowMessages;
	public Options getOptions() {return new Options();}

	public MessageOptionsPanel() {
		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Message Options"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));
		paneOptions.setLayout (new GridLayout (1, 1, 0, 0));

		m_chkShowMessages = new JCheckBox ("Show Messages");
		m_chkShowMessages.setSelected (true);
		m_chkShowMessages.setToolTipText ("Shows useful messages.");
		paneOptions.add (m_chkShowMessages);

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}

	public class Options {
		public boolean isShowMessages() {return m_chkShowMessages.isSelected();}
	}
}
