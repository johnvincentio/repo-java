package com.idc.five;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.idc.five.game.Board;
import com.idc.five.game.Coordinate;
import com.idc.five.game.Game;
import com.idc.five.game.Moves;
import com.idc.five.game.SizingItemInfo;
import com.idc.five.output.Output;
import com.idc.five.output.OutputFile;
import com.idc.five.players.Players;
import com.idc.five.scoring.Scoring;
import com.idc.five.totaling.Totaling;
import com.idc.five.utils.JVFile;

//TODO; change player type without restarting game.

//TODO; show suggested move as an icon on the board.

//TODO; scoring priorities will change when it is no longer possible or realistic for either side to win

//TODO; on victory, show the winning combination(s)
//TODO; understand some formations that would be lethal.

public class App extends JFrame {
	private static final long serialVersionUID = -6288780408957182327L;

	public static final String APP_NAME = "Five in a Row";

	private Players m_players = new Players();
	private Board m_board = new Board();
	private Moves m_moves = null;

	private BoardGUI m_boardGUI = null;
	private Game m_game = null;

	private JTextField m_statusField;

	private FiveFilter m_fiveFilter = new FiveFilter();

	public static void main(String[] args) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					new App(APP_NAME);
				}
			}
		);
	}

	public App (String msg) {
		super (msg);
		initialize();
		updateStatus();
	}

	private void initialize () {
		System.out.println(">>> App::initialize");
		m_game = null;
		m_boardGUI = null;

		int rows = m_board.getRows();
		int columns = m_board.getColumns();

		SizingItemInfo sizingItemInfo = new SizingItemInfo (rows, columns);
		System.out.println("sizingItemInfo "+sizingItemInfo);
		setSize (sizingItemInfo.getCalculatedWindowWidth(), sizingItemInfo.getCalculatedWindowHeight());

		m_board = null;
		m_board = new Board (m_players, rows, columns);

		m_moves = null;
		m_moves = new Moves (m_players, m_board);

		m_game = new Game (m_players, m_board, m_moves);
		m_boardGUI = new BoardGUI (sizingItemInfo, m_players, m_board, m_moves);

		setContentPane (makeContentPane());
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				System.exit(0);
			}
		});
		setLocationRelativeTo (null);	// Center window.
		setResizable (false);
		setVisible (true);				// Make window visible
		System.out.println("<<< App::initialize");
	}

	private Container makeContentPane() {
		System.out.println(">>> App::makeContentPane");
		JPanel topPanel = new JPanel();
		topPanel.setLayout (new BorderLayout());

		JPanel gamePanel = new JPanel();
		gamePanel.setLayout (new FlowLayout());
		JButton newGameButton = new JButton ("New Game");
		newGameButton.addActionListener (new NewGameAction());
		gamePanel.add (newGameButton);
		JButton undoButton = new JButton ("Undo");
		undoButton.addActionListener (new UndoAction());
		gamePanel.add (undoButton);
		JButton suggestButton = new JButton ("Help");
		suggestButton.addActionListener (new SuggestAction());
		gamePanel.add (suggestButton);

		JButton importButton = new JButton ("Import");
		importButton.addActionListener (new ImportAction());
		gamePanel.add (importButton);
		JButton exportButton = new JButton ("Export");
		exportButton.addActionListener (new ExportAction());
		gamePanel.add (exportButton);
		JButton dumpButton = new JButton ("Dump");
		dumpButton.addActionListener (new DumpAction());
		gamePanel.add (dumpButton);

		JPanel configPanel = new JPanel();
		configPanel.setLayout (new FlowLayout());

		JComboBox<Integer> comboRows = new JComboBox<Integer>();
		for (int i = m_board.getMinRows(); i <= m_board.getMaxRows(); i++) {
			comboRows.addItem(i);
		}
		comboRows.setSelectedIndex(m_board.getCurrentRow());
		comboRows.addActionListener (new NewRowsAction());

		JComboBox<Integer> comboColumns = new JComboBox<Integer>();
		for (int i = m_board.getMinColumns(); i <= m_board.getMaxColumns(); i++) {
			comboColumns.addItem(i);
		}
		comboColumns.setSelectedIndex(m_board.getCurrentColumn());
		comboColumns.addActionListener (new NewColumnsAction());

		configPanel.add(new JLabel("Number of Rows"));
		configPanel.add(comboRows);
		configPanel.add(new JLabel("Number of Columns"));
		configPanel.add(comboColumns);

		JPanel playerPanel = new JPanel();
		playerPanel.setLayout (new FlowLayout());

		JComboBox<String> comboWhite = new JComboBox<String>(m_players.getTypes());
		comboWhite.setSelectedIndex (m_players.getWhiteSelectedType());
		comboWhite.addActionListener (new NewWhitePlayerAction());

		JComboBox<String> comboBlack = new JComboBox<String>(m_players.getTypes());
		comboBlack.setSelectedIndex (m_players.getBlackSelectedType());
		comboBlack.addActionListener (new NewBlackPlayerAction());

		playerPanel.add(new JLabel("White"));
		playerPanel.add(comboWhite);
		playerPanel.add(new JLabel("Black"));
		playerPanel.add(comboBlack);

		topPanel.add(gamePanel, BorderLayout.NORTH);
		topPanel.add(configPanel, BorderLayout.CENTER);
		topPanel.add(playerPanel, BorderLayout.SOUTH);

		JPanel pane = new JPanel();
		pane.setLayout (new BorderLayout());
		pane.add (topPanel, BorderLayout.NORTH);

		m_boardGUI.addMouseListener (new GameBoardMouseListener());
		m_boardGUI.addMouseMotionListener (new GameBoardMouseMotionListener());

		JPanel paneBoard = new JPanel();
		paneBoard.setLayout (new BorderLayout());
		paneBoard.add (m_boardGUI, BorderLayout.CENTER);

		pane.add (paneBoard, BorderLayout.CENTER);

		m_statusField = new JTextField();
/*
		m_statusField = new JTextField(70);
		Font f = m_statusField.getFont();
		System.out.println("f "+f.getFontName()+", "+f.getName()+", "+f.getSize()+", "+f.getFamily());
		System.out.println("f "+f.getPSName());
		Font displayFont = new Font("Dialog", Font.PLAIN, 10);
		m_statusField.setFont(displayFont);
*/
		pane.add (m_statusField, BorderLayout.SOUTH);
		System.out.println("<<< App::makeContentPane");
		return pane;
	}

	private void setStatusMessage (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_statusField.setText(msg);
					m_statusField.getCaret().setDot(0); 
					validate();
				}
			}
		);
	}

	private class NewGameAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			initialize();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class UndoAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			m_game.undo();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class SuggestAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			System.out.println(">>> App::SuggestAction::actionPerformed");
			if (m_game.isGameOver())
				setStatusMessage ("Game is Over");
			else {
				Coordinate coordinate = m_game.suggestMove();
				setStatusMessage ("Suggest ["+coordinate.getRow()+","+coordinate.getCol()+"]");
			}
			System.out.println("<<< App::SuggestAction::actionPerformed");
		}
	}

	private class ImportAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			JFileChooser fileLoad = new JFileChooser();
			fileLoad.setFileSelectionMode (JFileChooser.FILES_ONLY);
			fileLoad.setCurrentDirectory (new File (JVFile.getCwd()));
			fileLoad.setDialogTitle("Load Five in a Row from file");
			fileLoad.setFileFilter(m_fiveFilter);
			fileLoad.setAcceptAllFileFilterUsed(false);
			int retval = fileLoad.showOpenDialog (App.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File file = fileLoad.getSelectedFile();
				loadGame (file);
			}
			fileLoad = null;
			updateStatus();
			m_boardGUI.repaint();
		}
	}
	private class ExportAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			JFileChooser fileSave = new JFileChooser();
			fileSave.setDialogType (JFileChooser.SAVE_DIALOG);
			fileSave.setFileSelectionMode (JFileChooser.FILES_ONLY);
			fileSave.setCurrentDirectory (new File (JVFile.getCwd()));
			fileSave.setDialogTitle("Save Five in a Row to file");
			fileSave.setFileFilter(m_fiveFilter);
			fileSave.setAcceptAllFileFilterUsed(false);
			int retval = fileSave.showSaveDialog (App.this);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File file = fileSave.getSelectedFile();
				if (isValidSaveFile (file)) {
					saveGame (file);
					JOptionPane.showMessageDialog (App.this, "Five in a Row Saved to "+file.getPath(), "Five in a Row Saved",
							JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog (App.this, "Cannot save to file "+file.getPath(), 
							"File not saved" ,JOptionPane.ERROR_MESSAGE);
				}
			}
			fileSave = null;
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private boolean isValidSaveFile (File file) {
//		LogHelper.debug (">>> Appgui::isValidSaveFile; file "+file.getPath());
		if (file.exists()) return false;
		String ext = JVFile.getExtension(file.getPath());
		if (ext == null || ext.length() < 1) return false;
		if (file.getName().toLowerCase().endsWith("five")) return true;
		return false;
	}

	private class DumpAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			System.out.println(">>> App::DumpAction");

//			Output output = new OutputTTY();
			Output output = new OutputFile ("five-in-a-row-dump-file", ".txt", "/tmp");
			output.open();

			output.println("current player "+m_game.getCurrentPlayer());
			output.println(m_players.toString());

			m_board.showBoard("dump Board", output);
			m_moves.showMoves ("dump Moves", output);

			Scoring scoring = new Scoring (m_players, m_board, m_game.getCurrentPlayer());
			scoring.showCountsInfo ("dump CountsInfo", output);
			scoring.showScores ("dump Scores", output);

			Coordinate coordinate = scoring.getHighestPossibleScoringMove();
			output.println("highest possible scoring move for player "+m_game.getCurrentPlayer()+"; row "+coordinate.getRow()+" column "+coordinate.getCol());

			Totaling totaling = new Totaling (m_players, m_board);
			totaling.showTotalsAll ("dump TotalsAll", output);
			totaling.showScoresAll ("dump ScoresAll", output);
			totaling.showTotals ("dump Totals", output);
			totaling = null;

			output.close();		// close output when finished with the dump

			updateStatus();
			m_boardGUI.repaint();

			System.out.println("<<< App::DumpAction");
		}
	}

	private class NewWhitePlayerAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			m_players.setWhiteSelectedType ((String) combo.getSelectedItem());
			initialize();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class NewBlackPlayerAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> combo = (JComboBox<String>) e.getSource();
			m_players.setBlackSelectedType ((String) combo.getSelectedItem());
			initialize();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class NewRowsAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Integer> combo = (JComboBox<Integer>) e.getSource();
			m_board.setRows ((int) combo.getSelectedItem());
			initialize();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class NewColumnsAction implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<Integer> combo = (JComboBox<Integer>) e.getSource();
			m_board.setColumns ((int) combo.getSelectedItem());
			initialize();
			updateStatus();
			m_boardGUI.repaint();
		}
	}

	private class GameBoardMouseListener implements MouseListener {
		public void mousePressed (MouseEvent e) {
			if (m_game.isGameOver()) return;
//			System.out.println(">>> App::GameBoardMouseListener; Current player is "+m_players.getPlayerName (m_game.getCurrentPlayer()));
			if (m_players.isPlayerComputer (m_game.getCurrentPlayer()))
				m_game.makeAutoMove();
			else {
				Coordinate coordinate = m_boardGUI.getCoordinate (e.getX(), e.getY());	// null => out of bounds
//				System.out.println("trying to mark; coordinate "+coordinate);
				if (coordinate != null && m_board.isEmpty(coordinate)) m_game.move (m_game.getCurrentPlayer(), coordinate);	// Make a move.
			}
//			System.out.println("Move was "+m_game.getLastMove().getRow()+","+m_game.getLastMove().getCol());
			updateStatus();
			repaint();
//			System.out.println("<<< App::GameBoardMouseListener");
		}
		public void mouseClicked (MouseEvent e) {}
		public void mouseReleased (MouseEvent e) {}
		public void mouseEntered (MouseEvent e) {}
		public void mouseExited (MouseEvent e) {}
	}

	private class GameBoardMouseMotionListener implements MouseMotionListener {
		public void mouseDragged (MouseEvent e) {}
		public void mouseMoved (MouseEvent e) {
			if (m_game.isGameOver()) return;
//			System.out.println(">>> App::GameBoardMouseMotionListener::mouseMoved");

			Coordinate coordinate = null;
			if (m_players.isPlayerComputer (m_game.getCurrentPlayer())) {
				coordinate = m_game.suggestMove();
//				System.out.println("Coordinate is "+coordinate);
				m_boardGUI.setCurrentMouseCoordinate (coordinate);
			}
			else {
				m_boardGUI.setCurrentMousePosition (e.getX(), e.getY());
			}
			repaint();
//			System.out.println("<<< App::GameBoardMouseMotionListener::mouseMoved");
		}
	}

	private void updateStatus() {
//		System.out.println(">>> App::updateStatus");
		if (m_game.isVictory (Players.PLAYER1)) {
			setStatusMessage (m_players.getPlayerName (Players.PLAYER1) + " wins");
			return;
		}
		if (m_game.isVictory (Players.PLAYER2)) {
			setStatusMessage (m_players.getPlayerName (Players.PLAYER2) + " wins");
			return;
		}
/*
		if (m_game.isBadMove()) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}
*/

		Totaling totaling = new Totaling (m_players, m_board);
		StringBuffer scoring = totaling.getTotalsForScoring();
		totaling = null;

		if (m_game.isTie()) {
			setStatusMessage ("Tie; Scores " + scoring);
			return;
		}
		setStatusMessage (m_players.getPlayerName (m_game.getCurrentPlayer()) + " to play; "+scoring);
//		System.out.println("<<< App::updateStatus");
	}

	public class FiveFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			return f.getName().toLowerCase().endsWith("five");
		}

		public String getDescription() {
			return "Five in a Row Files (*.five)";
		}
	}

	public void saveGame (File file) {
		System.out.println(">>> SavedGame::save; file "+file.getPath());

		PrintWriter pw = null;
		try {
			pw = new PrintWriter (new BufferedWriter (new FileWriter(file)));
			pw.println("-- ");
			pw.println("-- players,isPlayerComputer(1),isPlayerComputer(2) --");
			pw.println("-- ");
			pw.println("players,"+m_players.isPlayerComputer(1)+","+m_players.isPlayerComputer(2));

			pw.println("");
			pw.println("-- ");
			pw.println("-- board,board.getRows(),board.getColumns() --");
			pw.println("-- ");
			pw.println("board,"+m_board.getRows() + "," + m_board.getColumns());

			pw.println("");
			pw.println("-- ");
			pw.println("-- initialize --");
			pw.println("-- ");
			pw.println("initialize");

			pw.println("");
			pw.println("-- ");
			pw.println("-- move,move_number,player,row,column --");
			pw.println("-- ");
			for (int num = 0; num < m_board.getRows() * m_board.getColumns(); num++) {
				Coordinate coord = m_moves.getMoveByMoveNumber (num);
				if (coord == null) break;
				int player = m_board.getPlayerAt (coord.getRow(), coord.getCol());
				pw.println ("move," + num + "," + player + "," + coord.getRow() + "," + coord.getCol());
			}

			pw.println("");
			pw.println("-- ");
			pw.println("-- end --");
			pw.println("-- ");

			pw.flush();
			pw.close();
		}
		catch (IOException ex) {
			System.out.println("Unable to save to file "+file.getPath()+"; error "+ex.getMessage());
		}
		System.out.println("<<< SavedGame::save; file "+file.getPath());
	}

	public void loadGame(File file) {
		System.out.println(">>> SavedGame::load; file "+file.getPath());

		BufferedReader in = null;
		String inputLine;
		try {
			in = new BufferedReader (new FileReader(file));
			while (true) {
				inputLine = in.readLine();
				if (inputLine == null) break;
				if (inputLine.length() < 1) continue;
				if (inputLine.substring(0, 2).equals("--")) continue;
//				System.out.println("inputLine :"+inputLine);

				String[] parts = inputLine.split(",");

				if (parts[0].equalsIgnoreCase("players")) {
					m_players.setPlayerWhite(parts[1].equalsIgnoreCase("true"));
					m_players.setPlayerBlack(parts[2].equalsIgnoreCase("true"));
					continue;
				}
				if (parts[0].equalsIgnoreCase("board")) {
					m_board.setRows(Integer.parseInt(parts[1]));
					m_board.setColumns(Integer.parseInt(parts[2]));
					continue;
				}
				if (parts[0].equalsIgnoreCase("initialize")) {
					initialize();
				}
				if (parts[0].equalsIgnoreCase("move")) {
//					int num = Integer.parseInt(parts[1]);
					int player = Integer.parseInt(parts[2]);
					int row = Integer.parseInt(parts[3]);
					int column = Integer.parseInt(parts[4]);
					m_game.move (player, row, column);
				}
			}
		}
		catch (IOException ex) {
			System.out.println("Unable to load file "+file.getPath()+"; error "+ex.getMessage());			
		}
		System.out.println("<<< SavedGame::load; file "+file.getPath());
	}
}
