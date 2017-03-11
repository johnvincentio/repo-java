package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.SoftBevelBorder;

import com.idc.file.JVFile;

public class LogOptionsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1;

	private DiffallGui m_diffallGui;

	private JCheckBox m_chkGui;
	private JTextField m_logFile;
	private JButton m_btnEditFile;
	private File m_tmpFile;
	public Options getOptions() {return new Options();}

	public LogOptionsPanel (DiffallGui diffallGui) {
		m_diffallGui = diffallGui;

		JPanel paneText = new JPanel();
		paneText.setLayout (new BorderLayout());
		paneText.add (new JLabel ("Log Options"), BorderLayout.WEST);

		JPanel paneOptions = new JPanel();
		paneOptions.setBorder (new SoftBevelBorder (SoftBevelBorder.LOWERED));

		m_chkGui = new JCheckBox ("Log to the App");
		m_chkGui.setSelected (false);
		m_chkGui.setToolTipText ("Shows messages in the messages window.");
		paneOptions.add (m_chkGui);

		m_tmpFile = JVFile.makeWorkingFile (m_diffallGui.getLogDirectory());
		m_logFile = new JTextField (25);
		m_logFile.setEnabled (false);
		m_logFile.setText (m_tmpFile.getPath());
		paneOptions.add (m_logFile);

		m_btnEditFile = new JButton ("Edit Log File");
		m_btnEditFile.addActionListener (this);
		paneOptions.add (m_btnEditFile);

		setLayout (new BorderLayout());
		add (paneText, BorderLayout.NORTH);
		add (paneOptions, BorderLayout.CENTER);
	}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
			if (source == m_btnEditFile) {
				if (m_tmpFile.isFile())
					m_diffallGui.doEditor (m_tmpFile.getPath());
			}
		}
	}

	public class Options {
		public boolean isLogApp() {return m_chkGui.isSelected();}
		public File getLogFile() {return m_tmpFile;}
	}
}
