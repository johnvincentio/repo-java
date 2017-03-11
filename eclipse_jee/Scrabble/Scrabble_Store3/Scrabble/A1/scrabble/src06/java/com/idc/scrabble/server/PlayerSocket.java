package com.idc.scrabble.server;

import com.idc.scrabble.utils.Debug;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerSocket {
	private Socket m_socket;
	private DataInputStream m_input;
	private DataOutputStream m_output;
	private boolean m_bConnected = false;

	public PlayerSocket() {
		Debug.println("--- PlayerSocket::PlayerSocket");
	}
	public boolean connect (ServerSocket ss) {
		Debug.println(">>> PlayerSocket::Connect");
		if (m_bConnected) return true;
		try {
			m_socket = ss.accept();
			m_input = new DataInputStream (m_socket.getInputStream());
			m_output = new DataOutputStream (m_socket.getOutputStream());
		}
		catch (IOException ioException) {
			Debug.println("Unable to open socket");
			return false;
		}
		m_bConnected = true;
		Debug.println("<<< PlayerSocket::Connect");
		return m_bConnected;
	}
	public synchronized void close () {
		Debug.println(">>> PlayerSocket::close");
		if (! m_bConnected) return;
		try {
			m_input.close();
			m_output.close();
			m_socket.close();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< PlayerSocket::close");
	}
	public synchronized String getScrabbleMessage () {
		Debug.println(">>> PlayerSocket::getScrabbleMessage");
		String msg = "";
		try {
			msg = m_input.readUTF();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< PlayerSocket::getScrabbleMessage; "+msg);
		return msg;
	}
	public synchronized void sendScrabbleMessage (final String msg) {
		Debug.println(">>> PlayerSocket::sendScrabbleMessage");
		if (! m_bConnected) return;
		try {
			m_output.writeUTF(msg);
			m_output.flush();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< PlayerSocket::sendScrabbleMessage");
	}
}

