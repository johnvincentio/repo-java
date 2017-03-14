package com.idc.diff.all.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.idc.file.JVFile;
import com.idc.swing.JVMessagesArea;
import com.idc.trace.LogHelper;

public class DiffallGui extends JFrame {
	private static final long serialVersionUID = 3591937507178896647L;

	private String m_strPropertiesFile = "diffallgui.properties";
	private String m_strEditor;
	private File m_LogDirectory;
	public File getLogDirectory() {return m_LogDirectory;}

	private CompareExtensions m_compareExtensions = new CompareExtensions();
	public Extensions getCompareExtensions() {return m_compareExtensions.getExtensions();}
	private DeleteExtensions m_deleteExtensions = new DeleteExtensions();
	public Extensions getDeleteExtensions() {return m_deleteExtensions.getExtensions();}
	private ByteCompareExtensions m_byteCompareExtensions = new ByteCompareExtensions();
	public Extensions getByteCompareExtensions() {return m_byteCompareExtensions.getExtensions();}
	private DeleteDirectories m_deleteDirectories = new DeleteDirectories();
	public Extensions getDeleteDirectories() {return m_deleteDirectories.getExtensions();}

	private LowPanel m_lowPanel = new LowPanel (this);
	private MidPanel m_midPanel = new MidPanel();

	private DirectoryOptionsPanel m_DirectoryOptionsPanel = new DirectoryOptionsPanel (this);
	private LogOptionsPanel m_LogOptionsPanel;
	private StatisticsOptionsPanel m_StatisticsPanel;
	private MessageOptionsPanel m_MessageOptionsPanel = new MessageOptionsPanel();
	private BasicOptionsPanel m_BasicOptionsPanel = new BasicOptionsPanel();
	private CompareOptionsPanel m_CompareOptionsPanel = new CompareOptionsPanel();
	private CleaningOptionsPanel m_CleaningOptionsPanel = new CleaningOptionsPanel();

	private AppThread m_appThread = null;
	private Diffgui m_diffgui;

	public DiffallGui (String msg, String[] args) {
		super(msg);
		LogHelper.info ("starting app");
		LogHelper.info ("args.length "+args.length);
		LogHelper.info ("args[0] "+args[0]);
		if (args.length > 0 && args[0].length() > 8)
			m_strPropertiesFile = args[0];
		System.out.println("Properties file:" + m_strPropertiesFile);
		setContentPane (makeContentPane());
		m_diffgui = new Diffgui (this);
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
	public static void main(String[] args) {new DiffallGui ("DiffallGui", args);}
	private Container makeContentPane() {
		Properties prop = new Properties();
		try {
			prop.load (new FileInputStream (m_strPropertiesFile));
		} catch (IOException ioe) {
			System.err.println ("Exception getting properties; " + ioe.getMessage());
		}
		m_strEditor = prop.getProperty ("GUI_EDITOR");
		System.out.println("gui_editor :"+m_strEditor+":");
		m_LogDirectory = new File (prop.getProperty ("LOG_DIR"));
		System.out.println("m_LogDirectory :"+m_LogDirectory+":");
		JVFile.makeFullDirectories (m_LogDirectory);
		m_LogOptionsPanel = new LogOptionsPanel (this);
		mySleep (1000);
		m_StatisticsPanel = new StatisticsOptionsPanel (this);

		JPanel paneSwitches = new JPanel();
		paneSwitches.setLayout (new GridLayout (2, 3, 0, 0));
		paneSwitches.add (m_MessageOptionsPanel);
		paneSwitches.add (m_LogOptionsPanel);
		paneSwitches.add (m_StatisticsPanel);
		paneSwitches.add (m_BasicOptionsPanel);
		paneSwitches.add (m_CleaningOptionsPanel);
		paneSwitches.add (m_CompareOptionsPanel);

		JPanel paneTopOptions = new JPanel();
		paneTopOptions.setLayout (new BorderLayout());
		paneTopOptions.add (m_DirectoryOptionsPanel, BorderLayout.NORTH);
		paneTopOptions.add (paneSwitches, BorderLayout.CENTER);

		JPanel paneExtensions = new JPanel();
		paneExtensions.setLayout (new BorderLayout());
		paneExtensions.add (m_compareExtensions, BorderLayout.NORTH);
		paneExtensions.add (m_byteCompareExtensions, BorderLayout.CENTER);
		paneExtensions.add (m_deleteExtensions, BorderLayout.SOUTH);
		
		JPanel topPane = new JPanel();
		topPane.setLayout (new BorderLayout());
		topPane.add (paneTopOptions, BorderLayout.NORTH);
		topPane.add (paneExtensions, BorderLayout.CENTER);
		topPane.add (m_deleteDirectories, BorderLayout.SOUTH);
	
		JPanel pane = new JPanel();
		pane.setLayout (new BorderLayout());
		pane.add (topPane,BorderLayout.NORTH);
		pane.add (m_midPanel, BorderLayout.CENTER);
		pane.add (m_lowPanel, BorderLayout.SOUTH);			
		return pane;
	}

	public JVMessagesArea getMessagesArea() {return m_midPanel.getMessagesArea();}
	public void setButtonText (final boolean bBtn) {m_lowPanel.setButtonText (bBtn);}
	public void setStatusMessage (final String msg) {m_lowPanel.setStatusMessage (msg);}
	public void initProgressBar() {m_lowPanel.initProgressBar();}
	public void setProgressBar (final int value) {m_lowPanel.setProgressBar (value);}
	public void handleProgressIndicator () {m_lowPanel.setProgressBar();}
	public void setProgressBarMax () {m_lowPanel.setProgressBarMax();}
	public void setEditButtonActive (final boolean bBtn) {m_lowPanel.setEditButtonActive (bBtn);}
	public void setClearButtonActive (final boolean bBtn) {m_lowPanel.setClearButtonActive (bBtn);}
	public void setClearTextButtonActive (final boolean bBtn) {m_lowPanel.setClearTextButtonActive (bBtn);}
	public void setDirBaseButtonActive (final boolean bBtn) {m_DirectoryOptionsPanel.setDirBaseButtonActive (bBtn);}
	public void setDirDiffButtonActive (final boolean bBtn) {m_DirectoryOptionsPanel.setDirDiffButtonActive (bBtn);}

	public void setStarted() {
		setButtonText (false);
		setDirBaseButtonActive (false);
		setDirDiffButtonActive (false);
		setEditButtonActive (false);
		setClearButtonActive (false);
		setClearTextButtonActive (false);
	}
	public void setStopped() {
		setButtonText (true);
		setDirBaseButtonActive (true);
		setDirDiffButtonActive (true);
		setEditButtonActive (true);
		setClearButtonActive (true);
		setClearTextButtonActive (true);
	}		

	public void doEditor(String strFile) {
		String[] strCmd = {m_strEditor, strFile};		
//		LogHelper.info("strCmd :"+strCmd+":");
		try {
			Runtime.getRuntime().exec(strCmd);
		}
		catch(IOException e) {
//			LogHelper.info("cannot run command "+strCmd);
		}
	}

	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}

	public void startSelected() {
		LogHelper.info (">>> startSelected");
		if (! m_DirectoryOptionsPanel.isOptionsValid()) {
			setStatusMessage("Enter all the fields and press Search");
			setProgressBar(0);
		}
		else if (m_DirectoryOptionsPanel.isDirectoriesSame()) {
			setStatusMessage("Directories must be different!");
			setProgressBar(0);
		}
		else {
			m_appThread = new AppThread (this);
			m_appThread.start();
			m_appThread.setStart();
			setStarted();
		}
		LogHelper.info ("<<< startSelected");		
	}
	public void stopSelected() {
		m_appThread.setStop();
		setStopped();
	}

	public void doDiffdir() {
		LogHelper.info (">>> doDiffdir");
		setStatusMessage ("working...");
		initProgressBar();
		m_diffgui.doDiffdir (
				m_LogOptionsPanel.getOptions(),
				m_StatisticsPanel.getOptions(),
				m_DirectoryOptionsPanel.getOptions(),
				m_BasicOptionsPanel.getOptions(),
				m_CleaningOptionsPanel.getOptions(),
				m_CompareOptionsPanel.getOptions(),
				m_MessageOptionsPanel.getOptions());
		if (m_diffgui.isSearchStopped()) {
			getMessagesArea().add ("---------------------------------------");	
			getMessagesArea().add ("Compare stopped by user");
			getMessagesArea().add ("---------------------------------------");
			setStatusMessage ("Stopped...");
		}
		else {		
			getMessagesArea().add ("---------------------------------------");	
			getMessagesArea().add ("Compare is complete");
			getMessagesArea().add ("---------------------------------------");
			setProgressBarMax();
			setStatusMessage ("Finished...");		
		}
		LogHelper.info ("<<< doDiffdir");
	}
	public void doStopClient() {
		if (m_appThread != null) {
			m_appThread.setStop();
			try {
				while (m_appThread.isAlive()) {
					LogHelper.info ("thread is alive");
					Thread.sleep (10);
					LogHelper.info ("Sleeping");
				}
				LogHelper.info ("thread is not alive");
			}
			catch(InterruptedException e) {
				LogHelper.info ("no sleep");
			}
		}
		m_appThread = null;
		LogHelper.info ("exiting app...");
		System.exit(0);
	}
	public static void mySleep (int millis) {
		try {
			Thread.sleep (millis);
		}
		catch (Exception ex) {
			System.err.println("sleep exception in mySleep; "+ex.getMessage());
		}
	}
}
