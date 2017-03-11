package com.idc.minesweeper;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//TODO; color the adjacent square numbers
//TODO; support LC on already visible square
//TODO; show blast mine when selected
//TODO; keep mines left score
//TODO; show all mines when mine has been blasted
//TODO; draw flag on top of raised tile
//TODO; on show all mines, use not a mine gif for flagged incorrectly.

public class App extends JFrame {
	private static final long serialVersionUID = 3511316899178701892L;

	private Board m_board;

	private JTextField m_textMines;	// remaining mines display
	private JTextField m_textTimer;	// timer display

	public static void main (String args[]) {	// not a lot!
		SwingUtilities.invokeLater (
			new Runnable() {
				public void run() {
					new App("Minesweeper");
				}
			}
		);
	}

	public App(String msg) {
		super(msg);
		initialize();
	}

	private void initialize() {
		m_board = null;
		m_board = new Board (this);

		setContentPane (makeGameContentPane());	// create the screen
		this.addWindowListener(new WindowAdapter() {	// handle exit
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setSize(300,200);
		pack();
		setResizable (false);
		setVisible(true);
	}

//
// Make the pane
//
	private Container makeGameContentPane() {
		System.out.println(">>> App::makeContentPane()");
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel topPane = new JPanel();	// top Pane
		m_textMines = new JTextField(3);
		m_textMines.setEditable(false);
		m_textTimer = new JTextField(5);
		m_textTimer.setEditable(false);
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new StartActionListener());
		topPane.add(new JLabel("Mines Left:"));
		topPane.add(m_textMines);
		topPane.add(btnStart);
//		topPane.add(new JLabel("Time:"));
//		topPane.add(m_textTimer);

		JPanel lowPane = new JPanel();	// minefield
		lowPane.setLayout(new BorderLayout());
		lowPane.add(m_board,BorderLayout.CENTER);

		pane.add(topPane,BorderLayout.NORTH);	// put it together
		pane.add(lowPane,BorderLayout.CENTER);
		System.out.println("<<< App::makeContentPane()");
		return (pane);
	}

//
// methods to update gui components.
//
	public void setTimerField (final String msg) {
		System.out.println(">>> App::setTimerField()");
		m_textTimer.setText(msg);
		System.out.println("<<< App::setTimerField()");
	}
	public void setMinesField (final String msg) {
		System.out.println(">>> App::setMinesField()");
		m_textMines.setText(msg);
		System.out.println("<<< App::setMinesField()");
	}

	public void setSize (int width, int height) {
		System.out.println("setSize; w "+width+" h "+height);
		super.setSize (width, height);
		System.out.println("before validate");
		validate();
		System.out.println("before repaint");
		repaint();
	}

	public class StartActionListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			System.out.println(">>> App::StartActionListener");
			JButton jb = (JButton) e.getSource();
			String strBtn = jb.getText();
			System.out.println("action "+jb.getText());
			if (strBtn.equals("Start")) {
				System.out.println("restart");
				initialize();
			}
			System.out.println("<<< App::StartActionListener");
		}
	}
}
