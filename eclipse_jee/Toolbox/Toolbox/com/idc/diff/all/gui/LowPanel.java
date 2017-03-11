package com.idc.diff.all.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LowPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1;

	private DiffallGui m_diffallGui;

	private JButton m_btnStart;
	private JLabel m_txtStatus;
	private JProgressBar m_progress;
	private JTextField m_editField; 
	private JButton m_btnEdit;
	private JButton m_btnClear;
	private JButton m_btnClearText;

	private static final int MAX_CNTR = 1000;
	private int m_cntr = 0;

	public LowPanel (DiffallGui diffallGui) {
		m_diffallGui = diffallGui;

		m_btnStart = new JButton("Start");
		m_btnStart.setDefaultCapable(true);
		m_btnStart.addActionListener(this);
		add(m_btnStart);
						
		m_txtStatus = new JLabel();
		add (m_txtStatus);
		m_progress = new JProgressBar();
		add(m_progress);
		m_editField = new JTextField(35);
		m_editField.addActionListener(this);
		m_btnEdit = new JButton("Edit");
		m_btnEdit.addActionListener(this);
		m_btnClear = new JButton("Clear");
		m_btnClear.addActionListener(this);
		m_btnClearText = new JButton("Clear Text");
		m_btnClearText.addActionListener(this);
		add(m_editField);		
		add(m_btnEdit);
		add(m_btnClear);
		add(m_btnClearText);
	}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
//			LogHelper.info("jbutton");
			if(source == m_btnStart) {
//				LogHelper.info("m_btnStart");
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				if (strBtn.equals("Start"))
					m_diffallGui.startSelected();
				else if (strBtn.equals("Stop")) {
					m_diffallGui.stopSelected();
				}							
			}
			else if (source == m_btnEdit) {
//				LogHelper.info("btnEdit");
				String strFile = m_editField.getText().trim();
				if(strFile.length() > 0) {
					File file = new File(strFile);
					if (file.isFile())
						m_diffallGui.doEditor(strFile);
				}
			}
			else if (source == m_btnClear) {
//				LogHelper.info("btnClear");
				setEditField("");
			}
			else if (source == m_btnClearText) {
//				LogHelper.info("btnClearText");
				m_diffallGui.getMessagesArea().clear();
			}
		}
//		else
//			LogHelper.info("else type");
	}

	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn)
			msg = "Start";
		else
			msg = "Stop";
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnStart.setText (msg);
				}
			}
		);
	}
	public void setStatusMessage (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_txtStatus.setText(msg);
					m_diffallGui.validate();
				}
			}
		);		
	}

	public void initProgressBar() {
		final int iMin = 0;
		final int iMax = MAX_CNTR;
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_progress.setMinimum (iMin);
					m_progress.setMaximum (iMax);
					m_progress.setValue (iMin);
					m_progress.setStringPainted (false);	// true for %age
//					m_progress.setIndeterminate(true);		// jdk 1.4
				}
			}
		);		
	}
	public void setProgressBar (final int value) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_progress.setValue(value);
				}
			}
		);
	}
	public void setProgressBar() {
		m_cntr++;
		if (m_cntr > MAX_CNTR) m_cntr = 1;
		setProgressBar (m_cntr);		
	}
	public void setProgressBarMax () {setProgressBar (MAX_CNTR);}

	public void setEditField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_editField.setText(msg);
				}
			}
		);		
	}
	public void setEditButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnEdit.setEnabled(bBtn);
				}
			}
		);
	}
	public void setClearButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnClear.setEnabled(bBtn);
				}
			}
		);
	}
	public void setClearTextButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnClearText.setEnabled(bBtn);
				}
			}
		);
	}
}
