package com.idc.file.clean.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.file.clean.gui.Extension;
import com.idc.file.clean.gui.Extensions;
import com.idc.swing.JVMessagesArea;
import com.idc.swing.progress.JVProgressBar;
import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class FilesCleanGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;

	private JButton m_btnDir;
	private JTextField m_dirField;
	private JButton m_btnStart;

	private JVMessagesArea m_messagesArea;

	private JLabel m_txtStatus;
	private JVProgressBar m_progress;
	
	private AppThread m_appThread = null;
	private FilesClean m_filesClean;
	private Extensions m_extDelete = new Extensions();

	public FilesCleanGui (String msg, String[] args) {
		super (msg);
		LogHelper.info ("starting app");
		LogHelper.info("args "+args.length);
		File startFile = null;
		if (args.length > 0) startFile = new File (args[0]);

		setupExtensions();
		setContentPane (makeContentPane (startFile));
		m_filesClean = new FilesClean (this);
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize (700,900);
		pack();
		setVisible (true);
	}
	public AppThread getAppThread() {return m_appThread;}
	public static void main(String[] args) {new FilesCleanGui ("FilesCleanGui", args);}

	private Container makeContentPane (File startFile) {
		JPanel topPane = new JPanel();
		m_btnDir = new JButton ("Directory");
		m_btnDir.addActionListener (this);
		m_dirField = new JTextField (50);
		if (startFile != null) m_dirField.setText (startFile.getPath());
		m_dirField.addActionListener (this);
		m_btnStart = new JButton ("Start");
		m_btnStart.setDefaultCapable (true);
		m_btnStart.addActionListener (this);
		topPane.add (m_btnDir);
		topPane.add (m_dirField);
		topPane.add (m_btnStart);
		
		JPanel midPane = new JPanel();
		midPane.setLayout (new BorderLayout());
		midPane.setBorder (BorderFactory.createEmptyBorder (10, 20, 10, 20));
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane (30, 100);
		midPane.add (new JScrollPane (m_messagesArea.getTextArea()), BorderLayout.CENTER);

		JPanel lowPane = new JPanel();
		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JVProgressBar();
		lowPane.add (m_progress);
	
		JPanel pane = new JPanel();
		pane.setLayout (new BorderLayout());
		pane.add (topPane,BorderLayout.NORTH);
		pane.add (midPane,BorderLayout.CENTER);
		pane.add (lowPane,BorderLayout.SOUTH);			
		return pane;
	}
	public JVMessagesArea getMessagesArea() {return m_messagesArea;}
	public void setButtonText (boolean bBtn) {
		final String msg = bBtn ? "Start" : "Stop";
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnStart.setText(msg);
				}
			}
		);
	}
	public void setDirButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnDir.setEnabled(bBtn);
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
	private void setDirField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_dirField.setText(msg);
					validate();
				}
			}
		);		
	}

	public void setStarted() {
		setButtonText (false);
		setDirButtonActive (false);
	}
	public void setStopped() {
		setButtonText (true);
		setDirButtonActive (true);
	}		
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JButton) {
//			LogHelper.info("jbutton");
			if (source == m_btnStart) {
//				LogHelper.info("m_btnStart");
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				if (strBtn.equals ("Start"))
					startSelected();
				else if (strBtn.equals ("Stop")) {
					m_appThread.setStop();
					setStopped();
				}							
			}
			else if (source == m_btnDir) {
//				LogHelper.info("btnDirBase");
				JFileChooser m_fileChooser = new JFileChooser();
				m_fileChooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
				m_fileChooser.setCurrentDirectory (new File (getDirField()));
				int retval = m_fileChooser.showOpenDialog (FilesCleanGui.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooser.getSelectedFile();
					setDirField (file.getPath());
				}
			}
		}
	}

	private String getDirField() {return m_dirField.getText();}
	private boolean isDirFieldValid() {
		String strDir = getDirField();
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}

	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	public void handleProgressIndicator() {
		m_progress.setProgressBar();		
	}
	public void handleLine (final String line) {
		String str = line;
		JVString.replace (str, "\t","	");
		m_messagesArea.add (str);
	}
	private void startSelected() {
		LogHelper.info(">>> startSelected");
		boolean bError = false;
		if (! isDirFieldValid()) bError = true;
		if (bError) {
			setStatusMessage("Enter all the fields and press Start");
			m_progress.setProgressBar(0);
		}
		else {
			m_appThread = new AppThread(this);
			m_appThread.start();
			m_appThread.setStart();
			setStarted();
		}
		LogHelper.info("<<< startSelected");		
	}
	public void doApp() {
		LogHelper.info(">>> doApp");
		setStatusMessage("working...");
		m_progress.initProgressBar ();
		m_filesClean.doApp (new File (getDirField()));
		if (m_filesClean.isSearchStopped()) {
			m_messagesArea.add ("---------------------------------------");	
			m_messagesArea.add ("Cleaning stopped by user");
			m_messagesArea.add ("---------------------------------------");
			setStatusMessage("Stopped...");
		}
		else {		
			m_messagesArea.add ("---------------------------------------");	
			m_messagesArea.add ("Cleaning is complete");
			m_messagesArea.add ("---------------------------------------");
			m_progress.setMaxProgressBar();
			setStatusMessage("Finished...");		
		}
		LogHelper.info("<<< doApp");
	}
	public void doStopClient() {
		if (m_appThread != null) {
			m_appThread.setStop();
			try {
				while(m_appThread.isAlive()) {
					LogHelper.info("thread is alive");
					Thread.sleep(10);
					LogHelper.info("Sleeping");
				}
				LogHelper.info("thread is not alive");
			}
			catch(InterruptedException e) {
				LogHelper.info("no sleep");
			}
		}
		m_appThread = null;
		LogHelper.info("exiting app...");
		System.exit(0);
	}

	public Extensions getDeleteExtensions() {return m_extDelete;}
	private void setupExtensions() {
		m_extDelete.add (new Extension(".*?\\.bak", "*.bak", true));
		m_extDelete.add (new Extension(".*?\\.checkedout", "*.checkedout", true));
		m_extDelete.add (new Extension(".*?\\.class", "*.class", true));
		m_extDelete.add (new Extension(".*?\\.contrib", "*.contrib", true));
		m_extDelete.add (new Extension(".*?\\.dat", "*.dat", true));
		m_extDelete.add (new Extension(".*?\\.dnx", "*.dnx", true));
		m_extDelete.add (new Extension(".*?\\.hijacked", "*.hijacked", true));
		m_extDelete.add (new Extension(".*?\\.index", "*.index", true));
		m_extDelete.add (new Extension(".*?\\.ini", "*.ini", true));
		m_extDelete.add (new Extension(".*?\\.keep", "*.keep", true));
		m_extDelete.add (new Extension(".*?\\.lock", "*.lock", true));
		m_extDelete.add (new Extension(".*?\\.log", "*.log", true));
		m_extDelete.add (new Extension(".*?\\.psf", "*.psf", true));
		m_extDelete.add (new Extension(".*?\\.swp", "*.swp", true));
		m_extDelete.add (new Extension(".*?\\.webspheredeploy", "*.webspheredeploy", true));
		m_extDelete.add (new Extension("_.*?\\.java", "_*.java", true));
		m_extDelete.add (new Extension("EJS.*?\\.java", "EJS*.java",true));
	}
}
