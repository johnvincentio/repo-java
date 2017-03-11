
package com.idc.loadtest;

import com.idc.trace.LogHelper;

public class AppThreadItem extends Thread {
	private AppThreads m_appThreads;
	private int m_threadId;
	private int m_repeat;
	private int m_delay;
	private RunTests m_runTests;
	private boolean m_bPleaseSuspendThisThread = true;
	private boolean m_bStop = false;
	
	public AppThreadItem (AppThreads appThreads, ThreadGroup threadGroup, int threadId, Stages stages, int repeat, int delay) {
		super (threadGroup, "Load Test Thread "+threadId);
		LogHelper.info(">>> AppThreadItem::constructor "+threadId);
		m_appThreads = appThreads;
		m_threadId = threadId;
		m_repeat = repeat;
		m_delay = delay;
		m_runTests = new RunTests (this, m_appThreads, stages);
		LogHelper.info("<<< AppThreadItem::constructor "+m_threadId);		
	}
	public void setSuspend() {m_bPleaseSuspendThisThread = true;}
	public boolean isThreadHasBeenInstructedToStop() {return m_bStop;}
	private synchronized void setResume() {
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public void setStart () {
		m_bStop = false;
		setResume();
	}
	public void setStop() {
		LogHelper.info("AppThreadItem - setStop "+m_threadId);
		m_bStop = true;
	}
	public void run() {
		LogHelper.info(">>> AppThreadItem::run "+m_threadId);
		try {
			synchronized (this) {
				while (m_bPleaseSuspendThisThread) {
					LogHelper.info("AppThreadItem - suspending thread "+m_threadId);
					wait();
				}
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		LogHelper.info("thread id "+m_threadId);
		for (int loop=1; loop<=m_repeat; loop++) {
			if (loop > 1) {
				try {
					LogHelper.info("delay, waiting; thread id "+m_threadId);
					Thread.sleep(m_delay * 1000L);
					LogHelper.info("delay, woke up; thread id "+m_threadId);
				}
				catch (InterruptedException iex) {
					LogHelper.info("InterruptedException "+iex.getMessage());
				}
			}
			m_runTests.runTests(m_threadId);
		}
		LogHelper.info("<<< AppThreadItem::run "+m_threadId);
	}
}
