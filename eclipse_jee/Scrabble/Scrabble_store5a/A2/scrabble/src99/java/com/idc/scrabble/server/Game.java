package com.idc.scrabble.server;

import com.idc.scrabble.utils.PacketReader;
import com.idc.scrabble.utils.PacketWriter;
import com.idc.scrabble.utils.Constants;
import com.idc.scrabble.utils.Debug;

import java.util.List;
import java.util.ArrayList;
import java.net.ServerSocket;

public class Game {
	private ServerApp m_app;
	private Game m_game;
	private Scorer m_scorer;
	private ServerSocket m_server;
	private TilesBag m_tilesBag;		// bag of tiles
	private List m_listPlayers;		// Collection of Players
	private int m_nCurrentPlayer = 0;	// start with the first player
	private int m_lastKeyID = 10000;	// unique key id - client/server 
	private int m_nMaxPlayers;			// server configurable.
	private boolean m_bIsGameOver = false;

	public Game (ServerApp app, ServerSocket ss) {
		Debug.println(">>> Game::Game");
		m_app = app;
		m_server = ss;
		m_scorer = new Scorer(this);
		m_tilesBag = new TilesBag();
		m_listPlayers = new ArrayList();	// PlayerInfo collection
		Debug.println("<<< Game::Game");
	}
//
//	Cycle through the players
//
	public void startServer (int numThreads) {	// max no players
		Debug.println(">>> Game::startServer");
		m_game = m_app.getGameRef();
		m_nMaxPlayers = numThreads;
		for (int i=0; i<m_nMaxPlayers; i++) {
			m_lastKeyID++;
			PlayerThread playerThread = new PlayerThread(m_game, m_server, i);
			PlayerInfo playerInfo = new PlayerInfo (playerThread, m_lastKeyID, i);
			m_listPlayers.add(playerInfo);	// new player
			if (i == 0) playerThread.setResume();	// first player can move
			playerThread.start();		// start the player thread
		}
		Debug.println("<<< Game::startServer");
	}
//
// handle the board
//
	public boolean isGameOver() {return m_bIsGameOver; }
	private void setGameOver() {
		Debug.println("***** Game is Over *************");
		m_bIsGameOver = true;
	}
//
//	handle the current user's rack
//
	public void replaceRackLetter(boolean bTossBack, int place, int letter) {
//		Debug.println(">>> GamePlayer::replaceRackLetter");
		int num;
		if (letter < 0) return;		// no letter
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		List rack = playerInfo.getRack();
		for (int i=0; i<rack.size(); i++) {
			if (letter == ((Integer) rack.get(i)).intValue()) {
				rack.remove(i);		 // remove this letter
				if (bTossBack)
					m_tilesBag.addLetterToBag(letter);	// put letter back into bag
				rack.add(new Integer(m_tilesBag.getNextLetterFromBag())); // get new letter
				break;		// only one instance of the letter
			}
		}
//		Debug.println("<<< GamePlayer::replaceRackLetter");
	}
//
//	methods to update playerinfo, the place to find information
//	about the games players.
//
//	Be careful. This is actually being executed from the current player thread.
//	If you do not need to change the current player, just leave it be.
//	If need to cycle to the next player, resume that player and be sure to suspend
//	the current player thread. Be careful, get the right thread!!!!!
//
//	You cannot just drop threads. The main thread stopped after starting the 
//	player threads and so the player threads have to get it sorted.
//	The player threads must stay up. If a thread goes down, the current thread 
//	must know not to turn over control. Dead threads must be marked as such,
//	the cycling process must pass them over. Check a thread is actually active
//	before handing over control.
//
//	This method needs to signal all threads who is the active player. This code was
//	originally called in the validate() method, but this method plays with 
//	m_nCurrentPlayer which the broadCastCurrentPlayer method was using to 
//	determine what to send and to whom.
//
//	Yuk! This method must now handle the signalling!
//
	private void setNextValidPlayerForMove() {	// handle next player please!
		Debug.println(">>> Game::setNextValidPlayerForMove");
		Debug.println("Current player "+m_nCurrentPlayer);
		int nNextPlayer;
		int nTries = 0;				// number of attempts to find an OK player
		PlayerInfo playerInfo;
		while (nTries < m_nMaxPlayers*2) {	 // this method MUST EXIT - NOT TERMINATE
			nTries++;
			nNextPlayer = m_nCurrentPlayer + 1;	// try this player
			if (nNextPlayer >= m_listPlayers.size())
				nNextPlayer = 0;		// recycle the list
			playerInfo = (PlayerInfo) m_listPlayers.get(nNextPlayer);
			if (playerInfo.isPlayerRetired()) continue;	// player does not want to play
			if (playerInfo.isThreadDead()) continue;	// do not use.

			try {		// is the next player thread dead?
				Debug.println("checking next player thread is alive");
				if (! playerInfo.isCheckThreadAlive()) { // thread is dead 
					Debug.println("next player thread is not alive");
					throw new Exception("Thread is not alive");	// - mark it as such
				}
			}
			catch (Exception ex) {
				Debug.println("FAILED - set next player thread to DEAD");
				playerInfo.setThreadDead();
				continue;
			}
			Debug.println("survived the next player thread check");
			if (! playerInfo.isThreadInitialized()) continue;	// no player there.

// this player looks like a valid choice

			Debug.println("Valid Player is "+nNextPlayer);
			Debug.println("Current Player is "+m_nCurrentPlayer);
			if (nNextPlayer == m_nCurrentPlayer) {
				Debug.println("current player is the next player.....");
				return;			// next player is the current player
			}

			try {		// being careful, check next player thread is still OK
				Debug.println("rechecking next player thread is alive");
				if (! playerInfo.isCheckThreadAlive()) {
					Debug.println("next player thread is not alive");
					throw new Exception("Thread is not alive");
				}
			}
			catch (Exception ex) {
				Debug.println("FAILED - set next player thread to DEAD");
				playerInfo.setThreadDead();
				continue;
			}
//
//	this looks dangerous but it's OK. This just sets a flag which will not be checked
//	until this method completes.
//	Code looks confusing, current to previous, next to current.	
//		This is not reality, just what I want to happen.	
//	Note that previous thread is actually the thread this is running this code.
//
			Debug.println("Current thread becomes previous thread");
			int nOldPlayer = m_nCurrentPlayer;	// previous player
			m_nCurrentPlayer = nNextPlayer;	// this is now the current player

			Debug.println("Disabling the previous player thread");
			playerInfo = (PlayerInfo) m_listPlayers.get(nOldPlayer);
			playerInfo.setThreadSuspend();	// disable previous player thread
			sendCurrentPlayer (nOldPlayer);	//	signal new current player to previous player

			Debug.println("Resume the next player thread");
			sendCurrentPlayer (nNextPlayer);//	signal new current player
			playerInfo = (PlayerInfo) m_listPlayers.get(nNextPlayer);
			playerInfo.setThreadResume();	// enable next player thread

			Debug.println("Set current thread to "+m_nCurrentPlayer);
			Debug.println("<<< Game::setNextValidPlayerForMove");
			return;			// this allows current thread to go to its wait state
		}
//		setGameOver();
		Debug.println("<<< Game::setNextValidPlayerForMove");
	}
//
//	this method is responsible for determining the state of a player
//	thread. it needs to check the playerinfo flags, the player thread class flags
//	and needs to query the OS for the status of the thread.
//
//	This could be entertaining as I do not really know how to check for a dead thread.
//
	private boolean isThisPlayerAPlayingPlayer(PlayerInfo playerInfo) {
		Debug.println(">>> Game::isThisPlayerAPlayingPlayer");
		if (playerInfo.isThreadDead()) return false;
		try {		// is the next player thread dead?
			Debug.println("checking next player thread is alive");
			if (! playerInfo.isCheckThreadAlive()) { // thread is dead 
				Debug.println("next player thread is not alive");
				throw new Exception("Thread is not alive");	// - mark it as such
			}
		}
		catch (Exception ex) {
			Debug.println("FAILED - The thread is DEAD");
			return false;
		}
		Debug.println("survived the next player thread check");
		if (! playerInfo.isThreadInitialized()) return false;
		return true;		// valid playing player
	}
	private void setUserName (int place, String name) {
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		playerInfo.setName (name);
	}
	private void createUserRack (int place) {
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		playerInfo.setRack (m_tilesBag.getLettersFromBag (Constants.RACK_SIZE));
	}
	public void addToPlayerScore (int place, int increment) {
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		playerInfo.incrementScore (increment);
	}
//
// Send various message types to the client.
// These methods may be invoked from the listening thread and from the main
// thread.
//
	private void sendKey(int place) {
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		PacketWriter packet = new PacketWriter(Constants.KEY);
		packet.append(playerInfo.getKey());
		packet.append(playerInfo.getName());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
	}
	private void broadCastBoard() {	// send Board to all valid players
		PlayerInfo playerInfo;
		for (int i=0; i < m_listPlayers.size(); i++) {
			playerInfo = (PlayerInfo) m_listPlayers.get(i);
			if (isThisPlayerAPlayingPlayer(playerInfo))
				sendBoard(i);
		}
	}
	private void broadCastScores() {	// send Scores to all valid players
		PlayerInfo playerInfo;
		for (int i=0; i < m_listPlayers.size(); i++) {
			playerInfo = (PlayerInfo) m_listPlayers.get(i);
			if (isThisPlayerAPlayingPlayer(playerInfo))
				sendScores(i);
		}
	}
	private void broadCastTileCount() {	// send TileCount to all valid players
		PlayerInfo playerInfo;
		for (int i=0; i < m_listPlayers.size(); i++) {
			playerInfo = (PlayerInfo) m_listPlayers.get(i);
			if (isThisPlayerAPlayingPlayer(playerInfo))
				sendTileCount(i);
		}
	}
	private void broadCastCurrentPlayer() {	
		PlayerInfo playerInfo;
		for (int i=0; i < m_listPlayers.size(); i++) {
			playerInfo = (PlayerInfo) m_listPlayers.get(i);
			if (isThisPlayerAPlayingPlayer(playerInfo))
				sendCurrentPlayer(i);
		}
	}
	private void sendBoard(int place) {
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		PacketWriter packet = new PacketWriter(Constants.BOARD);
		packet.append(playerInfo.getKey());
		for (int i=0; i<Constants.BOARD_SIZE; i++) {
			for (int j=0; j<Constants.BOARD_SIZE; j++)
				packet.append(m_scorer.getBoard(i,j));
		}
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
	}
	private void sendScores(int place) {
		Debug.println(">>> Game::sendScores");
		PlayerInfo playerInfo;
		PacketWriter packet = new PacketWriter(Constants.SCORES);
		playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		for (int i=0; i < m_listPlayers.size(); i++) {
			playerInfo = (PlayerInfo) m_listPlayers.get(i);
			if (! isThisPlayerAPlayingPlayer(playerInfo)) continue;
			packet.append(playerInfo.getName());
			packet.append(playerInfo.getKey());
			packet.append(playerInfo.getScore());
		}
		playerInfo = (PlayerInfo) m_listPlayers.get(place);
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendScores");
	}
	private void sendTileCount(int place) {
		Debug.println(">>> Game::sendTileCount");
		PacketWriter packet = new PacketWriter(Constants.TILES_LEFT);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		packet.append(m_tilesBag.getSize());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendTileCount");
	}
	private void sendRack(int place) {
		Debug.println(">>> GamePlayer::sendRack");
		Integer intno;
		int num;
		PacketWriter packet = new PacketWriter(Constants.RACK);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		List rack = playerInfo.getRack();
		for (int i=0; i<rack.size(); i++)
			packet.append(((Integer) rack.get(i)).intValue());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< GamePlayer::sendRack");
	}
	private void sendCurrentPlayer(int place) {
		Debug.println(">>> Game::sendCurrentPlayer");
		PacketWriter packet = new PacketWriter(Constants.PLAYER);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		PlayerInfo p2 = (PlayerInfo) m_listPlayers.get(m_nCurrentPlayer);
		packet.append(p2.getKey());			 // keyid of current player
		Debug.println("Player msg "+packet.getString());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendCurrentPlayer");
	}
	public void sendBadMoveMessage (int place, String msg) {
		Debug.println(">>> Game::sendBadMoveMessage, place "+place);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		PacketWriter packet = new PacketWriter(Constants.BADMOVE);
		packet.append(playerInfo.getKey());
		packet.append(msg);
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendBadMoveMessage");
	}
//
// processMessage - will only be invoked from the socket listening thread.
// 		Handle the incoming message from the client.
// 			Be Thread-safe!!!!	
//
	public synchronized void processMessage(int seatNo, String msg) {
		Debug.println(">>> GamePlayer::processMessage()"+msg);
		PacketReader packetReader = new PacketReader (msg);
		if (packetReader.length() < 1) {
			Debug.println("<<< GamePlayer::processMessage(); nothing");
			return;
		}
		Debug.println("seatno "+seatNo);
		int packetType = packetReader.getNextInt();
		Debug.println("type "+packetType);
		int keyid = packetReader.getNextInt();
		Debug.println("keyid "+keyid);

		switch (packetType) {
			case Constants.START:
				sendKey(seatNo);		// send the key id for this user
				break;
			case Constants.NAME:
				String strName = packetReader.getNext();
				Debug.println("username |"+strName+"|");
				setUserName(seatNo,strName);
				break;
			case Constants.INIT:	// client has been initialised
				createUserRack(seatNo);
				sendBoard(seatNo);		// send the board
				sendRack(seatNo);		// send the rack
				sendScores(seatNo);		// send the scores
				sendTileCount(seatNo);		// send the tile count
				sendCurrentPlayer(seatNo);	// send current player
				break;
			case Constants.MOVE:	// player moved
				int tmpBoard[][] = new int[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
				int pos = 0;
				int row, col;
				while (packetReader.hasNext()) {	// load client board
					row = pos / Constants.BOARD_SIZE;
					col = pos - row * Constants.BOARD_SIZE;
   					tmpBoard[row][col] = packetReader.getNextInt();
					pos++;
				}
				boolean bValid = m_scorer.validateThisUserMove(tmpBoard,seatNo);
				if (bValid) {
					Debug.println("Handling valid move");
					sendRack(seatNo);		// send the rack
					broadCastBoard();		// send the board
					broadCastScores();		// send the scores
					broadCastTileCount();		// send the tile count
					m_scorer.setBoardNotStillEmpty();	// first move is special
					setNextValidPlayerForMove();	// cycle to the next player
//					broadCastCurrentPlayer();	// send current player
				}
				else {
					Debug.println("Handling illegal move");
					sendBoard(seatNo);		// send the board
					sendRack(seatNo);		// send the rack
					sendScores(seatNo);		// send the scores
					sendTileCount(seatNo);		// send the tile count
					sendCurrentPlayer(seatNo);	// send current player
				}
				break;
			case Constants.PASS:	// player passed
				while (packetReader.hasNext())
					replaceRackLetter(true,seatNo,packetReader.getNextInt());
				sendBoard(seatNo);		// send the board
				sendRack(seatNo);		// send the rack
				sendScores(seatNo);		// send the scores
				sendTileCount(seatNo);		// send the tile count
				setNextValidPlayerForMove();	// cycle to the next player
//				sendCurrentPlayer(seatNo);	// send current player
				break;
			case Constants.CLEAR:	// reset, no client move
				sendBoard(seatNo);		// send the board
				sendRack(seatNo);		// send the rack
				sendScores(seatNo);		// send the scores
				sendTileCount(seatNo);		// send the tile count
				sendCurrentPlayer(seatNo);	// send current player
				break;
			default:
				break;
		}
		Debug.println("<<< GamePlayer::processMessage()");
	}
}

