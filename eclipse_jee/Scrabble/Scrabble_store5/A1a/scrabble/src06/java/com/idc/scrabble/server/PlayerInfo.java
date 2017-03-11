package com.idc.scrabble.server;

import java.util.List;

public class PlayerInfo {
	private PlayerThread m_playerThread;	// Player thread
	private String m_name;					// Player name
	private int m_key;						// Player unique ID
	private int m_place;					// Seat number at the table.
	private int m_score;					// Current score
	private boolean m_bRetired;				// has player retired?
	private boolean m_bThreadDead;			// is player thread dead?
	private List m_listRack;				// Player tile rack
	public PlayerInfo (PlayerThread player, int key, int place) {
		m_playerThread = player;
		m_name = "default";
		m_key = key;
		m_place = place;
		m_score = 0;
		m_bRetired = false;
		m_bThreadDead = false;
		m_listRack = null;
	}
	public String getName() {return m_name;}
	public void setName (String name) {m_name = name;}
	public int getKey() {return m_key;}
	public int getPlace() {return m_place;}
	public int getScore() {return m_score;}
	public void incrementScore (int score) {m_score += score;}
	public List getRack() {return m_listRack;}
	public void setRack(List rack) {m_listRack = rack;}
	public boolean isThreadDead() {return m_bThreadDead;}
	public boolean isPlayerRetired() {return m_bRetired;}
	public void setThreadDead() {m_bThreadDead = true;}

	public PlayerThread getPlayer() {return m_playerThread;}
	public PlayerSocket getSocket() {return m_playerThread.getSocket();}
	public boolean isThreadInitialized() {
		return m_playerThread.isThreadInitialized();}
	public boolean isCheckThreadAlive() {return m_playerThread.isAlive();}
	public void setThreadSuspend() {m_playerThread.setSuspend();}
	public void setThreadResume() {m_playerThread.setResume();}
}

