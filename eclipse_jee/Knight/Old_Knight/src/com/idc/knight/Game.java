
package com.idc.knight;

public class Game {
	static final int XLIMIT = 8;
	static final int YLIMIT = 8;
	static final int XYLIMIT = XLIMIT * YLIMIT;
	static final int[] Xinc = {1,2,2,1,-1,-2,-2,-1};
	static final int[] Yinc = {2,1,-1,-2,-2,-1,1,2};
	static final int MAXMOVETYPES = 8;

	public Game() {Debug.setFile ("jv.txt",false);}
	public void start (int x, int y) {
		int board[][] = new int[XLIMIT][YLIMIT];
		Debug.timing("Starting ("+x+","+y+")");
		nextMove (1,x,y,board);
		Debug.timing("Finished ("+x+","+y+")");
		Debug.flush();
	}
	public void nextMove (int move, int x, int y, int board[][]) {
		board[x][y] = move;
		if (move >= XYLIMIT)
			printBoard (board);
		int newX, newY;
		for (int i = 0; i < MAXMOVETYPES; i++) {
			newX = x + Xinc[i]; newY = y + Yinc[i];
			if (newX < 0 || newX >= XLIMIT) continue;
			if (newY < 0 || newY >= YLIMIT) continue;
			if (board[newX][newY] < 1)
				nextMove (move+1, newX, newY, board);
		}
		board[x][y] = 0;
	}
	public void printBoard (int board[][]) {
		StringBuffer sb = new StringBuffer();
		for (int y = YLIMIT-1; y >= 0; y--) {
			for (int x = 0; x < XLIMIT; x++) {
				sb.append(board[x][y]); sb.append(',');
		}	}
		Debug.println(sb.toString());
	}
	public static void main (String args[]) {
		Game game = new Game();
		if (args[0].toLowerCase().equals("all")) {
			System.out.println("allMain");
			for (int x = 0; x < XLIMIT; x++) {
				for (int y = 0; y < YLIMIT; y++) game.start(x,y);
			}
			Debug.timing ("There are no more possible solutions. Exiting...");
			Debug.flush();
		}
		else {
			System.out.println("oneMain");
			int x = (new Integer(args[0]).intValue());
			int y = (new Integer(args[1]).intValue());
			game.start(x,y);
		}
	}
}

