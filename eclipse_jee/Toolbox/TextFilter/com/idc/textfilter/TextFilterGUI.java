package com.idc.textfilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.idc.trace.LogHelper;

public class TextFilterGUI extends JFrame {
	private static final long serialVersionUID = 1;

	private JTabbedPane m_tabbedPane;
	private AppInput m_appInput;
	private AppOutput m_appOutput;
	private AppThread m_appThread = null;
	public AppInput getAppInput() {return m_appInput;}
	public AppOutput getAppOutput() {return m_appOutput;}
	public AppThread getAppThread() {return m_appThread;}

	public TextFilterGUI (String msg, String[] args) {
		super(msg);
		LogHelper.info(">>> TextFilterGUI");
		setContentPane(makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStopClient();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);
		LogHelper.info("<<< DBToolGui");
	}

	public static void main(String[] args) {
		new TextFilterGUI("TextFilterGUI", args);
	}
	public void setSize (int width, int height) {
		super.setSize (width, height);
		validate();
		repaint();
	}
	private Container makeContentPane() {
		m_tabbedPane = new JTabbedPane();
		m_appInput = new AppInput(this);
		m_appOutput = new AppOutput();
		m_tabbedPane.addTab("Input", m_appInput.makeContentPane());
		m_tabbedPane.addTab("Output", m_appOutput.makeContentPane());

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(m_tabbedPane,BorderLayout.CENTER);
		return pane;
	}

	public void startThread() {
		m_appThread = new AppThread(this);
		m_appThread.start();
		m_appThread.setStart();		
	}
	public void stopThread() {
		m_appThread.setStop();
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

	public void doTextFilter() {
		LogHelper.info(">>> doTextFilter");
		getAppInput().setStatusMessage("Executing...");
		String text = getAppInput().getMessagesArea().getTextArea().getSelectedText();
		if (text == null || text.length() < 1)
			text = getAppInput().getMessagesArea().getTextArea().getText();
//		LogHelper.info("text :"+text+":");
		Integer scenario = getAppInput().getSelectedItem();
		getAppInput().getProgressBar().initProgressBar();
		getAppOutput().getMessagesArea().clear();
		TextFilter textFilter = new TextFilter(this);
		textFilter.doTextFilter(scenario, text);
		if (textFilter.isExecutionStopped()) {
			getAppOutput().getMessagesArea().add("---------------------------------------");	
			getAppOutput().getMessagesArea().add("Execution stopped by user");
			getAppOutput().getMessagesArea().add("---------------------------------------");
			getAppInput().setStatusMessage("Stopped...");
		}
		else {		
			getAppOutput().getMessagesArea().add("---------------------------------------");	
			getAppOutput().getMessagesArea().add("Execution is complete");
			getAppOutput().getMessagesArea().add("---------------------------------------");
			getAppInput().getProgressBar().setMaxProgressBar();
			getAppInput().setStatusMessage("Finished...");		
		}
		LogHelper.info("<<< doTextFilter");
	}
}
