package com.idc.scrabble.client;

import com.idc.scrabble.utils.Constants;
import com.idc.scrabble.utils.Debug;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientSocket {
	private Socket m_socket;
	private DataInputStream m_input;
	private DataOutputStream m_output;
	private boolean m_bConnected = false;

	public ClientSocket () {}
	public boolean isConnected() {return m_bConnected;}
	public boolean connect (String strURL) {
		if (m_bConnected) return true;
		Debug.println(">>> ClientSocket::connect");
		try {
			m_socket = new Socket (strURL,Constants.SCRABBLE_PORT);
			m_input = new DataInputStream (m_socket.getInputStream());
			m_output = new DataOutputStream (m_socket.getOutputStream());
		}
		catch (IOException ioException) {
			Debug.println("Unable to connect to server");
			return false;
		}
		m_bConnected = true;
		Debug.println("<<< ClientSocket::connect");
		return m_bConnected;
	}
	public void close () {
		Debug.println(">>> ClientSocket::close");
		if (! m_bConnected) return;
		try {
			m_input.close();
			m_output.close();
			m_socket.close();
		}
		catch (IOException ioException) { 
			Debug.println("Unable to close connection to server");
		}
		m_bConnected = false;
		Debug.println("<<< ClientSocket::close");
	}
	public String getScrabbleMessage () {
		Debug.println(">>> ClientSocket::getScrabbleMessage");
		String msg = "";
		try {
			msg = m_input.readUTF();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< ClientSocket::getScrabbleMessage;"+msg);
		return msg;
	}
	public synchronized void sendScrabbleMessage (final String msg) {
		Debug.println(">>> ClientSocket::sendScrabbleMessage");
		Debug.println("Message |"+msg+"|");
		if (! m_bConnected) return;
		try {
			m_output.writeUTF(msg);
			m_output.flush();
		}
		catch (IOException ioException) { 
			ioException.printStackTrace();
			System.exit(1);
		}
		Debug.println("<<< ClientSocket::sendScrabbleMessage");
	}
}

