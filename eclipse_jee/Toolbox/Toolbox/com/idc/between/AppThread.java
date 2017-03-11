package com.idc.between;

import com.idc.trace.LogHelper;

public class AppThread extends Thread {
	private TheAppTemplate m_app;

	private boolean m_bPleaseSuspendThisThread = true;
	private boolean m_bStop = false;

	public AppThread (TheAppTemplate app) {
		LogHelper.info(">>> AppThread::constructor");
		m_app = app;
		m_bPleaseSuspendThisThread = true;
		LogHelper.info("<<< AppThread::constructor");
	}
	public void setSuspend() {
//		LogHelper.info("--- AppThread::setSuspend");
		m_bPleaseSuspendThisThread = true;
	}
	public boolean getStopStatus() {
//		LogHelper.info("--- AppThread::getStopStatus");
		return m_bStop;
	}
	private synchronized void setResume() {
//		LogHelper.info("--- AppThread::setResume");
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public void setStart () {
//		LogHelper.info("--- AppThread::setStart");
		m_bStop = false;
		setResume();
	}
	public void setStop() {
//		LogHelper.info("--- AppThread::setStop");
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
			m_app.doTask();
			m_app.setStopped();
			setSuspend();
		}
		LogHelper.info("<<< AppThread::run");
	}
}
