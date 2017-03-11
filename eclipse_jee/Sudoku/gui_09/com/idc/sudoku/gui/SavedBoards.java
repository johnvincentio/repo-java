package com.idc.sudoku.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.idc.trace.LogHelper;

public class SavedBoards {
	public SavedBoards() {}

	public static void save (File file, Square[][] squares) {
		LogHelper.info (">>> SavedBoards::save; file "+file.getPath());
		Square sq;
		PrintWriter pw = null;
		try {
			pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
			for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {		// redraw the board
				for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
					sq = squares[row][col];
					if (sq.isOccupied())
						pw.print(sq.getIntSquareValue());
					else
						pw.print("0");
				}
				pw.println();
			}
			pw.flush();
			pw.close();
		}
		catch (IOException ex) {
			LogHelper.error ("Unable to save to file "+file.getPath()+"; error "+ex.getMessage());
		}
		LogHelper.info ("<<< SavedBoards::save; file "+file.getPath());
	}

	public static void load(File file, Square[][] squares) {
		LogHelper.info (">>> SavedBoards::load; file "+file.getPath());
		for (int row = Boardgui.getMinSquare(); row <= Boardgui.getMaxSquare(); row++) {
			for (int col = Boardgui.getMinSquare(); col <= Boardgui.getMaxSquare(); col++) {
				squares[row][col].setIntSquareUser(0);
			}
		}

		BufferedReader in = null;
		String inputLine;
		int row = 0;
		int value;
		try {
			in = new BufferedReader (new FileReader(file));
			while (true) {
				inputLine = in.readLine();
				if (inputLine == null) break;
				if (inputLine.length() < 1) continue;
				if (inputLine.substring(0, 2).equals("--")) continue;
				for (int col = 0; col < inputLine.length(); col++) {
					value = Integer.parseInt(inputLine.substring(col,col+1));
//					LogHelper.info ("row "+row+" col "+col+" value "+value);
					squares[row+1][col+1].setIntSquareUser(value);
				}
				row++;
				if (row >= Boardgui.MAX_SQUARE) break;
			}
		}
		catch (IOException ex) {
			LogHelper.error ("Unable to load file "+file.getPath()+"; error "+ex.getMessage());			
		}
		LogHelper.info ("<<< SavedBoards::load; file "+file.getPath());
	}
}
