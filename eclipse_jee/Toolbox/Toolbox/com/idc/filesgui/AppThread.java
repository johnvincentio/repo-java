package com.idc.filesgui;

import com.idc.trace.LogHelper;

public class AppThread extends Thread {
	private FilesSinceGui m_app;
	private boolean m_bPleaseSuspendThisThread = true;
	private boolean m_bStop = false;

	public AppThread (FilesSinceGui app) {
		LogHelper.info(">>> AppThread::constructor");
		m_app = app;
		m_bPleaseSuspendThisThread = true;
		LogHelper.info("<<< AppThread::constructor");
	}
	public void setSuspend() {m_bPleaseSuspendThisThread = true;}
	public boolean getStopStatus() {return m_bStop;}
	private synchronized void setResume() {
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public void setStart () {
		m_bStop = false;
		setResume();
	}
	public void setStop() {
		setResume();
		m_bStop = true;
	}
	public void run() {
		LogHelper.info(">>> AppThread::run");
		while (! m_app.isAppOver()) {
			try {
				synchronized (this) {
					while (m_bPleaseSuspendThisThread) {
						LogHelper.info("suspending thread");
						wait();
					}
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_app.doFilesSince();
			m_app.setStopped();
			setSuspend();
		}
		LogHelper.info("<<< AppThread::run");
	}
}
