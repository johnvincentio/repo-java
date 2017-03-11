
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

public class ClientPlayer extends Thread {
	private ScrabbleClient m_app;
	private String m_strURL;
	private ScrabbleClientSocket m_socket;
	private boolean m_bInit = false;

	public ClientPlayer (ScrabbleClient app, String strURL) {
		Debug.println(">>> ClientPlayer::ClientPlayer");
		m_app = app;
		m_strURL = strURL;
		m_socket = null;
		m_bInit = false;
		Debug.println("<<< ClientPlayer::ClientPlayer");
	}
	public ScrabbleClientSocket getSocket() {return m_socket;}
	public boolean isThreadActive() {return m_bInit;}
	public void run() {
		Debug.println(">>> ClientPlayer::run");
		m_socket = new ScrabbleClientSocket ();
		if (! m_socket.connect(m_strURL)) {	// dirty for now....
			String msg = "Unable to play Scrabble at this moment. Please try again later";
			String title = "Scrabble Trouble";
			JOptionPane.showMessageDialog(m_app,msg,title,JOptionPane.ERROR_MESSAGE);
			m_app.doStopClient();
		}
		m_app.sendStartMessage();
		m_bInit = true;

		while (true) {
//			Debug.println("ClientPlayer::run before process msg");
			m_app.processMessage(m_socket.getScrabbleMessage());
//			Debug.println("ClientPlayer::run after process msg");
		}
//		m_socket.close();
//		Debug.println("<<< ClientPlayer::run");
	}
}

