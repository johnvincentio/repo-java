package com.idc.scrabble.server;

import com.idc.scrabble.utils.*;
import java.net.*;
import java.io.*;

public class ScrabbleServerSocket {
	private Socket m_socket;
	private DataInputStream m_input;
	private DataOutputStream m_output;
	private boolean m_bConnected = false;

	public ScrabbleServerSocket() {
		Debug.println("--- ScrabbleServerSocket::ScrabbleServerSocket");
	}
	public boolean connect (ServerSocket ss) {
		Debug.println(">>> ScrabbleServerSocket::Connect");
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
		Debug.println("<<< ScrabbleServerSocket::Connect");
		return m_bConnected;
	}
	public synchronized void close () {
		Debug.println(">>> ScrabbleServerSocket::close");
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
		Debug.println("<<< ScrabbleServerSocket::close");
	}
	public synchronized String getScrabbleMessage () {
		Debug.println(">>> ScrabbleServerSocket::getScrabbleMessage");
		String msg = "";
		try {
			msg = m_input.readUTF();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< ScrabbleServerSocket::getScrabbleMessage; "+msg);
		return msg;
	}
	public synchronized void sendScrabbleMessage (final String msg) {
		Debug.println(">>> ScrabbleServerSocket::sendScrabbleMessage");
		if (! m_bConnected) return;
		try {
			m_output.writeUTF(msg);
			m_output.flush();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< ScrabbleServerSocket::sendScrabbleMessage");
	}
}

