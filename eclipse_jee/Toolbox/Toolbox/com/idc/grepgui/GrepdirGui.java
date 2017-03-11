package com.idc.grepgui;

/*
	To DO:
	1. pull down box for file types.
	5. pass info as parameters
	7. enter implies start - maybe KeyListener on all TextFields, enter will do the Search.
*/

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.trace.LogHelper;
import com.idc.utils.JVString;

public class GrepdirGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private static final int MAX_CNTR = 1000;
	private int m_cntr = 0;
//	private static final String PROPERTIES_FILE="grepdirgui.properties";
	private String m_strPropertiesFile = "grepdirgui.properties";
	private String m_strEditor;
	private JTextArea m_messagesArea;	
	private JTextField m_stringField1;
	private JTextField m_stringField2;
	private JTextField m_stringField3;
	private JTextField m_typeField;
	private JTextField m_dirField;
	private JTextField m_editField;	
	private JButton m_btnApp;
	private JButton m_btnDir;
	private JButton m_btnEdit;
	private JButton m_btnClear;
	private JButton m_btnClearText;
	private JLabel m_txtStatus;
	private JProgressBar m_progress;
	private JFileChooser m_fileChooser;
	private JComboBox<String> m_comboDirs;
	private JCheckBox m_chkCaseSensitive;

	private AppThread m_appThread = null;
	private Grepdir m_grepdir;

	public GrepdirGui (String msg, String[] args) {
		super(msg);
		if (args.length > 0 && args[0].length() > 8)
			m_strPropertiesFile = args[0];
		System.out.println("Properties file:"+m_strPropertiesFile);
		m_grepdir = new Grepdir(this);
		setContentPane(makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);	
	}
	public AppThread getAppThread() {return m_appThread;}
	public static void main(String[] args) {
		new GrepdirGui("GrepdirGUI", args);
	}

	private Container makeContentPane() {

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(m_strPropertiesFile));
		} catch (IOException ioe) {
			System.err.println("Exception getting properties; "+ioe.getMessage());
		}
		m_strEditor = prop.getProperty("GUI_EDITOR");
		System.out.println("gui_editor :"+m_strEditor+":");

		ArrayList<File> list = new ArrayList<File>();
		String strKey, strValue;
		for (int num=1; num<11; num++) {
			strKey = "FAV_DIR_" + Integer.toString(num);
			strValue = prop.getProperty(strKey,"");
			System.out.println("key :"+strKey+": value :"+strValue+":");
			if (strValue.length() > 0) {
				File myFile = new File(strValue);
				if (myFile.isDirectory()) list.add(myFile);
			}
		}

		JPanel paneA = new JPanel();
		JLabel label1 = new JLabel("Search");
		JLabel label2 = new JLabel("File Type");
		m_stringField1 = new JTextField(25);
		m_stringField2 = new JTextField(15);
		m_stringField3 = new JTextField(10);
	
		m_typeField = new JTextField(6);
		m_typeField.setText("*.java");
		m_typeField.addActionListener(this);
		m_chkCaseSensitive = new JCheckBox ("Case Sensitive");

		paneA.add(label1);				
		paneA.add(m_stringField1);
		paneA.add(m_stringField2);
		paneA.add(m_stringField3);
		paneA.add(label2);
		paneA.add(m_typeField);
		paneA.add(m_chkCaseSensitive);

		m_comboDirs = new JComboBox<String>();
		Iterator<File> iter = list.iterator();
		while(iter.hasNext()) {
			m_comboDirs.addItem(((File) iter.next()).getPath());
		}
		m_comboDirs.addItem(System.getProperty("user.dir"));
		m_comboDirs.addActionListener(this);
		m_btnDir = new JButton("Directory");
		m_btnDir.addActionListener(this);

		m_dirField = new JTextField(20);
		m_dirField.addActionListener(this);	
		m_dirField.setText((String) m_comboDirs.getItemAt(0));
		m_dirField.addActionListener(this);	

		m_fileChooser = new JFileChooser();
//		m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		String strCwd = System.getProperty("user.dir");
		String strCwd = getDirField();
		m_fileChooser.setCurrentDirectory(new File(strCwd));

		JPanel paneB = new JPanel();
		paneB.add(m_comboDirs);		
		paneB.add(m_btnDir);
		paneB.add(m_dirField);		

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(paneA, BorderLayout.NORTH);
		topPane.add(paneB, BorderLayout.SOUTH);

		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		midPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		m_messagesArea = new JTextArea(40,80);
		m_messagesArea.setEditable(false);		
		m_messagesArea.setDragEnabled(true);	
		midPane.add(new JScrollPane(m_messagesArea),BorderLayout.CENTER);
		
		JPanel lowPane = new JPanel();
		m_btnClearText = new JButton("Clear Text");
		m_btnClearText.addActionListener(this);
		lowPane.add(m_btnClearText);
		m_btnApp = new JButton("Search");
		m_btnApp.setDefaultCapable(true);
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);
						
		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JProgressBar();
		lowPane.add(m_progress);
	
		m_editField = new JTextField(25);
		m_editField.addActionListener(this);
		m_btnEdit = new JButton("Edit");
		m_btnEdit.addActionListener(this);
		m_btnClear = new JButton("Clear");
		m_btnClear.addActionListener(this);		
		lowPane.add(m_editField);		
		lowPane.add(m_btnEdit);
		lowPane.add(m_btnClear);								

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
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
					m_messagesArea.setCaretPosition(m_messagesArea.getText().length());
					validate();
				}
			}
		);
	}
	public void clearMessagesArea () {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_messagesArea.setText("");
					m_messagesArea.setCaretPosition(m_messagesArea.getText().length());
					validate();
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
					m_progress.setStringPainted(false);	// true for %age
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
//		LogHelper.info(">>> searchSelected");
		boolean bError = false;
		if (! isStringFieldValid()) bError = true;
		if (! isTypeFieldValid()) bError = true;
		if (! isDirFieldValid()) bError = true;
		if (bError) {
			setStatusMessage("Enter all the fields and press Search");
			setProgressBar(0);			
		}
		else {
			m_appThread = new AppThread(this);
			m_appThread.start();
			m_appThread.setStart();
			setStarted();
		}
//		LogHelper.info("<<< searchSelected");		
	}
	public void setStarted() {
		setButtonText(false);
		setEditButtonActive(false);
		setDirButtonActive(false);
		setClearButtonActive(false);		
	}
	public void doStopClient() {
		if (m_appThread != null) {
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
		}
		m_appThread = null;
		LogHelper.info("exiting app...");
		System.exit(0);
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
//			searchSelected();
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
				m_fileChooser.setCurrentDirectory(new File(getDirField()));
				int retval = m_fileChooser.showOpenDialog(GrepdirGui.this);
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
			else if (source == m_btnClearText) {
				LogHelper.info("btnClearText");
				clearMessagesArea();
			}
		}
		else if (source instanceof JComboBox) {
			LogHelper.info("JComboBox");
			String strDir = (String) m_comboDirs.getSelectedItem();
			System.out.println("strdir :"+strDir+":");
			setDirField(strDir);
		}
		else
			LogHelper.info("else type");
	}
	private String getStringField1() {return m_stringField1.getText();}
	private String getStringField2() {return m_stringField2.getText();}
	private String getStringField3() {return m_stringField3.getText();}
	private String getTypeField() {return m_typeField.getText();}
	private String getDirField() {return m_dirField.getText();}
	private boolean isDirFieldValid() {
		String strDir = getDirField();
		if (strDir == null || strDir.length() < 1)	return false;
		File file = new File (strDir);
		if (! file.isDirectory()) return false;
		return true;
	}
	private boolean isTypeFieldValid() {
		if (getTypeField() == null || getTypeField().length() < 1)
			return false;
		return true;
	}
	private boolean isStringFieldValid() {
		if (getStringField1() == null || getStringField1().length() < 1)
			return false;
		return true;
	}
	private boolean isCaseSensitive() {
		return m_chkCaseSensitive.isSelected();
	}
	public void addWord(String word) {setMessagesArea(word);}		
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	public void doEditor(String strFile) {
		String[] strCmd = JVString.createStringArrayForExecCmd (m_strEditor, strFile);
		LogHelper.info("strCmd.length "+strCmd.length);
		for (String s : strCmd) {
			LogHelper.info("String  :"+s+":");
		}
		try {
			Runtime.getRuntime().exec(strCmd);
		}
		catch (IOException e) {
			LogHelper.info("Exception; "+e.getMessage());
			LogHelper.info("cannot run command "+strCmd);
		}
	}
	public void doGrepdir() {
		LogHelper.info(">>> doGrepdir");
		ArrayList<String> findList = new ArrayList<String>();
		String strFind =  getStringField1();
		findList.add(strFind);
		strFind = getStringField2();
		if (strFind.length() > 0) findList.add(strFind);
		strFind = getStringField3();
		if (strFind.length() > 0) findList.add(strFind);
	
		String strDir = getDirField();
		String strFiles = getTypeField();

		setStatusMessage("searching...");
		initProgressBar(0,MAX_CNTR);
		addWord("");
		addWord("Searching from "+strDir);
		Iterator<String> iter = findList.iterator();
		while (iter.hasNext()) {
			strFind = (String) iter.next();
			addWord("	for String "+strFind);
		}
		addWord("	in files "+strFiles);
		addWord("	Case Sensitive: "+isCaseSensitive());
		addWord("");

		m_grepdir.doGrepdir(strDir, findList, strFiles, isCaseSensitive());

		if (m_grepdir.isSearchStopped()) {
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
		LogHelper.info("<<< doGrepdir");
	}
	public void handleProgressIndicator() {
		m_cntr++;
		if (m_cntr > MAX_CNTR) m_cntr = 1;
		setProgressBar(m_cntr);		
	}
}
/*
	private void setTypeField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_typeField.setText(msg);
					validate();
				}
			}
		);		
	}
*/
