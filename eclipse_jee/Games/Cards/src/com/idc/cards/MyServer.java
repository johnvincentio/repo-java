package com.idc.cards;

import com.idc.trace.LogHelper;

public class MyServer extends Thread {
	private final int m_nMaxPlayers; // maximum number of players

	private Players m_players; // the players

	private Deck m_deck; // the deck of cards

	public MyServer(final int num) {
		m_nMaxPlayers = num;
		m_players = new Players(this, m_nMaxPlayers);
		m_deck = new Deck();
	}

	public void run() {
		LogHelper.info("MyServer:run");
		for (int i = 0; i < m_nMaxPlayers; i++)
			m_players.setWorkerStart(i); // start the player threads
		mySleep(1000); // let the threads run until they suspend

		while (true) {
			if (m_deck.isEmpty()) break; // no cards left = game over
			if (! m_players.hasNextValidPlayer()) break; // no valid next player = game over
			m_players.setWorkerResume(); // resume the current player thread
			if (!hasCurrentPlayerMoved()) break; // wait for worker thread
			m_players.rankPlayers(); // do any new ranking
			m_players.showHands("MyServer:run; showHands()");
		}
		LogHelper.info("MyServer:run; stopping threads");
		for (int i = 0; i < m_nMaxPlayers; i++) {
			m_players.setWorkerStop(i); // set worker thread to stop
			m_players.setWorkerResume(i); // set worker thread to resume, and thus stop
		}
		mySleep(1000); // let threads get done
		m_players.showHands("MyServer:all done"); // show the player data
	}

	private boolean hasCurrentPlayerMoved() { // allow current player to finish a turn
		yield();
		for (int loop = 0; loop < 5; loop++) {
			if (m_players.hasCurrentPlayerMoved()) return true;
			LogHelper.info("before yield()");
			yield();
		}
		return false;
	}

	private void mySleep(int num) { // take a short nap
		try {
			Thread.sleep(num);
		} catch (InterruptedException ie) {
			LogHelper.error("Could not sleep");
		}
	}

	public Deck getDeck() {
		return m_deck;
	}

	public void workerMyTurn(int num) {m_players.setWorkerTurn(num);} // used by worker threads
}
