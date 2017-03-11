package com.idc.cards;

import com.idc.trace.LogHelper;

public class MyWorker extends Thread {
	private int m_nId; // player id

	private MyServer m_server; // ref to the server

	private boolean m_bSuspendThread; // true if thread should be suspended

	private boolean m_bStopThread; // true if thread should be stopped

	private boolean m_bWaiting; // true if thread is waiting

	public void setSuspend() {
		m_bSuspendThread = true;
	}

	public synchronized void setResume() {
		m_bSuspendThread = false;
		notify();
	}

	public synchronized void setStop() {
		m_bStopThread = true;
	}

	private boolean isStopped() {
		return m_bStopThread;
	}

	public boolean getWaiting() {
		return m_bWaiting;
	}

	public MyWorker(int id, MyServer server) {
		LogHelper.info("MyWorker(constructor);  player " + id);
		m_nId = id;
		m_server = server;
		m_bSuspendThread = true; // want the thread to wait when started
		m_bStopThread = false;
		m_bWaiting = false;
	}

	public void run() {
		LogHelper.info("MyWorker:run  player " + m_nId);
		m_bWaiting = false; // thread is running
		while (! isStopped()) { // while thread has not been requested to stop
			try {
				synchronized (this) {
					while (m_bSuspendThread) { // thread has been set to suspended
						LogHelper.info("MyWorker:run; thread waiting; player " + m_nId);
						m_bWaiting = true; // thread is waiting
						wait(); // suspend the thread
					}
				}
			} catch (InterruptedException ex) {
				LogHelper.error("Trouble: " + ex.getMessage());
			}
			m_bWaiting = false; // thread is running
			LogHelper.info("MyWorker:run; thread woken; player " + m_nId);
			if (! isStopped()) { // was thread set to stop when it was suspended?
				LogHelper.info("MyWorker:run; workerMyTurn(); player " + m_nId);
				m_server.workerMyTurn(m_nId); // make this players move
				setSuspend(); // set this thread to suspend
			}
		}
		LogHelper.info("MyWorker:run; thread stopped; player " + m_nId);
	}
}
