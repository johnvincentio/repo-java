package com.idc.between;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.idc.swing.JVLabel;
import com.idc.swing.JVMessagesArea;
import com.idc.swing.date.DateTimeButton;
import com.idc.swing.progress.JVProgressBar;
import com.idc.trace.LogHelper;

public class FilesBetweenGui extends JFrame implements ActionListener, PropertyChangeListener, KeyListener {
	private static final long serialVersionUID = 1;
	private JVMessagesArea m_messagesArea;
	private JTextField m_searchDirField;
	private JTextField m_outputDirField;
	private JButton m_btnApp;
	private JButton m_btnSearchDir;
	private JVLabel m_txtStatus;
	private JVProgressBar m_progress;
	private DateTimeButton m_startDateTimeButton;
	private DateTimeButton m_endDateTimeButton;
	private JFileChooser m_dirChooser;

	private TheApp m_theApp;
	private AppThread m_appThread;

	public String getSearchDirField() {return m_searchDirField.getText();}
	public void setSearchDirField(String s) {m_searchDirField.setText(s);}
	public String getOutputDirField() {return m_outputDirField.getText();}

	public String getStartDateField() {return m_startDateTimeButton.getText();}
	public String getEndDateField() {return m_endDateTimeButton.getText();}
	public void addMessage (String msg) {m_messagesArea.add(msg);}
	public void setProgressMessage (String msg) {m_txtStatus.setText(msg);}

	public void initProgressBar (int iMin, int iMax) {m_progress.initProgressBar(iMin, iMax);}
	public void setProgressBar (int value) {m_progress.setProgressBar(value);}
	public void setProgressBar () {m_progress.setProgressBar();}
	public void setMaxProgressBar () {m_progress.setMaxProgressBar();}
	public AppThread getAppThread() {return m_appThread;}

	public FilesBetweenGui (String msg) {
		super(msg);

		m_theApp = new TheApp(this);
		m_appThread = new AppThread(m_theApp);
		m_appThread.start();
		setContentPane(makeContentPane());
		this.addWindowListener        (new WindowAdapter() {
			public void windowOpened (WindowEvent e) {
//				m_outputDirField.requestFocus();		// this works
			}
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);
	}
	private Container makeContentPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		m_dirChooser = new JFileChooser();
		m_dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		m_dirChooser.setCurrentDirectory(new File("/"));

		JPanel pane1 = new JPanel();
		m_btnSearchDir = new JButton("Directory");
		m_btnSearchDir.addActionListener(this);

		m_searchDirField = new JTextField(20);
//		m_searchDirField.setText("/home/jv/wsadAll/Tools/wrkspc/work");
		m_searchDirField.addActionListener(this);
		m_searchDirField.addKeyListener(this);
		pane1.add (new JLabel("Search Directory"));
		pane1.add (m_searchDirField);
		pane1.add (m_btnSearchDir);

		JPanel pane2 = new JPanel();
		m_startDateTimeButton = new DateTimeButton();
		m_startDateTimeButton.addPropertyChangeListener("date", this);
		m_endDateTimeButton = new DateTimeButton();
		m_endDateTimeButton.addPropertyChangeListener("date", this);
		pane2.add (new JLabel("Start Date/Time"));
		pane2.add (m_startDateTimeButton);
		pane2.add (new JLabel("End Date/Time"));
		pane2.add (m_endDateTimeButton);

		JPanel pane3 = new JPanel();
		m_outputDirField = new JTextField(20);
//		m_outputDirField.setText("/tmp/between");
		m_outputDirField.addActionListener(this);
		m_outputDirField.addKeyListener(this);
//		m_outputDirField.requestFocus();		// did not work
//		setFocus (m_outputDirField);			// did not work
		pane3.add (new JLabel("Output Directory"));
		pane3.add (m_outputDirField);

		JPanel topPane = new JPanel();
		topPane.setLayout(new BorderLayout());
		topPane.add(pane1,BorderLayout.NORTH);
		topPane.add(pane2,BorderLayout.CENTER);
		topPane.add(pane3,BorderLayout.SOUTH);
		
		JPanel midPane = new JPanel();
		midPane.setLayout(new BorderLayout());
		m_messagesArea = new JVMessagesArea();
		m_messagesArea.makeContentPane(40, 90);
		midPane.add(new JScrollPane(m_messagesArea.getTextArea()), BorderLayout.CENTER);

		JPanel lowPane = new JPanel();
		m_btnApp = new JButton("Search");
		m_btnApp.addActionListener(this);
		lowPane.add(m_btnApp);

		m_txtStatus = new JVLabel();
		lowPane.add (m_txtStatus.getLabel());
		m_progress = new JVProgressBar();
		lowPane.add(m_progress);

		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);
		return pane;
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
				searchSelected (strBtn);
			}
			else if (source == m_btnSearchDir) {
				LogHelper.info("m_btnSearchDir");
				m_dirChooser.setCurrentDirectory(new File(getSearchDirField()));
				int retval = m_dirChooser.showOpenDialog(this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = m_dirChooser.getSelectedFile();
					setSearchDirField (file.getPath());
				}
			}
		}
		else
			LogHelper.info("else type");
	}
	private void searchSelected (String strBtn) {
		LogHelper.info(">>> searchSelected");
		if (strBtn.equals("Search")) {
			boolean bError = false;
			if (! AppTasks.areDateFieldsValid (getStartDateField(), getEndDateField())) bError = true;
			if (! AppTasks.isSearchDirFieldValid (getSearchDirField())) bError = true;
			if (! AppTasks.isOutputDirFieldValid (getOutputDirField())) bError = true;
			if (bError) {
				m_txtStatus.setText("Enter all the fields and press Search");
				m_progress.setProgressBar(0);
			}
			else {
				m_appThread.setStart();
				m_theApp.setStarted();
			}
		}
		else if (strBtn.equals("Stop")) {
			m_appThread.setStop();
			m_theApp.setStopped();
		}
		LogHelper.info("<<< searchSelected");
	}

	public void doStopClient() {
		m_theApp.setAppOver(true);
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
	public static void main(String[] args) {
		new FilesBetweenGui("FilesBetweenGui");
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
	public void setFocus (final JComponent component) {
		SwingUtilities.invokeLater (
				new Runnable() {
					public void run() {
						component.requestFocus();
					}
				}
		);
	}
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof DateTimeButton) {
			DateTimeButton db = (DateTimeButton)e.getSource();
			if (db == m_startDateTimeButton)
				LogHelper.info("Start Date time changed: ");
			else if (db == m_endDateTimeButton)
				LogHelper.info("End Date time changed: ");
			else
				LogHelper.info("something is wrong....");
			LogHelper.info(db.getDateTime().toString());
		}
	}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}
	public void keyPressed (KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//	    	System.out.println("enter pressed");
	    	searchSelected (m_btnApp.getText());
        }
	}
//	if ((e.getSource().equals(btnOK)) && ( e.getKeyCode() == KeyEvent.VK_ENTER ))
}
