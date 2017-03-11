package com.idc.calendar;

import com.idc.swing.progress.JVProgressBar;
import com.idc.trace.LogHelper;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class App extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private JTextArea m_messagesArea;
	private JTextField m_fileField;
	private JButton m_btnApp;
	private JButton m_btnFile;
	private JLabel m_txtStatus;
	private JVProgressBar m_progress;
	private JFileChooser m_fileChooser;
	private JComboBox m_comboYear;

	private AppThread m_appThread;
	private SubApp m_subApp;
	private boolean m_bAppOver = false;

	public App (String msg) {
		super(msg);
		m_subApp = new SubApp(this);
		m_appThread = new AppThread(this);
		m_appThread.start();
		makeFileChooser();
		setContentPane(makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(300,400);
		pack();
		setVisible(true);
	}
	public AppThread getAppThread() {return m_appThread;}
	public boolean isAppOver() {return m_bAppOver;}
	public static void main(String[] args) {
		new App("Calendar");
	}
	public void makeFileChooser() {
		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		String strCwd = System.getProperty("user.dir");
		m_fileChooser.setCurrentDirectory(new File(strCwd));
	}
	public void doStopClient() {
		m_bAppOver = true;
		m_appThread.setStop();
		try {
			while (m_appThread.isAlive()) {
				LogHelper.info("thread is alive");
				Thread.sleep(10);
				LogHelper.info("Sleeping");
			}
			LogHelper.info("thread is not alive");
		}
		catch (InterruptedException e) {
			LogHelper.info("no sleep");
		}
		LogHelper.info("exiting app...");
		System.exit(0);
	}
	private Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();

		m_fileField = new JTextField(20);
		m_fileField.addActionListener(this);
		m_btnFile = new JButton("File");
		m_btnFile.addActionListener(this);

		m_comboYear = new JComboBox();
		for (int i=2005; i<2020; i++) {
			m_comboYear.addItem(Integer.toString(i));
		}
		m_comboYear.addActionListener(this);

		topPane.add(m_comboYear);
		topPane.add(m_btnFile);
		topPane.add(m_fileField);

		JPanel midPane = new JPanel();
		m_messagesArea = new JTextArea(20,45);
		m_messagesArea.setEditable(false);
//		m_messagesArea.setDragEnabled(true);
		midPane.add(new JScrollPane(m_messagesArea));

		JPanel lowPane = new JPanel();
		m_btnApp = new JButton("Start");
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);

		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JVProgressBar();
		lowPane.add(m_progress);

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn) msg = "Start";
			else msg = "Stop";
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_btnApp.setText(msg);
			}
		}
		);
	}
	public void setFileButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_btnFile.setEnabled(bBtn);
			}
		}
		);
	}
	public void setMessagesArea (final String msg) {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_messagesArea.append(msg);
				m_messagesArea.append("\n");
				m_messagesArea.setCaretPosition(
				m_messagesArea.getText().length());
			}
		}
		);
	}
	public void setStatusMessage (final String msg) {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_txtStatus.setText(msg);
				validate();
			}
		}
		);
	}
	private void setFileField (final String msg) {
		SwingUtilities.invokeLater (
		new Runnable() {
			public void run() {
				m_fileField.setText(msg);
				validate();
			}
		}
		);
	}
	private void StartSelected() {
		LogHelper.info(">>> StartSelected");
		boolean bError = false;
		if (! isFileFieldValid()) bError = true;
		if (bError) {
			setStatusMessage("Enter all the fields and press Start");
			m_progress.setProgressBar(0);
		}
		else {
			m_appThread.setStart();
			setStarted();
		}
		LogHelper.info("<<< StartSelected");
	}
	public void setStarted() {
		setButtonText(false);
		setFileButtonActive(false);
	}
	public void setStopped() {
		setButtonText(true);
		setFileButtonActive(true);
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			LogHelper.info("textfield");
		}
		else if (source instanceof JButton) {
			LogHelper.info("jbutton");
			if (source == m_btnApp) {
				LogHelper.info("btnApp");
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				if (strBtn.equals("Start"))
					StartSelected();
				else if (strBtn.equals("Stop")) {
					m_appThread.setStop();
					setStopped();
				}
			}
			else if (source == m_btnFile) {
				LogHelper.info("m_btnFile");
				int retval = m_fileChooser.showOpenDialog(App.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooser.getSelectedFile();
					setFileField (file.getPath());
				}
			}
		}
		else if (source instanceof JComboBox) {
			LogHelper.info("JComboBox");
			String strYear = (String) m_comboYear.getSelectedItem();
			System.out.println("strYear :"+strYear+":");
		}
		else
			LogHelper.info("else type");
	}
	private String getFileField() {return m_fileField.getText();}
	private boolean isFileFieldValid() {
		String strDir = getFileField();
		if (strDir == null || strDir.length() < 1)        return false;
		File file = new File (strDir);
		if (file.isDirectory()) return false;
		return true;
	}
	public void addWord(String word) {setMessagesArea(word);}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	public void doApp() {
		LogHelper.info(">>> doApp");
		String strFile = getFileField();
		String strYear = (String) m_comboYear.getSelectedItem();

		setStatusMessage("Creating...");
		m_progress.initProgressBar(0,100);
		addWord("");
		addWord("Creating Calendar for "+strYear+" in file "+strFile);
		addWord("");

		m_subApp.doApp(strYear, strFile);
		if (m_subApp.isStopped()) {
			addWord("---------------------------------------");
			addWord("Application stopped by user");
			addWord("---------------------------------------");
			setStatusMessage("Stopped...");
		}
		else {
			addWord("---------------------------------------");
			addWord("Application is complete");
			addWord("---------------------------------------");
			m_progress.setMaxProgressBar();
			setStatusMessage("Finished...");
		}
		LogHelper.info("<<< doApp");
	}
	public void handleProgressIndicator() {m_progress.setProgressBar();}
}
