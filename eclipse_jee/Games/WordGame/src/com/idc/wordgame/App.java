package com.idc.wordgame;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class App extends JFrame implements ActionListener {
	private static final long serialVersionUID = -7568936734558333075L;

	private JTextArea m_messagesArea;  // messages for the user
	private JTextField m_nameField;	// word to use
	private	JButton m_btnApp;	// will toggle search/stop
	private JLabel m_txtStatus;	// inform the user
	private JProgressBar m_progress;	// status the task

	private AppThread m_appThread;	// worker thread, dictionary lookup
	private Dictionary m_dict;	// word list, dictionary
	private boolean m_bAppOver = false;

	public App(String msg) {
		super(msg);
		Debug.setFile("CONSOLE",false);
		Debug.println("App()");
		m_dict = new Dictionary(this);	// initialise the dictionary
		m_appThread = new AppThread (this, m_dict); 
		m_appThread.start();	// start worker thread
		setContentPane(makeGameContentPane());	// create the user interface
		this.addWindowListener(new WindowAdapter() {	// handle exit
			public void windowClosing(WindowEvent e) {
				doStopClient();		// exit gracefully
			}
		});
		if (! m_dict.setupDictionary()) {
			JOptionPane.showMessageDialog(this,"No dictionary",
							"Word Search",JOptionPane.ERROR_MESSAGE);
			doStopClient();
		}
		setStatusMessage("Enter your Word and press Search");
		setSize(500,900);
		pack();
		setVisible(true);
	}
	public AppThread getAppThread() {return m_appThread;}
	public boolean isAppOver() {return m_bAppOver;}
	public void addWord(String word) {setMessagesArea (word);}
	public static void main (String args[]) {	// not a lot!
		new App ("Word Game");
	}
	private void doStopClient() {	// the thread may need time to clean up
		m_bAppOver = true;		// not in this app, but might as well!
		m_appThread.setStop();
		try {
			while (m_appThread.isAlive()) {	// if thread still running
				Debug.println("thread is alive");
				Thread.sleep(10);			// sleep
				Debug.println("sleeping");
			}
			Debug.println("thread is not alive");
		}
		catch (InterruptedException e) {
			Debug.println("no sleep");
		}
		Debug.println("exiting app...");
		System.exit(0);
	}
	private Container makeGameContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();	// top Pane
		JLabel label = new JLabel("Your Word");
		m_nameField = new JTextField(20);	// user entered word
//		m_nameField.addActionListener(new WordFieldHandler());
		m_nameField.addActionListener(this);
		m_btnApp = new JButton("Search");
		m_btnApp.addActionListener(this);
		topPane.add(label);
		topPane.add(m_nameField);
		topPane.add(m_btnApp);

		JPanel midPane = new JPanel();	// panel for a messages area
		m_messagesArea = new JTextArea(20,35);
		m_messagesArea.setEditable(false);
		midPane.add(new JScrollPane(m_messagesArea));

		JPanel lowPane = new JPanel();	// panel for status
		m_txtStatus = new JLabel();			// status messages
		lowPane.add(m_txtStatus);
		m_progress = new JProgressBar();	// progress bar
		lowPane.add(m_progress);

		pane.add(topPane,BorderLayout.NORTH);	// put it together
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return (pane);
	}
//
// methods to update gui components - Be thread-safe!
//
	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn) msg = "Search";	// toggle Search/Stop
		else msg = "Stop";
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					m_btnApp.setText(msg);
				}
			}
		);
	}
	private void setMessagesArea (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					m_messagesArea.append (msg);
					m_messagesArea.append ("\n");
					m_messagesArea.setCaretPosition ( 
						m_messagesArea.getText().length());
				}
			}
		);
	}
	public void setStatusMessage(final String msg) {
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
					m_progress.setStringPainted(true);
					m_progress.setIndeterminate(false);
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
	private void setNameField (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					m_nameField.setText(msg);
					validate();
				}
			}
		);
	}
//
// handle user actions
//
	private void searchSelected () {
		StringBuffer sb = new StringBuffer(m_nameField.getText());
		StringBuffer nb = new StringBuffer();
		for (int i=0; i<sb.length(); i++) {
			if (sb.charAt(i) != ' ') nb.append(sb.charAt(i));
		}
		String strWord = nb.toString().toLowerCase();
		setNameField(strWord);
		if (nb.length() < 1) {
			setStatusMessage("Enter your Word and press Search");
			setProgressBar(0);
		}
		else {
			m_appThread.setStart(strWord);
			setButtonText(false);
		}
	}
	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField)
			searchSelected();
		else if (source instanceof JButton) {
			JButton jb = (JButton) e.getSource();
			String strBtn = jb.getText();
			if (strBtn.equals("Search"))
				searchSelected();
			else if (strBtn.equals("Stop")) {
				m_appThread.setStop();
				setButtonText(true);
			}
		}
	}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
//
//	useful for debugging - not in use
//
	@SuppressWarnings("unused")
	private class WordFieldHandler implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			Debug.println("command: "+e.getActionCommand());
			Debug.println("event type: "+e.getID());
			Debug.println("paramString: "+e.paramString());
			int modifiers = e.getModifiers();
			Debug.println("Modifiers:");
			Debug.println("\tALT : "+checkMod(modifiers,ActionEvent.ALT_MASK));
			Debug.println("\tCTRL : "+checkMod(modifiers,ActionEvent.CTRL_MASK));
			Debug.println("\tMETA : "+checkMod(modifiers,ActionEvent.META_MASK));
			Debug.println("\tSHIFT : "+checkMod(modifiers,ActionEvent.SHIFT_MASK));
			Object source = e.getSource();
			Debug.println("object :"+source.getClass().getName());
			if (source instanceof JTextField)
				Debug.println("type JTextField");
		}
		private boolean checkMod(int modifiers, int mask) {
			return ((modifiers & mask) == mask);
		}

	}
}

