package com.idc.knight;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.idc.knight.xml.Scenario;
import com.idc.knight.xml.Solution;
import com.idc.knight.xml.XStreamHelper;

public class Game {

	private static Logger logger = Logger.getRootLogger();

	private static String m_solutions_directory = "/Users/jv/tmp/knight";
	private String getSolutionsDirectory() {return m_solutions_directory;}

/*
Run one starting position and one board size:
		startScenario (new Pair (4, 5), new Pair (0, 0), 100);
*/
	public static void main (String... args) {
		if (args.length < 6) {
			System.out.println("Usage: game x-size y-size x-pos y-pos max-solutions solutions-directory");
			System.exit(1);
		}
		for (String arg : args) {
			System.out.println("argument :"+arg+":");
		}
		Game game = new Game();
		game.doUserRequest(args);
	}

/*
//	game.doPlayGame_1();		
//	game.doPlayGame_2();		//TODO; uncomment this.....
//	game.doPlayGame_1a();
	game.doPlayGame_2a();
*/
	private void doUserRequest(String... args) {
		try {
			m_solutions_directory = args[5];
			startScenario (new Pair (Integer.parseInt(args[0]), Integer.parseInt(args[1])), 
					new Pair (Integer.parseInt(args[2]), Integer.parseInt(args[3])), Integer.parseInt(args[4]));
		}
		catch (Exception ex) {
			System.err.println ("Game::doPlayGame Exception; "+ex.getMessage());
		}
	}
/*
 * end of test
 */

	@SuppressWarnings("unused")
	private void doPlayGame_1() {
		doCaseByX (3, 3, 8);		// 3, [3-8]
		doCaseByX (4, 3, 8);		// 4, [3-8]
		doCaseByX (5, 3, 8);		// 5, [3-8]
		doCaseByX (6, 3, 8);		// 6, [3-8]

		doCaseByY (3, 3, 8);		// [3-8], 3
		doCaseByY (4, 3, 8);		// [3-8], 4
		doCaseByY (5, 3, 8);		// [3-8], 5
		doCaseByY (6, 3, 8);		// [3-8], 6
	}

/*
	TODO;
	7-7
	7-8
	8-7
	8-8
*/
	@SuppressWarnings("unused")
	private void doPlayGame_2() {
		doCaseByX (7, 7, 8);		// 7, [7-8]
		doCaseByX (8, 7, 8);		// 8, [7-8]
	}

	@SuppressWarnings("unused")
	private void doPlayGame_1a() {
		doCaseByX (3, 3, 8);		// 3, [3-8]
	}

	@SuppressWarnings("unused")
	private void doCaseByX (int max_x) {
		doCaseByX (max_x, 3, 8, 100);
	}
	private void doCaseByX (int max_x, int min_y, int max_y) {
		doCaseByX (max_x, min_y, max_y, 100);
	}
	private void doCaseByX (int max_x, int min_y, int max_y, int max_solutions) {
		try {
			for (int board_y = min_y; board_y <= max_y; board_y++) {
				Pair size = new Pair (max_x, board_y);
				for (int x = 0; x < max_x; x++) {
					for (int y = 0; y < board_y; y++) startScenario (size, new Pair (x, y), max_solutions);
				}
			}
		}
		catch (Exception ex) {
			System.err.println ("Game::doPlayGame Exception; "+ex.getMessage());
		}
	}

	@SuppressWarnings("unused")
	private void doCaseByY (int max_y) {
		doCaseByY (max_y, 3, 8, 100);
	}
	private void doCaseByY (int max_y, int min_x, int max_x) {
		doCaseByY (max_y, min_x, max_x, 100);
	}
	private void doCaseByY (int max_y, int min_x, int max_x, int max_solutions) {
		try {
			for (int board_x = min_x; board_x <= max_x; board_x++) {
				Pair size = new Pair (board_x, max_y);
				for (int x = 0; x < board_x; x++) {
					for (int y = 0; y < max_y; y++) startScenario (size, new Pair (x, y), max_solutions);
				}
			}
		}
		catch (Exception ex) {
			System.err.println ("Game::doPlayGame Exception; "+ex.getMessage());
		}
	}

	private void reportStartScenario (Scenario scenario) {
		logger.info (">>> Start Scenario; size "+scenario.getSize()+" start "+scenario.getStart());
		File file = JVFile.createSolutionsSubDirectory (getSolutionsDirectory(), scenario.getSize().dirname(), scenario.getStart().dirname());
		logger.info ("Creating directory "+file.getPath());
		JVFile.makeFullDirectories (file);
	}
	private void reportEndScenario (Scenario scenario) {
//		board.printBoard ("End of game");
		logger.info ("<<< End of Scenario; Total solutions "+scenario.getSolutions()+" Total moves moved "+scenario.getTotalMoves());
		File file = JVFile.createSolutionsSubDirectory (getSolutionsDirectory(), scenario.getSize().dirname(), scenario.getStart().dirname(), "report.xml");
		String xml = XStreamHelper.classToXml (scenario);
		JVFile.writeFile (xml, file);
	}
	private void reportSolutionFound (Solution solution) {
//		board.printBoard ("Found a solution# "+totalSolutions+" at moves moved "+currentSolutionTotalMoves+" total moves moved "+totalMoves+" seconds taken "+timing);
		logger.info ("Found a solution# "+solution.getSolution()+" at moves moved "+solution.getCurrentSolutionTotalMoves()+" total moves moved "+solution.getTotalMoves()+" seconds taken "+solution.getTiming());
		String xml = XStreamHelper.classToXml (solution);
		File file = JVFile.createSolutionsSubDirectory (getSolutionsDirectory(), solution.getSize().dirname(), solution.getStart().dirname(), solution.getStringSolution() + ".xml");
//		System.out.println("file :"+file.getPath());
		JVFile.writeFile (xml, file);
	}

	private void startScenario (Pair size, Pair start, long MaxSolutions) throws Exception {
		long totalSolutions = 0;				// count of solutions found
		long totalMoves = 0;					// total number of knight moves
		long currentSolutionTotalMoves = 0;		// total number of knight moves for the current solution

		long currentTimer = (new Date()).getTime();		// start timer
//		System.out.println("currentTimer "+currentTimer);

		Scenario scenario = new Scenario (size, start);
		reportStartScenario (scenario);		// report start of a new scenario

		Board board = new Board();				// create the board
		board.setBoard (size);					// set board size

		int moveCounter = 0;					// initialize the move counter; notice the move counter is zero based.

		Pair fromPair = new Pair (start);				// set From = start position
		Pair tempPair = new Pair();						// used as temporary storage
		board.addFirstMove (moveCounter, fromPair);		// add the initial position

		while (true) {
			if (MaxSolutions > 0 && totalSolutions >= MaxSolutions) break;
			currentSolutionTotalMoves++;

			moveCounter = board.getMoveCounter (fromPair);
			if (! board.isPossible (fromPair, moveCounter, tempPair)) {
//				logger.debug ("Board is not possible; from "+from+" moveCounter "+moveCounter);
//				board.printBoard ("before moveBackward");
				board.moveBackward (fromPair);
//				board.printBoard ("after moveBackward; from "+from+" moveCounter "+moveCounter);
			}

			int moveAwayType = board.getNextValidMove (fromPair, tempPair);
			if (moveAwayType > 0) {
				board.moveForward (fromPair, moveAwayType, tempPair);
				fromPair.set (tempPair);
				moveCounter = board.getMoveCounter (fromPair);
				if (board.isMovesComplete (moveCounter)) {			// found a solution
					totalSolutions++;
					totalMoves += currentSolutionTotalMoves;
					long timing = (new Date()).getTime() - currentTimer;
					reportSolutionFound (new Solution (totalSolutions, size, start, board, currentSolutionTotalMoves, totalMoves, timing));

					currentTimer = (new Date()).getTime();		// re-start the timer
					currentSolutionTotalMoves = 0;				// total number of knight moves for the current solution
				}
			}
			else {
//				logger.debug ("unable to find a moveAwayType; from "+from+" moveCounter "+moveCounter);
//				board.printBoard ("before moveBackward; from "+from+" moveCounter "+moveCounter);
				if (! board.isAnyMoreMoves (fromPair)) {
					logger.info ("There are no more possible moves");
					break;
				}
				board.moveBackward (fromPair);
//				board.printBoard ("after moveBackward; from "+from+" moveCounter "+moveCounter);
			}
		}

		totalMoves += currentSolutionTotalMoves;
		scenario.setSolutions (totalSolutions);
		scenario.setTotalMoves (totalMoves);
		scenario.setEndTime();
		reportEndScenario (scenario);				// report the end of a scenario
	}
}
