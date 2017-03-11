package com.idc.scrabble.client;

import com.idc.scrabble.utils.Debug;

public class ClientThread extends Thread {
	private ClientApp m_app;
	private String m_strURL;
	private ClientSocket m_socket;
	private boolean m_bInit = false;

	public ClientThread (ClientApp app, String strURL) {
		Debug.println(">>> ClientThread::ClientThread");
		m_app = app;
		m_strURL = strURL;
		m_socket = null;
		m_bInit = false;
		Debug.println("<<< ClientThread::ClientThread");
	}
	public ClientSocket getSocket() {return m_socket;}
	public boolean isThreadActive() {return m_bInit;}
	public void run() {
		Debug.println(">>> ClientThread::run");
		m_socket = new ClientSocket ();
		if (! m_socket.connect(m_strURL)) {	// dirty for now....
			m_app.doShutdown();
		}
		m_app.getClientMessages().sendStartMessage();
		m_bInit = true;
		while (true) {
//			Debug.println("ClientThread::run before process msg");
			m_app.getClientMessages().processMessage(m_socket.getScrabbleMessage());
//			Debug.println("ClientThread::run after process msg");
		}
//		m_socket.close();
//		Debug.println("<<< ClientThread::run");
	}
}

