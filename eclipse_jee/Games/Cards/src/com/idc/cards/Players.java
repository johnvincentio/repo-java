package com.idc.cards;

import java.util.ArrayList;
import java.util.List;

import com.idc.trace.LogHelper;

public class Players {
	private MyServer m_server; // back ref to the server

	private List<PlayerInfo> m_players = new ArrayList<PlayerInfo>(); // list of the players

	private final int m_nMaxPlayers; // maximum number of players

	private int m_nCurrentPlayer; // current player

	private int m_nRank = 0; // player ranking counter

	public Players(MyServer server, int num) {
		LogHelper.info("Players(constructor); number of players " + num);
		m_server = server;
		m_nMaxPlayers = num;
		for (int i = 0; i < m_nMaxPlayers; i++)
			m_players.add(new PlayerInfo(num, new MyWorker(i, m_server)));
		m_nCurrentPlayer = -1;
	}

	private MyWorker getWorker(int num) {
		return getPlayerInfo(num).getWorker();
	}

	public void setWorkerStart(int num) {
		getWorker(num).start();
	}

	public void setWorkerResume(int num) {
		getWorker(num).setResume();
	}

	public void setWorkerResume() {
		setWorkerResume(m_nCurrentPlayer);
	}

	public void setWorkerStop(int num) {
		getWorker(num).setStop();
	}

	public void setWorkerTurn(int num) {
		getPlayerInfo(num).getHand().add(m_server.getDeck().takeCard());
		getPlayerInfo(num).setMoved(true); // set player moved flag
	}

	private PlayerInfo getPlayerInfo(int num) {
		return (PlayerInfo) m_players.get(num);
	}

	private boolean isFinished(int points) { // set victory condition
		if (points > 20)
			return true;
		return false;
	}

	public void rankPlayers() {
		for (int i = 0; i < m_nMaxPlayers; i++) {
			PlayerInfo player = getPlayerInfo(i);
			if (! player.isDone() && isFinished(player.getPoints())) {
				player.setDone();
				player.setRank(++m_nRank); // rank the player
			}
		}
	}

	public boolean hasCurrentPlayerMoved() {
		if (m_nCurrentPlayer < 0)
			return true; // no current player
		if (! getWorker(m_nCurrentPlayer).isAlive())
			return false; // thread died
		if (! getWorker(m_nCurrentPlayer).getWaiting())
			return false; // not done
		if (! getPlayerInfo(m_nCurrentPlayer).getMoved())
			return false; // moved flag not set
		return true;
	}

	public boolean hasNextValidPlayer() {
		int count = 0; // let all players finish
		for (int i = 0; i < m_nMaxPlayers; i++) {
			if (getPlayerInfo(i).isDone())
				count++;
		}

		count = 0;
		while (true) { // find a valid next player
			m_nCurrentPlayer++; // try next player
			count++;
			if (count > m_nMaxPlayers) break; // only loop once through the players
			if (m_nCurrentPlayer >= m_nMaxPlayers) m_nCurrentPlayer = 0; // back to the top
			PlayerInfo playerlnfo = getPlayerInfo(m_nCurrentPlayer);
			if (! playerlnfo.isDone()) {
				playerlnfo.setMoved(false);
				return true; // is this player finished
			}
		}
		return false; // could not find a valid next player
	}

	public void showHands(String msg) {
		LogHelper.info("showHands; " + msg);
		for (int i = 0; i < m_nMaxPlayers; i++) {
			PlayerInfo player = getPlayerInfo(i);
			LogHelper.info("Player " + i + " Ranked " + player.getRank()
					+ " has the following hand " + player.showHand());
		}
		LogHelper.info("showHands complete");
	}

	public class PlayerInfo {
		private int m_nPlayer; // player number

		private MyWorker m_worker; // worker thread

		private Hand m_hand; // players hand

		private boolean m_bDone; // true when player has reached a victory condition

		private int m_nRank; // player ranking

		private boolean m_bMoved; // true if worker has completed a move

		public PlayerInfo(int num, MyWorker worker) {
			m_nPlayer = num;
			m_worker = worker;
			m_hand = new Hand();
			m_bDone = false;
			m_nRank = 0;
			m_bMoved = false;
		}

		public int getPlayer() {
			return m_nPlayer;
		}

		public MyWorker getWorker() {
			return m_worker;
		}

		public Hand getHand() {
			return m_hand;
		}

		public boolean isDone() {
			return m_bDone;
		}

		public void setDone() {
			m_bDone = true;
		}

		public String showHand() {
			return m_hand.toString();
		}

		public int getPoints() {
			return m_hand.getPoints();
		}

		public int getRank() {
			return m_nRank;
		}

		public void setRank(int num) {
			m_nRank = num;
		}

		public boolean getMoved() {
			return m_bMoved;
		}

		public void setMoved(boolean b) {
			m_bMoved = b;
		}
	}
}
