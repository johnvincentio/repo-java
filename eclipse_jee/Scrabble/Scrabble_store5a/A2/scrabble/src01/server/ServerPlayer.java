import java.net.*;
import java.io.*;
import java.util.*;

public class ServerPlayer extends Thread {
	private Game m_game;
	private ServerSocket m_server;
	private ScrabbleServerSocket m_socket;
	private int m_seatNo;
	private boolean m_bInit;
	private boolean m_bPleaseSuspendThisThread;
	private boolean m_bThreadWaiting;

	public ServerPlayer (Game game, ServerSocket ss, int seatNo) {
		Debug.println(">>> ServerPlayer::ServerPlayer; Number "+seatNo);
		m_game = game;				// ref to game class
		m_server = ss;				// ServerSocket, need for Socket
		m_seatNo = seatNo;			// Player seat number at the table.
		m_socket = null;			// not yet!
		m_bInit = false;			// true when socket connected
		m_bThreadWaiting = false;	// true when waiting
		m_bPleaseSuspendThisThread = true;	// suspend on startup
		Debug.println("<<< ServerPlayer::ServerPlayer");
	}
	public ScrabbleServerSocket getSocket() {return m_socket;}
	public void setSuspend() {m_bPleaseSuspendThisThread = true;}
	synchronized void setResume() {
		m_bPleaseSuspendThisThread = false;
		notify();
	}
	public boolean isThreadInitialized() {return m_bInit;}
	public void run() {
		Debug.println(">>> ServerPlayer::run");
		m_socket = new ScrabbleServerSocket ();
		if (! m_socket.connect(m_server)) return;	// while waiting here
		m_bInit = true;		// for a connection, the suspended flag may
		m_bThreadWaiting = false;	// have been set to false by another thread

		while (! m_game.isGameOver()) {
			try {	// thus, on initialization, the thread may not be suspended.
				synchronized (this) {
					while (m_bPleaseSuspendThisThread) {
						m_bThreadWaiting = true;
						Debug.println("suspending thread");
						wait();
					}
				}
			}
			catch (InterruptedException e) {
				Debug.println("---ServerPlayer::run; exception - FATAL");
            	e.printStackTrace();
			}
			m_bThreadWaiting = false;
			Debug.println("thread is awake");
			Debug.println("ServerPlayer::run before get msg from socket");
			final String strTmp = m_socket.getScrabbleMessage();
			Debug.println("ServerPlayer::run before process msg");
			m_game.processMessage(m_seatNo,strTmp);
			Debug.println("ServerPlayer::run after process msg");
		}
		m_socket.close();
		Debug.println("<<< ServerPlayer::run");
	}
}

