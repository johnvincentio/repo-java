package com.idc.filesgui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.swing.date.DateTimeButton;
import com.idc.trace.LogHelper;

public class FilesSinceGui extends JFrame implements ActionListener, PropertyChangeListener {
	private static final long serialVersionUID = 1;
	private static final int MAX_CNTR=1000;
	private int m_cntr = 0;
	private static final String PROPERTIES_FILE="grepdirgui.properties";
	private String m_strEditor;
	private JTextArea m_messagesArea;
	private JTextField m_dirField;
	private JTextField m_editField;
	private JButton m_btnApp;
	private JButton m_btnDir;
	private JButton m_btnEdit;
	private JButton m_btnClear;
	private JLabel m_txtStatus;
	private JProgressBar m_progress;
	private JFileChooser m_fileChooser;
	private DateTimeButton m_startDateTimeButton;

	private AppThread m_appThread;
	private FilesSince m_filesSince;
	private boolean m_bAppOver = false;

	public FilesSinceGui (String msg) {
		super(msg);
		m_filesSince = new FilesSince(this);
		m_appThread = new AppThread(this);
		m_appThread.start();
		makeFileChooser();
		setContentPane(makeContentPane());
		this.addWindowListener        (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);
	}
	public AppThread getAppThread() {return m_appThread;}
	public boolean isAppOver() {return m_bAppOver;}
	public static void main(String[] args) {
		new FilesSinceGui("FilesSinceGui");
	}
	public void makeFileChooser() {
		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES_FILE));
		} catch (IOException ioe) {
			System.err.println("Exception getting properties; "+ioe.getMessage());
		}
		m_strEditor = prop.getProperty("GUI_EDITOR");
		LogHelper.info("gui_editor :"+m_strEditor+":");

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();
		JLabel label1 = new JLabel("Date/Time");
		m_startDateTimeButton = new DateTimeButton();
		m_startDateTimeButton.addPropertyChangeListener("date", this);

		m_dirField = new JTextField(20);
		m_dirField.addActionListener(this);
		m_btnDir = new JButton("Directory");
		m_btnDir.addActionListener(this);

		topPane.add(label1);
		topPane.add(m_startDateTimeButton);
		topPane.add(m_btnDir);
		topPane.add(m_dirField);

		JPanel midPane = new JPanel();
		m_messagesArea = new JTextArea(40,90);
		m_messagesArea.setEditable(false);
//		m_messagesArea.setDragEnabled(true);
		midPane.add(new JScrollPane(m_messagesArea));

		JPanel lowPane = new JPanel();
		m_btnApp = new JButton("Search");
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);

		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JProgressBar();
		lowPane.add(m_progress);

		m_editField = new JTextField(35);
		m_editField.addActionListener(this);
		m_btnEdit = new JButton("Edit");
		m_btnEdit.addActionListener(this);
		m_btnClear = new JButton("Clear");
		m_btnClear.addActionListener(this);
		lowPane.add(m_editField);
		lowPane.add(m_btnEdit);
		lowPane.add(m_btnClear);

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
	}
	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn) msg = "Search";
		else msg = "Stop";
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						m_btnApp.setText(msg);
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
	public void setDirButtonActive (final boolean bBtn) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						m_btnDir.setEnabled(bBtn);
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
	public void initProgressBar (final int iMin, final int iMax) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_progress.setMinimum(iMin);
					m_progress.setMaximum(iMax);
					m_progress.setValue(iMin);
					m_progress.setStringPainted(false);        // true for %age
					m_progress.setIndeterminate(true);                // jdk 1.4
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
	private void setEditField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_editField.setText(msg);
					validate();
				}
			}
		);
	}
	private void searchSelected() {
		LogHelper.info(">>> searchSelected");
		boolean bError = false;
		if (! isDateFieldValid()) bError = true;
		if (! isDirFieldValid()) bError = true;
		if (bError) {
			setStatusMessage("Enter all the fields and press Search");
			setProgressBar(0);
		}
		else {
			m_appThread.setStart();
			setStarted();
		}
		LogHelper.info("<<< searchSelected");
	}
	public void setStarted() {
		setButtonText(false);
		setEditButtonActive(false);
		setDirButtonActive(false);
		setClearButtonActive(false);
	}
	public void setStopped() {
		setButtonText(true);
		setEditButtonActive(true);
		setDirButtonActive(true);
		setClearButtonActive(true);
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
				if (strBtn.equals("Search"))
					searchSelected();
				else if (strBtn.equals("Stop")) {
					m_appThread.setStop();
					setStopped();
				}
			}
			else if (source == m_btnDir) {
				LogHelper.info("btnDir");
				int retval = m_fileChooser.showOpenDialog(FilesSinceGui.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_fileChooser.getSelectedFile();
					setDirField (file.getPath());
				}
			}
			else if (source == m_btnEdit) {
				LogHelper.info("btnEdit");
				String strFile = m_editField.getText().trim();
				if (strFile.length() > 0) {
					File file = new File(strFile);
					if (file.isFile())
					doEditor(strFile);
				}
			}
			else if (source == m_btnClear) {
				LogHelper.info("btnClear");
				setEditField("");
			}
		}
		else
			LogHelper.info("else type");
	}
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof DateTimeButton) {
			DateTimeButton db = (DateTimeButton)e.getSource();
			if (db == m_startDateTimeButton)
				LogHelper.info("Date time changed: ");
			else
				LogHelper.info("something is wrong....");
			LogHelper.info(db.getDateTime().toString());
		}
	}
	private String getDateField() {return m_startDateTimeButton.getText();}
	private String getDirField() {return m_dirField.getText();}
	private boolean isDirFieldValid() {
		String strDir = getDirField();
		if (strDir == null || strDir.length() < 1) return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}
	private boolean isDateFieldValid() {
		LogHelper.info(">>> isDateFieldValid");
		LogHelper.info("date field :"+getDateField()+":");
		if (getDateFieldValue() < 1) return false;
		return true;
	}
	private long getDateFieldValue() {
		long lSince = 0;
		if (getDateField() == null || getDateField().length() < 1)
		return lSince;

		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy kk:mm:ss");
		try {
			Date myDate = format.parse(getDateField());
			lSince = myDate.getTime();
		}
		catch (ParseException e) {
			LogHelper.error("Unable to parse "+getDateField());
			return 0L;
		}
		return lSince;
	}
	public void addWord(String word) {setMessagesArea(word);}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	public void doEditor(String strFile) {
		String strCmd = "\"" + m_strEditor + "\" \"" + strFile + "\"";
		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec(strCmd);
		}
		catch (IOException e) {
			LogHelper.info("cannot run command "+strCmd);
		}
	}
	public void doFilesSince() {
		LogHelper.info(">>> doFilesSince");
		String strDir = getDirField();
		String strDate = getDateField();

		setStatusMessage("searching...");
		initProgressBar(0,MAX_CNTR);
		addWord("");
		addWord("Searching from "+strDir);
		addWord("    for files dated later than "+strDate);
		addWord("");

		long lNumber = getDateFieldValue();
		m_filesSince.doFilesSince(strDir, lNumber);

		if (m_filesSince.isSearchStopped()) {
			addWord("---------------------------------------");
			addWord("Search stopped by user");
			addWord("---------------------------------------");
			setStatusMessage("Stopped...");
		}
		else {
			addWord("---------------------------------------");
			addWord("Search is complete");
			addWord("---------------------------------------");
			setProgressBar(MAX_CNTR);
			setStatusMessage("Finished...");
		}
		LogHelper.info("<<< doFilesSince");
	}
	public void handleProgressIndicator() {
		m_cntr++;
		if (m_cntr > MAX_CNTR) m_cntr = 1;
		setProgressBar(m_cntr);
	}
}
