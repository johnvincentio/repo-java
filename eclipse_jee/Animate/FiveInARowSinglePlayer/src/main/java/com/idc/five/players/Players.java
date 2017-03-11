package com.idc.five.players;

import java.awt.Color;

public class Players {
	public static final int PLAYER1 = 1;		// white
	public static final int PLAYER2 = 2;		// black

	public static boolean isValidPlayer (int player) {
		return player == Players.PLAYER1 || player == Players.PLAYER2;
	}
	public boolean isPlayerComputer (int player) {
		assert isValidPlayer (player);
		if (player == 1) return playerWhite.isComputer();
		return playerBlack.isComputer();
	}
	public String getPlayerName (int player) {
		assert isValidPlayer (player);
		if (player == 1) return playerWhite.getName();
		return playerBlack.getName();
	}
	public String getPlayerNameCharacter (int player) {
		return getPlayerName (player).substring(0, 1);
	}
	public Color getPlayerColor (int player) {
		assert isValidPlayer (player);
		if (player == 1) return playerWhite.getColor();
		return playerBlack.getColor();
	}

//	private boolean whiteCurrentPlayer = true;		// white always starts
	private Player playerWhite;
	private Player playerBlack;

	public static String[] types = {"Player", "Computer"};

	public Players() {
		playerWhite = new Player("White", Color.white, true);
		playerBlack = new Player("Black", Color.black, false);
	}

	public String[] getTypes() {return types;}
	public int getWhiteSelectedType() {return playerWhite.isComputer() ? 1 : 0;}
	public int getBlackSelectedType() {return playerBlack.isComputer() ? 1 : 0;}
	public void setWhiteSelectedType (String item) {
		boolean type = (item.equals("Computer")) ? true : false;
		playerWhite.setComputer (type);
	}
	public void setBlackSelectedType (String item) {
		boolean type = (item.equals("Computer")) ? true : false;
		playerBlack.setComputer (type);
	}

	public Player getPlayerWhite() {
		return playerWhite;
	}

	public Player getPlayerBlack() {
		return playerBlack;
	}

/*
	public boolean isWhiteCurrentPlayer() {
		return whiteCurrentPlayer;
	}
*/
	public void setPlayerWhite (boolean computer) {
		playerWhite.setComputer(computer);
	}

	public void setPlayerBlack (boolean computer) {
		playerBlack.setComputer(computer);
	}

	public static int whoIsOtherPlayer (int player) {return 3 - player;}

	@Override
	public String toString() {
		return "Players [playerWhite=" + playerWhite + ", playerBlack="	+ playerBlack + "]";
	}
}
