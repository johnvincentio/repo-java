package com.idc.wordgame;

public class AppThread extends Thread {
	private App m_app;
	private Dictionary m_dict;
	private boolean m_bPleaseSuspendThisThread = true;
	private boolean m_bStop = false;
	private String m_strGetWord;

	public AppThread (App app, Dictionary dict) {
		Debug.println(">>> AppThread::AppThread");
		m_app = app;
		m_dict = dict;
		m_bPleaseSuspendThisThread = true;
		m_strGetWord = "";
		Debug.println("<<< AppThread::AppThread");
	}
	public void setSuspend() {m_bPleaseSuspendThisThread = true;}
	public boolean getStopStatus() {return m_bStop;}
	private synchronized void setResume() {
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public void setStart(String strWord) {
		m_strGetWord = strWord;
		m_bStop = false;
		setResume();
	}
	public void setStop() {
		setResume();
		m_bStop = true;
		m_strGetWord = "";
	}
	public void run() {
		Debug.println(">>> AppThread::run");
		while (! m_app.isAppOver()) {
			try {
				synchronized (this) {
					while (m_bPleaseSuspendThisThread) {
						Debug.println("suspending thread");
						wait();
					}
				}
			}
			catch (InterruptedException e) {
            	e.printStackTrace();
			}
			m_dict.doWordSearch(m_strGetWord);
			m_app.setButtonText(true);
			setSuspend();
		}
		Debug.println("<<< App::run");
	}
}

