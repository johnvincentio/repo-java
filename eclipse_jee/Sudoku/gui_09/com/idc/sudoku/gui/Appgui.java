
package com.idc.sudoku.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.idc.file.JVFile;
import com.idc.trace.LogHelper;

public class Appgui extends JFrame {
	private static final long serialVersionUID = 9042845695212354647L;

	private JLabel m_txtStatus;
	private JProgressBar m_progress;

	private JButton m_btnHelp;
	private JButton m_btnImport;
	private JButton m_btnExport;
	private JButton m_btnStart;
	private JButton m_btnUserMove;
	private JButton m_btnComputer;
	private JButton m_btnMaxMoves;
	private JButton m_btnBrute;
	private JButton m_btnExit;

	private SudokuFilter m_sudokuFilter = new SudokuFilter();

	private Boardgui m_boardgui;

	public static void main (String... args) {
		LogHelper.info ("args len "+args.length);
		final String loadFile = args.length < 1 ? null : args[0];
		for (String s : args) {
			LogHelper.info ("arg "+s);
		}
		LogHelper.info ("loadFile :"+loadFile+":");
		
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					(new Appgui()).doApp(loadFile);
				}
			}
		);
	}

	private void doApp (String loadFile) {
		LogHelper.info (">>> Appgui::doApp; loadFile :"+loadFile+":");
		m_boardgui = new Boardgui(this);
		setContentPane (makeContentPane(loadFile));
		this.addWindowListener (new WindowAdapter() {
			public void windowClosing (WindowEvent e) {
				doStop();
			}
		});
		setSize(700,900);
		pack();
		setVisible(true);	
		LogHelper.info ("<<< Appgui::doApp");
	}

	public void doStop() {System.exit(0);}

	private JPanel getBoardPanel (int xlocal, int ylocal) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(Boardgui.getMaxLocal(), Boardgui.getMaxLocal(), 0, 0));
		for (int row = Boardgui.getMinFromLocal(xlocal); row <= Boardgui.getMaxFromLocal(xlocal); row++) {
			for (int col = Boardgui.getMinFromLocal(ylocal); col <= Boardgui.getMaxFromLocal(ylocal); col++) {
				panel.add(m_boardgui.getSquare(row,col));	// add squares to local squares
			}
		}
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.setBackground(new Color(250,150,50));
		return panel;
	}

	private Container makeContentPane (String loadFile) {
		LogHelper.info (">>> Appgui::makeContentPane");
		JPanel boardPanel = new JPanel();  // panel for the squares
		boardPanel.setLayout(new GridLayout(Boardgui.getMaxLocal(), Boardgui.getMaxLocal(), 0, 0));
		for (int row = Boardgui.getMinLocal(); row <= Boardgui.getMaxLocal(); row++) {
			for (int col = Boardgui.getMinLocal(); col <= Boardgui.getMaxLocal(); col++) {
				boardPanel.add(getBoardPanel(row,col));	// add local squares to board
			}
		}

		JPanel topPane = new JPanel(); // panel for the board
		topPane.setLayout(new BorderLayout());
		topPane.add(boardPanel, BorderLayout.CENTER);

/*
this works; Cntrl + shift + space 
m_btnHelp = createButton("Help", "Helpful tips for the game", KeyEvent.VK_H, 
	KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK), "Help Button");

pass keystroke
m_btnHelp = createButton("Help", "Helpful tips for the game", KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "Help Button");

F5
m_btnHelp = createButton("Help", "Helpful tips for the game", KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "Help Button");

Ctrl L
m_btnHelp = createButton("Help", "Helpful tips for the game", KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK), "Help Button");
*/

		m_btnHelp = createButton (1, "Help", "Helpful tips for the game", KeyEvent.VK_H, "Help Button");
		m_btnImport = createButton (2, "Import", "Import saved game", KeyEvent.VK_I, "Import Button");
		m_btnExport = createButton (3, "Export", "Export game to file", KeyEvent.VK_E, "Export Button");
		m_btnStart = createButton (4, "Start", "Enter starting position and press Start", KeyEvent.VK_S, "Start Button");
		m_btnUserMove = createButton (5, "User Move", "Make your move and press User Move", KeyEvent.VK_U, "User Button");
		m_btnComputer = createButton (6, "Computer Move", "Ask computer to make the next move", KeyEvent.VK_C, "Computer Button");
		m_btnMaxMoves = createButton (7, "Maximum Computer Moves", "Ask computer to make all possible moves", KeyEvent.VK_M, "Maximum Button");
		m_btnBrute = createButton (8, "Brute Force", "Ask computer to solve the puzzle using brute force method", KeyEvent.VK_B, "Brute Button");
		m_btnExit = createButton (9, "Exit", "Exit from the game", KeyEvent.VK_X, "Exit Button");

		JPanel midPane = new JPanel();
		midPane.add(m_btnHelp);
		midPane.add(m_btnImport);
		midPane.add(m_btnExport);
		midPane.add(m_btnStart);
		midPane.add(m_btnUserMove);
		midPane.add(m_btnComputer);	
		midPane.add(m_btnMaxMoves);
		midPane.add(m_btnBrute);
		midPane.add(m_btnExit);	

		JPanel lowPane = new JPanel();
		m_txtStatus = new JLabel();
		m_progress = new JProgressBar();
		lowPane.add (m_txtStatus);
		lowPane.add(m_progress);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(topPane,BorderLayout.NORTH);
		pane.add(midPane,BorderLayout.CENTER);
		pane.add(lowPane,BorderLayout.SOUTH);

		setInitButtons();

		if (loadFile != null && loadFile.length() > 0) {
			m_boardgui.loadBoard(new File(loadFile));
			m_boardgui.doStart();
			setStartedButtons();
		}
		LogHelper.info ("<<< Appgui::makeContentPane");
		return pane;
	}

	private JButton createButton (int action, String title, String tooltip, int mnemonic, String mapKey) {
		return createButton (action, title, tooltip, mnemonic, null, mapKey);
	}

	private JButton createButton (int action, String title, String tooltip, int mnemonic, KeyStroke keyStroke, String mapKey) {
		Action buttonAction = new ButtonAction (action, title, tooltip, new Integer(mnemonic));
		JButton button = new JButton();
		button.setAction(buttonAction);

		ActionMap actionMap = getRootPane().getActionMap();
		actionMap.put(mapKey, buttonAction);

		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		if (keyStroke == null)
			inputMap.put(KeyStroke.getKeyStroke(mnemonic, 0), mapKey);
		else
			inputMap.put(keyStroke, mapKey);
		return button;
	}

	class ButtonAction extends AbstractAction {
		private static final long serialVersionUID = -6609651353766180139L;

		private int m_action;
		private String m_text;
		public ButtonAction (int action, String text, String shortDescription, Integer mnemonic) {
			super(text);
			m_action = action;
			m_text = text;
			putValue(SHORT_DESCRIPTION, shortDescription);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed (ActionEvent ae) {
			LogHelper.debug("--- ButtonAction::actionPerformed; action_number "+m_action+" name :"+m_text+": Action [" + ae.getActionCommand() + "] performed!");
			int retval;
			switch (m_action) {
				case 1:		// help
					StringBuffer buf = new StringBuffer();
					buf.append("To start the game, either: \n");
					buf.append("1. enter starting number in each square that has one and press Start (s) \n");
					buf.append("or \n").append("2. Import a game (.sudoku extension) \n\n");
					buf.append("Once the game has started: \n\n").append("Place a number for a square and press User Move (u) \n");
					buf.append("To request the computer makes a move, press Computer Move (c) \n");
					buf.append("To request the computer makes as many moves as are possible, press Maximum Computer Moves (m) \n");
					buf.append("To request the computer solves the puzzle using brute force methodology, press Brute Force (b) \n");
					buf.append("\nTo exit the game, press Exit (x) \n");
					JOptionPane.showMessageDialog (Appgui.this, buf, "Help" ,JOptionPane.INFORMATION_MESSAGE);
					break;
				case 2:		// import
					LogHelper.info ("start import");
					JFileChooser fileLoad = new JFileChooser();
					fileLoad.setFileSelectionMode (JFileChooser.FILES_ONLY);
					fileLoad.setCurrentDirectory (new File (JVFile.getCwd()));
					fileLoad.setDialogTitle("Load Sudoku from file");
					fileLoad.setFileFilter(m_sudokuFilter);
//					fileLoad.addChoosableFileFilter(new SudokuFilter());
					fileLoad.setAcceptAllFileFilterUsed(false);
					retval = fileLoad.showOpenDialog (Appgui.this);
					if (retval == JFileChooser.APPROVE_OPTION) {
						File file = fileLoad.getSelectedFile();
						m_boardgui.loadBoard(file);
						m_boardgui.doStart();
						setStartedButtons();
					}
					fileLoad = null;
					LogHelper.info ("end import");
					break;
				case 3:		// export
					LogHelper.info ("Start export");
					JFileChooser fileSave = new JFileChooser();
					fileSave.setDialogType (JFileChooser.SAVE_DIALOG);
					fileSave.setFileSelectionMode (JFileChooser.FILES_ONLY);
					fileSave.setCurrentDirectory (new File (JVFile.getCwd()));
					fileSave.setDialogTitle("Save Sudoku to file");
					fileSave.setFileFilter(m_sudokuFilter);
//					fileSave.addChoosableFileFilter(new SudokuFilter());
					fileSave.setAcceptAllFileFilterUsed(false);
					retval = fileSave.showSaveDialog (Appgui.this);
					if (retval == JFileChooser.APPROVE_OPTION) {
						File file = fileSave.getSelectedFile();
						if (isValidSaveFile (file)) {
							m_boardgui.saveBoard(file);
							JOptionPane.showMessageDialog (Appgui.this, "Sudoku Saved to "+file.getPath(), "Sudoku Saved",
									JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog (Appgui.this, "Cannot save to file "+file.getPath(), 
									"File not saved" ,JOptionPane.ERROR_MESSAGE);
						}
					}
					fileSave = null;
					LogHelper.info ("end export");
					break;
				case 4:		// start
					m_boardgui.doStart();
					setStartedButtons();
					break;
				case 5:		// user move
					m_boardgui.doMove();
					break;
				case 6:		// computer move
					m_boardgui.doComputer();
					break;
				case 7:		// Maximum Computer Moves
					m_boardgui.doMaxMoves();
					break;
				case 8:		// Brute Force
					m_boardgui.doBrute();
					break;
				case 9:		// Exit
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog (Appgui.this, "Really want to exit?", "Exit", dialogButton);
					if (dialogResult == 0) doStop();
					break;
				default:
					break;
			}
		}
	}

	private boolean isValidSaveFile (File file) {
		LogHelper.debug (">>> Appgui::isValidSaveFile; file "+file.getPath());
		if (file.exists()) return false;
		String ext = JVFile.getExtension(file.getPath());
		if (ext == null || ext.length() < 1) return false;
		if (file.getName().toLowerCase().endsWith("sudoku")) return true;
		return false;
	}

	private void setInitButtons() {
		m_btnImport.setEnabled(true);
		m_btnExport.setEnabled(false);
		m_btnStart.setEnabled(true);
		m_btnUserMove.setEnabled(false);
		m_btnComputer.setEnabled(false);
		m_btnMaxMoves.setEnabled(false);
		m_btnBrute.setEnabled(false);
	}

	private void setStartedButtons() {
		m_btnImport.setEnabled(true);
		m_btnExport.setEnabled(true);
		m_btnStart.setEnabled(false);
		m_btnUserMove.setEnabled(true);
		m_btnComputer.setEnabled(true);
		m_btnMaxMoves.setEnabled(true);
		m_btnBrute.setEnabled(true);
	}

	public void makeStatusMessage (int row, int col, int value) {
		setStatusMessage ("("+row+","+col+") set to "+value);
	}

	public void setStatusMessage (final String msg) {
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					m_txtStatus.setText(msg);
					validate();
				}
			}
		);		
	}
}
