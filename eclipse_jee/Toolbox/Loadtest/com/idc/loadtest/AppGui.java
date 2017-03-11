
package com.idc.loadtest;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.trace.LogHelper;

public class AppGui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1;
	private static final String RUNAPP = "Run...";
	private static final String STOPAPP = "Stop";
	private static final int MAX_CNTR=1000;
	private int m_cntr = 0;
	private JTextArea m_messagesArea;	
	private JTextField m_stringThreads;
	private JTextField m_stringRepeat;
	private JTextField m_stringDelay;
	private JTextField m_stringTestfile;
	private JButton m_btnApp;
	private JButton m_btnClear;
	private JLabel m_txtStatus;
	private JProgressBar m_progress;
	private AppThreads m_appThreads;

	public AppGui (String msg, String[] args) {
		super(msg);

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
	public static void main(String[] args) {
		new AppGui("AppGUI", args);
	}

	private Container makeContentPane() {
		m_stringThreads = new JTextField(4);
		m_stringRepeat = new JTextField(2);
		m_stringDelay = new JTextField(2);
		m_stringTestfile = new JTextField(30);
		m_stringThreads.setText("1");
		m_stringRepeat.setText("1");
		m_stringDelay.setText("3");
		m_stringTestfile.setText("C:\\irac7\\wrkspc\\Toolbox\\Loadtest\\hes_test.xml");

		JPanel paneA = new JPanel();
		paneA.add(new JLabel("Threads #"));				
		paneA.add(m_stringThreads);
		paneA.add(new JLabel("Repeat #"));
		paneA.add(m_stringRepeat);
		paneA.add(new JLabel("Delay"));
		paneA.add(m_stringDelay);
		paneA.add(new JLabel("Test.xml"));
		paneA.add(m_stringTestfile);

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(paneA, BorderLayout.NORTH);
		
		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_messagesArea = new JTextArea(40,60);
		m_messagesArea.setEditable(false);		
		m_messagesArea.setDragEnabled(true);	
		midPane.add(new JScrollPane(m_messagesArea),BorderLayout.CENTER);

		JPanel lowPane = new JPanel();
		m_btnApp = new JButton(RUNAPP);
		m_btnApp.setDefaultCapable(true);
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);
		m_btnClear = new JButton("Clear");
		m_btnClear.setDefaultCapable(true);
		m_btnClear.addActionListener(this);
		lowPane.add(m_btnClear);
						
		m_txtStatus = new JLabel();
		lowPane.add (m_txtStatus);
		m_progress = new JProgressBar();
		lowPane.add(m_progress);
	
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);			
		return pane;
	}	
	public void setButtonText (boolean bBtn) {
		final String msg;
		if (bBtn) msg = RUNAPP;
		else msg = STOPAPP;
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_btnApp.setText(msg);
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
					m_messagesArea.setCaretPosition(
						m_messagesArea.getText().length());
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
	public void addWord(String word) {setMessagesArea(word);}

	public void startProgressBar() {initProgressBar(0,MAX_CNTR);}
	public void endProgressBar() {setProgressBar(MAX_CNTR);}
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
	public void handleProgressIndicator() {
		m_cntr+=10;
		if (m_cntr > MAX_CNTR) m_cntr = 1;
		setProgressBar(m_cntr);		
	}

	public void actionPerformed (ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			LogHelper.info("textfield");
		}
		else if (source instanceof JButton) {
			if (source == m_btnApp) {
				JButton jb = (JButton) e.getSource();
				String strBtn = jb.getText();
				if (strBtn.equals(RUNAPP)) {
					boolean bError = false;
					if (! isStringThreadsValid()) bError = true;
					if (! isStringRepeatValid()) bError = true;
					if (! isStringDelayValid()) bError = true;
					if (! isStringTestfileValid()) bError = true;
					if (bError) {
						setStatusMessage("Enter all the fields and press "+RUNAPP);
						setProgressBar(0);			
					}
					else {
						m_appThreads = null;
						m_appThreads = new AppThreads(this, getIntThreads(), getIntRepeat(), getIntDelay(), getTestfile());
						m_appThreads.start();
						m_appThreads.setStart();
						setStarted();
					}
				}
				else if (strBtn.equals(STOPAPP)) {
					m_appThreads.setStop();
					m_appThreads = null;
				}							
			}
			else if (source == m_btnClear) {
				clearMessagesArea();
			}
		}
		else
			LogHelper.info("else type");
	}
	public void doStopClient() {
		setMessagesArea("Exiting...");
		if (m_appThreads != null) {
			m_appThreads.setStop();
			try {
				while (m_appThreads.isAlive()) {
					LogHelper.info("AppGui thread is alive");
					Thread.sleep(1000);
					LogHelper.info("AppGui is Sleeping");
				}
				LogHelper.info("AppGui thread is not alive");
			}
			catch (InterruptedException e) {
				LogHelper.info("AppGui ; no sleep");
			}
		}
		m_appThreads = null;
		LogHelper.info("exiting app...");
		System.exit(0);
	}
	public void setStarted() {setButtonText(false);}
	public void setStopped() {setButtonText(true);}

	private String getThreads() {return m_stringThreads.getText();}
	private String getRepeat() {return m_stringRepeat.getText();}
	private String getDelay() {return m_stringDelay.getText();}
	private String getTestfile() {return m_stringTestfile.getText();}
	private int getIntThreads() {return getInt(getThreads());}
	private int getIntRepeat() {return getInt(getRepeat());}
	private int getIntDelay() {return getInt(getDelay());}

	private boolean isStringThreadsValid() {return isIntValid (getInt(getThreads()));}
	private boolean isStringRepeatValid() {return isIntValid (getInt(getRepeat()));}
	private boolean isStringDelayValid() {return isIntValid (getInt(getDelay()));}
	private boolean isStringTestfileValid() {
		if (getTestfile() == null || getTestfile().length() < 1)
			return false;
		return true;
	}
	private int getInt (String str) {
		if (str == null) return 0;
		try {
			return Integer.parseInt(str);
		}
		catch (Exception ex) {
			return 0;
		}
	}
	private boolean isIntValid (int num) {
		if (num < 0) return false;
		return true;
	}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
}
