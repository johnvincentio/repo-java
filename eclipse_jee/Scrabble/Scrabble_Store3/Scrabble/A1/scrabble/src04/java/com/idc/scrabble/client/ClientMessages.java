//
// Send various message types to the server.
// These methods may be invoked from the listening thread and from the main
// thread.
//
package com.idc.scrabble.client;

import com.idc.scrabble.utils.PacketReader;
import com.idc.scrabble.utils.PacketWriter;
import com.idc.scrabble.utils.Constants;
import com.idc.scrabble.utils.Debug;

public class ClientMessages {
	private ScrabbleClient m_app;
	public ClientMessages(ScrabbleClient app) {m_app = app;}
	private ScrabbleClientSocket getMySocket() {return m_app.getMySocket();}
	private int getKeyId() {return m_app.getKeyId();}
	private String getPlayerName() {return m_app.getPlayerName();}
	private int getBoardLetterNumber(int i, int j) {
		return m_app.getBoard(i,j).getLetterNumber();
	}
	private int getRackLetterNumber(int i) {
		return m_app.getRack(i).getLetterNumber();
	}
	public void close() {
		Debug.println(">>> ClientMessages::close()");
		if (getMySocket() != null) {
			Debug.println("stage 1");
			sendExitMessage();
			Debug.println("stage 2");
			getMySocket().close();
			Debug.println("stage 3");
		}
		Debug.println("<<< ClientMessages::close()");
	}

	public void sendStartMessage() {
		Debug.println(">>> sendStartMessage");
		PacketWriter packet = new PacketWriter(Constants.START);
		packet.append(-1);
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendStartMessage");
	}
	public void sendInitMessage() {
		Debug.println(">>> sendInitMessage");
		PacketWriter packet = new PacketWriter(Constants.INIT);
		packet.append(getKeyId());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendInitMessage");
	}
	public void sendExitMessage() {
		Debug.println(">>> sendExitMessage");
		PacketWriter packet = new PacketWriter(Constants.EXIT);
		packet.append(getKeyId());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendExitMessage");
	}
	public void sendNameMessage() {
		Debug.println(">>> sendNameMessage");
		PacketWriter packet = new PacketWriter(Constants.NAME);
		packet.append(-1);
		packet.append(getPlayerName());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendNameMessage");
	}
	public void sendClearMessage() {
		Debug.println(">>> sendClearMessage");
		PacketWriter packet = new PacketWriter(Constants.CLEAR);
		packet.append(getKeyId());
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendClearMessage");
	}
	public void sendMoveMessage() {
		Debug.println(">>> sendMoveMessage");
		PacketWriter packet = new PacketWriter(Constants.MOVE);
		packet.append(getKeyId());
		for (int i=0; i<Constants.BOARD_SIZE; i++) {
			for (int j=0; j<Constants.BOARD_SIZE; j++)
				packet.append(getBoardLetterNumber(i,j));
//m_board[i][j].getLetterNumber());
		}
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendMoveMessage");
	}
	public void sendPassMessage() {
		Debug.println(">>> sendPassMessage");
		PacketWriter packet = new PacketWriter(Constants.PASS);
		packet.append(getKeyId());
		for (int i=0; i<Constants.RACK_SIZE; i++)
//			packet.append(m_playerRack[i].getLetterNumber());
			packet.append(getRackLetterNumber(i));
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendPassMessage");
	}
//
// processMessage - will only be invoked from the socket listening thread.
// 		Handle the incoming message from the server.
// 			Be Thread-safe!!!!	
//
	public void processMessage(final String msg) {
		String strTmp = msg;
		Debug.println(">>> processMessage()"+strTmp);
		PacketReader packetReader = new PacketReader (strTmp);
		if (packetReader.length() < 1) {
			Debug.println("<<< processMessage(); nothing");
			return;
		}
		int packetType = packetReader.getNextInt();
		Debug.println("type "+packetType);
		int keyid = packetReader.getNextInt();
		Debug.println("keyid "+keyid);

		StringBuffer buffer = new StringBuffer();
		switch (packetType) {
			case Constants.KEY:
				m_app.setKeyId(keyid);
				buffer = new StringBuffer();
				buffer.append("Your Player ID is ");
				buffer.append(keyid);
				m_app.setMessagesArea (buffer.toString());
				sendNameMessage();
				sendInitMessage();
				break;
			case Constants.BOARD:
				int pos = 0;
				int row, col;
				while (packetReader.hasNext()) {
					row = pos / Constants.BOARD_SIZE;
					col = pos - row * Constants.BOARD_SIZE;
// m_app.setSquareLetter (m_board[row][col], packetReader.getNextInt());
   					m_app.setBoardLetter (row, col,  packetReader.getNextInt());
					pos++;
				}
				break;
			case Constants.SCORES:
				while (packetReader.hasNext(3)) {
					buffer = new StringBuffer();
					buffer.append(packetReader.getNext());
					buffer.append(",");
					buffer.append(packetReader.getNext());
					buffer.append(",");
					buffer.append(packetReader.getNext());
   					m_app.setMessagesArea (buffer.toString());
				}
				break;
			case Constants.TILES_LEFT:
				int iTiles = packetReader.getNextInt();
				buffer = new StringBuffer();
				buffer.append("Tiles Remaining=");
				buffer.append(iTiles);
				m_app.setMessagesArea (buffer.toString());
				break;
			case Constants.RACK:
				col = 0;
				while (packetReader.hasNext()) {
//m_app.setSquareLetter (m_playerRack[col++], packetReader.getNextInt());
   					m_app.setRackLetter (col++,  packetReader.getNextInt());
				}
				break;
			case Constants.PLAYER:
				int keyCurrent = packetReader.getNextInt();
				Debug.println("keyid "+keyid+" currentPlayerKeyid "+keyCurrent);
				Debug.println("getKeyId() "+getKeyId());

				buffer = new StringBuffer();
				buffer.append("Current Player ID is ");
				buffer.append(keyCurrent);
				buffer.append(". Your Player ID is ");
				buffer.append(keyid);
				m_app.setMessagesArea (buffer.toString());
				if (keyCurrent == getKeyId()) {
					m_app.setMyTurn(true);
					m_app.setPlayerMovedInfo("Your Turn","Scrabble");
				}
				else {
					m_app.setMyTurn(false);
				}
				break;
			case Constants.BADMOVE:
					m_app.setPlayerMovedError(packetReader.getNext(),"Scrabble");
				break;
			default:
				break;
		}
		Debug.println("<<< processMessage()");
	}
}

