
import java.util.*;
import java.io.*;
import java.net.*;

public class Game {
	private ScrabbleServer m_app;
	private Game m_game;
	private ServerSocket m_server;
	private List m_tilesBag;		// bag of tiles
	private List m_listPlayers;		// Collection of Players
	private int m_nCurrentPlayer = 0;	// start with the first player
	private int m_board[][];		// value of letter on the board
	private final static int BOARD_SIZE=15;
	private static final int RACK_SIZE=7;
	private int m_lastKeyID = 10000;	// unique key id - client/server 
	private int m_nMaxPlayers;			// server configurable.
	private boolean m_bBoardIsStillEmpty = true;	// first move rules
	private boolean m_bIsGameOver = false;

	private int m_iaNumberTiles[] = 		// tiles of each letter
		{2, 9, 2, 2, 4, 12, 2, 3, 2, 9, 1,
//       '' A  B  C  D   E  F  G  H  I  J   	    
         1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4,
//       K  L  M  N  O  P  Q  R  S  T  U 
         2, 2, 1, 2, 1};
//       V  W  X  Y  Z
	private int m_iaValue[] = 			// value of tiles by letter
		{0, 1, 3, 3, 2, 1, 4, 2, 4, 1, 8,
//       '' A  B  C  D  E  F  G  H  I  J   	    
         5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1,
//       K  L  M  N  O  P   Q  R  S  T  U 
         4, 4, 8, 4, 10};
//       V  W  X  Y  Z
	private int m_MultiplierTypesByPosition[] = 	// also in ScrabbleImages.java
	       {4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4,	// 0 = no multiplier
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,	// 1 = double letter
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,	// 2 = double word
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,	// 3 = triple letter
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,	// 4 = triple word
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,	// 5 = centre - double word
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			4, 0, 0, 1, 0, 0, 0, 5, 0, 0, 0, 1, 0, 0, 4,
  			0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0,
  			0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0,
  			0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
  			1, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 1,
  			0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0,
  			0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0,
  			4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4};

	public Game (ScrabbleServer app, ServerSocket ss) {
		Debug.println(">>> Game::Game");
		m_app = app;
		m_server = ss;
		m_tilesBag = new ArrayList();
		m_listPlayers = new ArrayList();	// PlayerInfo collection
		m_board = new int[BOARD_SIZE][BOARD_SIZE];
		for (int i=0; i<BOARD_SIZE; i++) {
			for (int j=0; j<BOARD_SIZE; j++) {
				m_board[i][j] = -1;	// empty square
			}
		}
		setupLetters();			// set up the bag of tiles
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
			ServerPlayer player = new ServerPlayer(m_game, m_server, i);
			PlayerInfo playerInfo = new PlayerInfo (player, m_lastKeyID, i);
			m_listPlayers.add(playerInfo);	// new player
			if (i == 0) player.setResume();	// first player can move
			player.start();		// start the player thread
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
	private boolean isThisOccupied (int x, int y, int tmp2[][]) {
		if ((x >= 0) && (x < BOARD_SIZE) &&
			(y >= 0) && (y < BOARD_SIZE) &&
			tmp2[x][y] > -1) return true;
		return false;
	}
//
// handle the letters in the bag
//
	private void setupLetters () {		// set up the tiles in the bag
		Debug.println(">>> Game::setupLetters");
		for (int i = 0; i<m_iaNumberTiles.length; i++) {
			for (int j = 0; j< m_iaNumberTiles[i]; j++)
				addLetterToBag(i);
		}
		Debug.println("<<< Game::setupLetters");
	}
	private void addLetterToBag (int letter) {	// add letter to tile bag
//		Debug.println(">>> Game::addLetterToBag");
		m_tilesBag.add(new Integer(letter));
		Collections.shuffle(m_tilesBag);	// give it a shuffle
//		Debug.println("<<< Game::addLetterToBag");
	}
	private void removeLetterFromBag (int nRemove) {
//		Debug.println(">>> Game::removeletterFromBag; "+nRemove);
		int letter;
		for (int i=0; i<m_tilesBag.size(); i++) {
			letter = ((Integer) m_tilesBag.get(i)).intValue();
			if (letter == nRemove) {
				m_tilesBag.remove(i);	// remove from tile bag
				Debug.println("Removed letter "+letter);
				Collections.shuffle(m_tilesBag);	// give it a shuffle
				break;
			}
		}
//		Debug.println("<<< Game::removeletterFromBag");
	}
	private int getNextLetterFromBag() {	// get next letter
//		Debug.println(">>> Game::getNextletterFromBag");
		int letter = -1;
		if (m_tilesBag.size() > 0) {
			letter = ((Integer) m_tilesBag.get(0)).intValue();
			m_tilesBag.remove(0);	// remove from tile bag
		}
//		Debug.println("<<< Game::getNextletterFromBag;"+letter);
		return letter;	// always return a letter, even a null one
	}
	private List getLettersFromBag (int howmany) {	// bulk loader!
//		Debug.println(">>> Game::getLettersFromBag");
		List listLetters = new ArrayList();
		int elements = m_tilesBag.size();
		if (elements < howmany) howmany = elements;
//		Debug.println("howmany "+howmany);
		if (howmany > 0) {
			for (int i=0; i<howmany; i++)
				listLetters.add(m_tilesBag.get(i));
			for (int i=0; i<howmany; i++)
				m_tilesBag.remove(i);
		}
//		Debug.println("<<< Game::getLettersFromBag");
		return listLetters;
	}
//
//	handle the current user's rack
//
	private void replaceRackLetter(boolean bTossBack, int place, int letter) {
//		Debug.println(">>> GamePlayer::replaceRackLetter");
		int num;
		if (letter < 0) return;		// no letter
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		List rack = playerInfo.getRack();
		for (int i=0; i<rack.size(); i++) {
			if (letter == ((Integer) rack.get(i)).intValue()) {
				rack.remove(i);		 // remove this letter
				if (bTossBack)
					addLetterToBag(letter);	// put letter back into bag
				rack.add(new Integer(getNextLetterFromBag())); // get new letter
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
		playerInfo.setRack (getLettersFromBag (RACK_SIZE));
	}
	private void addToPlayerScore (int place, int increment) {
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
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.KEY);
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
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.BOARD);
		packet.append(playerInfo.getKey());
		for (int i=0; i<BOARD_SIZE; i++) {
			for (int j=0; j<BOARD_SIZE; j++)
				packet.append(m_board[i][j]);
		}
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
	}
	private void sendScores(int place) {
		Debug.println(">>> Game::sendScores");
		PlayerInfo playerInfo;
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.SCORES);
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
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.TILES_LEFT);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		packet.append(m_tilesBag.size());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendTileCount");
	}
	private void sendRack(int place) {
		Debug.println(">>> GamePlayer::sendRack");
		Integer intno;
		int num;
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.RACK);
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
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.PLAYER);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		packet.append(playerInfo.getKey());
		PlayerInfo p2 = (PlayerInfo) m_listPlayers.get(m_nCurrentPlayer);
		packet.append(p2.getKey());			 // keyid of current player
		Debug.println("Player msg "+packet.getString());
		playerInfo.getSocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< Game::sendCurrentPlayer");
	}
	private void sendBadMoveMessage (int place, String msg) {
		Debug.println(">>> Game::sendBadMoveMessage, place "+place);
		PlayerInfo playerInfo = (PlayerInfo) m_listPlayers.get(place);
		ScrabblePacketWriter packet = new ScrabblePacketWriter(PacketTypes.BADMOVE);
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
		ScrabblePacketReader packetReader = new ScrabblePacketReader (msg);
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
			case PacketTypes.START:
				sendKey(seatNo);		// send the key id for this user
				break;
			case PacketTypes.NAME:
				String strName = packetReader.getNext();
				Debug.println("username |"+strName+"|");
				setUserName(seatNo,strName);
				break;
			case PacketTypes.INIT:	// client has been initialised
				createUserRack(seatNo);
				sendBoard(seatNo);		// send the board
				sendRack(seatNo);		// send the rack
				sendScores(seatNo);		// send the scores
				sendTileCount(seatNo);		// send the tile count
				sendCurrentPlayer(seatNo);	// send current player
				break;
			case PacketTypes.MOVE:	// player moved
				int tmpBoard[][] = new int[BOARD_SIZE][BOARD_SIZE];
				int pos = 0;
				int row, col;
				while (packetReader.hasNext()) {	// load client board
					row = pos / BOARD_SIZE;
					col = pos - row * BOARD_SIZE;
   					tmpBoard[row][col] = packetReader.getNextInt();
					pos++;
				}
				boolean bValid = validateThisUserMove(tmpBoard,seatNo);
				if (bValid) {
					Debug.println("Handling valid move");
					sendRack(seatNo);		// send the rack
					broadCastBoard();		// send the board
					broadCastScores();		// send the scores
					broadCastTileCount();		// send the tile count
					m_bBoardIsStillEmpty = false;	// first move is special
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
			case PacketTypes.PASS:	// player passed
				while (packetReader.hasNext())
					replaceRackLetter(true,seatNo,packetReader.getNextInt());
				sendBoard(seatNo);		// send the board
				sendRack(seatNo);		// send the rack
				sendScores(seatNo);		// send the scores
				sendTileCount(seatNo);		// send the tile count
				setNextValidPlayerForMove();	// cycle to the next player
//				sendCurrentPlayer(seatNo);	// send current player
				break;
			case PacketTypes.CLEAR:	// reset, no client move
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
//
//  Validate the client move.
//
	private boolean validateThisUserMove(int tmpBoard[][], int place) {
		Debug.println(">>> GamePlayer::validateThisUserMove()");
		List changedLetters = new ArrayList();
		for (int i=0; i<BOARD_SIZE; i++) {
			for (int j=0; j<BOARD_SIZE; j++) {
				if (tmpBoard[i][j] != m_board[i][j])
					changedLetters.add(
						new LetterSlave(i,j,m_board[i][j],tmpBoard[i][j],
							m_MultiplierTypesByPosition[i*BOARD_SIZE+j]));
			}
		}
		if (changedLetters.size() < 1) {
			sendBadMoveMessage (place,"You Did Not Move - Try Again");
			Debug.println("<<< GamePlayer::BADMOVE - no move");
			return false;
		}
		for (int i=0; i<changedLetters.size(); i++)
			Debug.println(((LetterSlave)changedLetters.get(i)).toString());

		int cx, cy;			 //	ensure letters are in a line
		boolean bHorizWord = true;
		boolean bVertWord = true;
		int nMinX = 99; int nMaxX = 0;
		int nMinY = 99; int nMaxY = 0;
		LetterSlave slave = (LetterSlave)changedLetters.get(0);
		int sx = slave.getX(); int sy = slave.getY();
		for (int i=0; i<changedLetters.size(); i++) {
			slave = (LetterSlave)changedLetters.get(i);
			cx = slave.getX(); cy = slave.getY();
			if (cx < nMinX) nMinX = cx;
			if (cy < nMinY) nMinY = cy;
			if (cx > nMaxX) nMaxX = cx;
			if (cy > nMaxY) nMaxY = cy;
			Debug.println("i "+i+"; sx "+sx+" sy "+sy+" cx "+cx+" cy "+cy);
			if (sx != cx) bHorizWord = false;	// not the same row
			if (sy != cy) bVertWord = false;	// not the same column
		}
		Debug.println("bHoriz "+bHorizWord+" bVert "+bVertWord);
		if ((! bHorizWord) && (! bVertWord)) {	// letters not in a line
			sendBadMoveMessage (place,"Letters must be placed in a line!");
			Debug.println("<<< GamePlayer::BADMOVE letters not aligned");
			return false;
		}
		Debug.println("min(x,y) ("+nMinX+","+nMinY+")");
		Debug.println("max(x,y) ("+nMaxX+","+nMaxY+")");

		boolean bValid = true;
		if (changedLetters.size() > 1) { // ensure letters are contiguous
			int i = nMinX; int j = nMinY;
			if (bHorizWord) {
				for (j=nMinY; j<=nMaxY; j++)
					if (tmpBoard[i][j] == -1) bValid = false;
			} else {
				for (i=nMinX; i<=nMaxX; i++)
					if (tmpBoard[i][j] == -1) bValid = false;
		}	}
		Debug.println("Is it a Contiguous word "+bValid);
		if (! bValid) {
			sendBadMoveMessage (place,"Letters must be contiguous!");
			Debug.println("<<< GamePlayer::BADMOVE letters not contiguous");
			return false;
		}

		bValid = false;
		if (! m_bBoardIsStillEmpty) {	// word must be stuck to a letter
			Debug.println("check word is appended to another");
			for (int i=0; i<changedLetters.size(); i++) {
				slave = (LetterSlave)changedLetters.get(i);
				cx = slave.getX(); cy = slave.getY();
				if (isThisOccupied (cx, cy-1, m_board) ||
						isThisOccupied (cx, cy+1, m_board) ||
						isThisOccupied (cx-1, cy, m_board) ||
						isThisOccupied (cx+1, cy, m_board)) {
					bValid = true;
					break;
				}
			}
			Debug.println("Is it stuck to another word "+bValid);
			if (! bValid) {
				sendBadMoveMessage (place,"Word must be attached to another");
				Debug.println("<<< GamePlayer::BADMOVE; Word must be attached to another");
				return false;
			}
		}
//
// Make a list of all words on the board.
// if word was already there, it must have been OK. Thus, only new words
// could create problems. 
// Placing a word can create more than one new word!
//

		Debug.println("Making a list of words on the board");
		List oldWordsBoardList = new ArrayList();
		List newWordsBoardList = new ArrayList();
		makeWordsBoardList (oldWordsBoardList, m_board);
		makeWordsBoardList (newWordsBoardList, tmpBoard);
		traceAllWords ("Old list",oldWordsBoardList);
		traceAllWords ("New list",newWordsBoardList);
		rationaliseWordsList (newWordsBoardList, oldWordsBoardList);
		traceAllWords ("word list to be scored",newWordsBoardList);
//
//	Score the words. Each letter has a value, except for blanks which
//	score zero. Check for word and letter multipliers.
//
		Debug.println("Making a list of words to be scored");
		String strWord;		 	// score each word in the list
		int scoreTotal = 0;
		List wordsToBeScored = new ArrayList();
		WordSlave wordSlave;
		ScoreSlave scoreSlave;		// put words into ScoreSlave format
		for (int i=0; i<newWordsBoardList.size(); i++) {
			wordSlave = (WordSlave)newWordsBoardList.get(i);
			scoreSlave = new ScoreSlave (wordSlave, tmpBoard);
			wordsToBeScored.add(scoreSlave);
		}
		Debug.println("Scoring each word in turn");
		for (int i=0; i<wordsToBeScored.size(); i++) {
			scoreSlave = (ScoreSlave)wordsToBeScored.get(i);
			scoreSlave.traceScoreSlave("Score this word");
			scoreTotal += scoreThisWord (scoreSlave);
		}
		Debug.println("Score is "+scoreTotal);
		Debug.println("Changed Letters "+changedLetters.size());
		if (changedLetters.size() >= 7) scoreTotal += 50;
		Debug.println("Total Score is "+scoreTotal);
		addToPlayerScore (place, scoreTotal);	// increment players score

		Debug.println("Disable any used multipliers");
		for (int i=0; i<wordsToBeScored.size(); i++) {
			scoreSlave = (ScoreSlave)wordsToBeScored.get(i);
			disableMultipliersThisWord (scoreSlave);
		}

//
//	Done with checking - It is a valid move.
//		Update the board and the user's rack.
//			Update the user's score.	
//
		for (int i=0; i<BOARD_SIZE; i++) {	// update the board
			for (int j=0; j<BOARD_SIZE; j++)
				m_board[i][j] = tmpBoard[i][j];
		}
		int letter;
		for (int i=0; i<changedLetters.size(); i++) { //update the rack
			letter = ((LetterSlave)changedLetters.get(i)).getNewLetter();
			Debug.println("letter "+letter);
			replaceRackLetter(false,place,letter);
		}
		Debug.println("<<< GamePlayer::OK MOVE");
		return true;
	}
//
//	handle scoring the word
//
	private int scoreThisWord (ScoreSlave scoreSlave) {
		Debug.println(">>>scoreThisWord "+scoreSlave.getWord());
		WordSlave wordSlave = scoreSlave.getWordSlave();
		LetterSlave letterSlave;
		int nTotal = 0;
		int nLetterValue, nLetterNumber, nMulti;
		int nWordMulti = 1;
		for (int i=0; i<scoreSlave.getWordLength(); i++) {	// handle letter values
			letterSlave = scoreSlave.getLetterSlave(i);
			nLetterNumber = letterSlave.getNewLetter();
			nLetterValue = m_iaValue[nLetterNumber];
			nMulti = letterSlave.getScorer();	// no dice, must turn off after use
			Debug.println("num "+nLetterNumber+" Value "+nLetterValue+" multi "+nMulti);
			if (nMulti == 1) nLetterValue *= 2;		// double letter
			if (nMulti == 2) nWordMulti *= 2;		// double word
			if (nMulti == 3) nLetterValue *= 3;		// triple letter
			if (nMulti == 4) nWordMulti *= 3;		// triple word
			if (nMulti == 5) nWordMulti *= 2;		// centre - double word
			nTotal += nLetterValue;
			Debug.println("Letter scored "+nLetterValue);
		}
		nTotal *= nWordMulti;
		Debug.println("<<< scoreThisWord, value "+nTotal);
		return nTotal;
	}
	private void disableMultipliersThisWord (ScoreSlave scoreSlave) {
		Debug.println(">>>disableMultipliersThisWord "+scoreSlave.getWord());
		WordSlave wordSlave = scoreSlave.getWordSlave();
		LetterSlave letterSlave;
		int nX, nY, num;
		for (int i=0; i<scoreSlave.getWordLength(); i++) {	// handle letter values
			letterSlave = scoreSlave.getLetterSlave(i);
			nX = letterSlave.getX();
			nY = letterSlave.getY();
			num = nX * BOARD_SIZE + nY;
			m_MultiplierTypesByPosition[num] = 0;	// disable multiplier
		}
		Debug.println("<<< disableMultipliersThisWord");
	}
//
//	handle the word lists
//	
	private void rationaliseWordsList (List newList, List oldList) {
		String oldWord, newWord;		 // remove old words from new list
		for (int i=0; i<oldList.size(); i++) {
			oldWord = ((WordSlave)oldList.get(i)).getWord();
			Debug.println ("Word to be removed is :"+oldWord);
			for (int j=0; j<newList.size(); j++) {
				newWord = ((WordSlave)newList.get(j)).getWord();
				if (newWord.equals(oldWord))
					newList.remove(j);	// remove from list
			}
		}
	}
	private void traceAllWords (String msg, List wordsList) {
		Debug.println("Trace words in list; "+msg);
		for (int i=0; i<wordsList.size(); i++) {
			Debug.println ("Word is; "+((WordSlave)wordsList.get(i)).toString());
		}
	}
	private void makeWordsBoardList (List wordsList, int iaBoard[][]) {
		parseBoard (true, wordsList, iaBoard);
		parseBoard (false, wordsList, iaBoard);
	}
	private void parseBoard (boolean bHoriz, List wordsList, int iaBoard[][]) {
		WordSlave wordSlave = null;
		boolean bFoundWord = false;
		int row, col, num;
		for (row=0; row<BOARD_SIZE; row++) {
			bFoundWord = false;
			for (col=0; col<BOARD_SIZE; col++) {
				if (bHoriz)				 // look for horiz words
					num = iaBoard[row][col];
				else					 // look for vertical words
					num = iaBoard[col][row];
				if (num > -1) {			// found a letter
					if (bFoundWord) {	// attach to current word
						wordSlave.addChar(num);
					} else {			// new word
						if (bHoriz)		// careful, row and col are switched!
							wordSlave = new WordSlave(true,row,col,num);
						else
							wordSlave = new WordSlave(false,col,row,num);	// here...
						bFoundWord = true;
					}
				}
				else {					 // not occupied
					if (bFoundWord && wordSlave.isValidWord()) 	// add word to list
						wordsList.add(wordSlave);
					bFoundWord = false;
				}
			}
		}
		if (bFoundWord && wordSlave.isValidWord()) 	// add word to list
			wordsList.add(wordSlave);
	}
//
//	Slave classes
//
	private class ScoreSlave {		// help to calculate the score
		private WordSlave m_wordSlave;
		private int m_newBoard[][];
		private List m_letterSlaves = new ArrayList();
		public ScoreSlave (WordSlave slave, int newBoard[][]) {
			m_wordSlave = slave;
			m_newBoard = newBoard;
			constructLetters();
		}
		public LetterSlave getLetterSlave(int i) {return (LetterSlave)m_letterSlaves.get(i);}
		public WordSlave getWordSlave() {return m_wordSlave;}
		public String getWord() {return m_wordSlave.getWord();}
		public int getWordLength() {return m_letterSlaves.size();}
		private void constructLetters() {
			int buflen = m_wordSlave.getLength();
			int bx = m_wordSlave.getX();
			int by = m_wordSlave.getY();
			int cx = bx;
			int cy = by;
			for (int i=0; i<buflen; i++) {
				if (m_wordSlave.isHoriz())
					cy = by + i;
				else
					cx = bx + i;
				m_letterSlaves.add(
					new LetterSlave(cx,cy,m_board[cx][cy],m_newBoard[cx][cy],
						m_MultiplierTypesByPosition[cx*BOARD_SIZE+cy]));
			}
		}
		public void traceScoreSlave (String msg) {
			Debug.println("traceScoreSlave; "+msg);
			Debug.println("WordSlave; "+m_wordSlave.toString());
			LetterSlave slave;
			for (int i=0; i<m_letterSlaves.size(); i++)
				Debug.println("LetterSlave; "+
							((LetterSlave)m_letterSlaves.get(i)).toString());
		}
	}
	private class WordSlave {		// model a word on the board
		private boolean m_bHorizontal;
		private int m_nX;
		private int m_nY;
		private StringBuffer m_buffer;
		public WordSlave (boolean bHoriz, int x, int y, int letter) {
			m_bHorizontal = bHoriz;
			m_nX = x;
			m_nY = y;
			m_buffer = new StringBuffer();
			addChar(letter);
		}
		public int getX() {return m_nX;}
		public int getY() {return m_nY;}
		public int getLength() {return m_buffer.length();}
		public StringBuffer getBuffer() {return m_buffer;}
		public boolean isHoriz() {return m_bHorizontal;}
		public boolean isValidWord() {
			if (getLength() < 2) return false;
			return true;
		}
		private void addChar (int letter) {m_buffer.append(getLetterChar(letter));}
		private String getWord() {return m_buffer.toString();}
		private char getLetterChar (int letter) {
			return (char) (letter + 96);}
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("bHoriz ").append(m_bHorizontal);
			buf.append(" X ").append(m_nX).append(",Y ").append(m_nY);
			buf.append(" Word :").append(getWord()).append(":");
			return buf.toString();
		}
	}
	private class LetterSlave {		// helps validate get it done!
		private int m_nX;
		private int m_nY;
		private int m_nOldLetter;
		private int m_nNewLetter;
		private int m_nScorer;
		private LetterSlave (int x, int y, int nOld, int nNew, int score) {
			m_nX = x;
			m_nY = y;
			m_nOldLetter = nOld;
			m_nNewLetter = nNew;
			m_nScorer = score;
		}
		public int getX() {return m_nX;}
		public int getY() {return m_nY;}
		public int getOldLetter() {return m_nOldLetter;}
		public int getNewLetter() {return m_nNewLetter;}
		public int getScorer() {return m_nScorer;}

		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("X ").append(m_nX).append(",Y ").append(m_nY);
			buf.append(" old ").append(m_nOldLetter);
			buf.append(" new ").append(m_nNewLetter);
			buf.append(" score ").append(m_nScorer);
			return buf.toString();
		}
	}
}

