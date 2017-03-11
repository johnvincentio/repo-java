package com.idc.scrabble.client;

import com.idc.scrabble.utils.PacketReader;
import com.idc.scrabble.utils.PacketWriter;
import com.idc.scrabble.utils.Constants;
import com.idc.scrabble.utils.Debug;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class ScrabbleClient extends JFrame implements ActionListener {
	private String m_strPlayerName = "Napolean";	// start with something
	private int m_keyid = -1;
	private JTextArea m_messagesArea;  // messages for the user
	private JTextField m_nameField;	// Username
	private Square m_board[][];   // scrabble grid.
	private Square m_playerRack[];   // scrabble player rack.

	private Square pressedSquare = null;
	private Square enteredSquare = null;

	private ClientMessages m_clientMessages;
	private ClientPlayer m_clientPlayer;
	private ScrabbleImages m_images;
	private boolean m_bMyTurn = false;

	public ScrabbleClient(String msg) {
		super(msg);
		Debug.setFile("CONSOLE",false);
		Debug.println("ScrabbleClient()");
		m_clientMessages = new ClientMessages (this);
		String strURL = "127.0.0.1";
//		String strURL = getCodeBase().getHost(); // Applet only
		m_clientPlayer = new ClientPlayer (this, strURL); // open socket to the server
		m_images = new ScrabbleImages(this,Constants.BOARD_SIZE);	// load letters and tiles
		setContentPane(makeGameContentPane());	// create the scrabble screen
		this.addWindowListener(new WindowAdapter() {	// handle exit
			public void windowClosing(WindowEvent e) {
				doStopClient();		// exit gracefully
			}
		});
		setSize(300,200);
		pack();
		setVisible(true);

		m_clientPlayer.start(); // thread to read messages from the socket
	}
	public ScrabbleClientSocket getMySocket(){return m_clientPlayer.getSocket();}
	public void setMyTurn (boolean status) {m_bMyTurn = status;}
	public boolean getMyTurn() {return m_bMyTurn;}
	public void setKeyId(int key) {m_keyid = key;}
	public int getKeyId() {return m_keyid;}
	public String getPlayerName() {return m_strPlayerName;}
	public Square getBoard(int i, int j) {return m_board[i][j];}
	public Square getRack(int i) {return m_playerRack[i];}

	private void sayWhat(String evDs, MouseEvent e) {	// useful for mouse
		Debug.println(evDs + " detected on " + 
				e.getComponent().getClass().getName()+" .\n");
	}
	public static void main (String args[]) {	// not a lot!
		ScrabbleClient app = new ScrabbleClient ("Scrabble");
	}
//
// Make the scrabble pane
//
	private Container makeGameContentPane() {
		JPanel boardPanel1;  // panel for the squares
		JPanel boardPanel2;  // stick the grid panel to the content pane
		JPanel rackPanel1;  // panel for the tiles in the rack
		JPanel rackPanel2;  // stick the rack panel to the content pane
		MouseListener mouseLstnr;        // look for mouse clicks

		Debug.println(">>> ScrabbleClient::makeContentPane()");
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		boardPanel1 = new JPanel();
		boardPanel1.setLayout(new GridLayout(Constants.BOARD_SIZE, Constants.BOARD_SIZE, 0, 0));

		mouseLstnr= new MouseListener() {
			public void mousePressed(MouseEvent me) {handleMouseMove(1,me);}
			public void mouseReleased(MouseEvent me) {handleMouseMove(2,me);}
			public void mouseEntered(MouseEvent me) {handleMouseMove(3,me);}
			public void mouseClicked(MouseEvent me) {/*sayWhat("clicked",me);*/}
			public void mouseExited(MouseEvent me) {/*sayWhat("exited",me);*/}
		};
	   
		m_board = new Square[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
		for (int row = 0; row < Constants.BOARD_SIZE; row++) {	// add squares to board
			for (int col = 0; col < Constants.BOARD_SIZE; col++) {
				m_board[row][col] = new Square(m_images,false, row, col);
				m_board[row][col].addMouseListener(mouseLstnr);
				boardPanel1.add(m_board[row][col]);
			}
		}
		rackPanel1 = new JPanel();	// add squares to rack
		rackPanel1.setLayout(new GridLayout(1, Constants.RACK_SIZE, 0, 0));
		m_playerRack = new Square[Constants.RACK_SIZE];
		for (int col=0; col<Constants.RACK_SIZE; col++) {
			m_playerRack[col] = new Square(m_images,true, col, 0);
			m_playerRack[col].addMouseListener(mouseLstnr);
			rackPanel1.add(m_playerRack[col]);
		}

		boardPanel2 = new JPanel();	// panel for the board
		boardPanel2.add(boardPanel1, BorderLayout.CENTER);

		rackPanel2 = new JPanel();	// panel for the rack
		rackPanel2.add(rackPanel1, BorderLayout.CENTER);

		JPanel topPane = new JPanel();	// top Pane, Name field only
		topPane.setLayout(new BorderLayout());
		m_nameField = new JTextField(m_strPlayerName);
		m_nameField.setEditable(false);
		topPane.add(m_nameField,BorderLayout.NORTH);
		topPane.add(boardPanel2,BorderLayout.CENTER);
		topPane.add(rackPanel2,BorderLayout.SOUTH);

		JPanel low1Pane = new JPanel();	// low1 Pane, buttons only
		JButton btnMove = new JButton("Move");
		JButton btnClear = new JButton("Clear");
		JButton btnPass = new JButton("Pass");
		JButton btnExit = new JButton("Exit");
		btnMove.addActionListener(this);
		btnPass.addActionListener(this);
		btnClear.addActionListener(this);
		btnExit.addActionListener(this);
		low1Pane.add(btnMove);
		low1Pane.add(btnPass);
		low1Pane.add(btnClear);
		low1Pane.add(btnExit);

		JPanel low2Pane = new JPanel();	// panel for a messages area
		m_messagesArea = new JTextArea(5,25);
		m_messagesArea.setEditable(false);
		low2Pane.add(new JScrollPane(m_messagesArea));

		JPanel lowPane = new JPanel();	// messages and buttons
		lowPane.setLayout(new BorderLayout());
		lowPane.add(low2Pane,BorderLayout.NORTH);
		lowPane.add(low1Pane,BorderLayout.SOUTH);

		pane.add(topPane,BorderLayout.NORTH);	// put it together
		pane.add(lowPane,BorderLayout.SOUTH);
		Debug.println("<<< ScrabbleClient::makeContentPane()");
		return (pane);
	}
//
// methods to update gui components - Be thread-safe!
//
	public void setMessagesArea (final String msg) {
		Debug.println(">>> ScrabbleClient::setMessagesArea()");
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					m_messagesArea.append (msg);
					m_messagesArea.append ("\n");
					m_messagesArea.setCaretPosition ( 
						m_messagesArea.getText().length());
				}
			}
		);
		Debug.println("<<< ScrabbleClient::setMessagesArea()");
	}
	public void setSquareLetter (final Square sq, final int letter) {
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					sq.setLetterNumber(letter);
				}
			}
		);
	}
	public void setUsernameField (final String msg) {
		Debug.println(">>> ScrabbleClient::setUsernameField()");
		SwingUtilities.invokeLater (
			new Runnable() { 
				public void run() {
					m_nameField.setText(msg);
					m_nameField.setCaretPosition ( 
						m_nameField.getText().length());
				}
			}
		);
		Debug.println("<<< ScrabbleClient::setUsernameField()");
	}
//
// stop the application gracefully - there is also a server!
//
	public void doStopClient() {
		Debug.println(">>> doStopClient()");
		if (m_clientMessages() != null) {
			m_clientMessages.close();
		}
		Debug.println("<<< doStopClient()");
		System.exit(0);
	}
//
// Send various message types to the server.
// These methods may be invoked from the listening thread and from the main
// thread.
//
	public void sendStartMessage() {
		Debug.println(">>> sendStartMessage");
		PacketWriter packet = new PacketWriter(Constants.START);
		packet.append(-1);
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendStartMessage");
	}
	private void sendInitMessage() {
		Debug.println(">>> sendInitMessage");
		PacketWriter packet = new PacketWriter(Constants.INIT);
		packet.append(m_keyid);
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendInitMessage");
	}
	private void sendExitMessage() {
		Debug.println(">>> sendExitMessage");
		PacketWriter packet = new PacketWriter(Constants.EXIT);
		packet.append(m_keyid);
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendExitMessage");
	}
	private void sendNameMessage() {
		Debug.println(">>> sendNameMessage");
		PacketWriter packet = new PacketWriter(Constants.NAME);
		packet.append(-1);
		packet.append(m_strPlayerName);
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendNameMessage");
	}
	private void sendClearMessage() {
		Debug.println(">>> sendClearMessage");
		PacketWriter packet = new PacketWriter(Constants.CLEAR);
		packet.append(m_keyid);
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendClearMessage");
	}
	private void sendMoveMessage() {
		Debug.println(">>> sendMoveMessage");
		PacketWriter packet = new PacketWriter(Constants.MOVE);
		packet.append(m_keyid);
		for (int i=0; i<Constants.BOARD_SIZE; i++) {
			for (int j=0; j<Constants.BOARD_SIZE; j++)
				packet.append(m_board[i][j].getLetterNumber());
		}
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendMoveMessage");
	}
	private void sendPassMessage() {
		Debug.println(">>> sendPassMessage");
		PacketWriter packet = new PacketWriter(Constants.PASS);
		packet.append(m_keyid);
		for (int i=0; i<Constants.RACK_SIZE; i++)
			packet.append(m_playerRack[i].getLetterNumber());
		Debug.println("packet;"+packet.getString());
		getMySocket().sendScrabbleMessage(packet.getString());
		Debug.println("<<< sendPassMessage");
	}
//
// processMessage - will only be invoked from the socket listening thread.
// 		Handle the incoming message from the server.
// 			Be Thread-safe!!!!	
//
	public void processMessage(final String msg) {
		String strTmp = msg;
		Debug.println(">>> processMessage()"+strTmp);
		PacketReader packetReader = new PacketReader (strTmp);
		if (packetReader.length() < 1) {
			Debug.println("<<< processMessage(); nothing");
			return;
		}
		int packetType = packetReader.getNextInt();
		Debug.println("type "+packetType);
		int keyid = packetReader.getNextInt();
		Debug.println("keyid "+keyid);

		StringBuffer buffer = new StringBuffer();
		switch (packetType) {
			case Constants.KEY:
				setKey(keyid);
				buffer = new StringBuffer();
				buffer.append("Your Player ID is ");
				buffer.append(keyid);
				setMessagesArea (buffer.toString());
				sendNameMessage();
				sendInitMessage();
				break;
			case Constants.BOARD:
				int pos = 0;
				int row, col;
				while (packetReader.hasNext()) {
					row = pos / Constants.BOARD_SIZE;
					col = pos - row * Constants.BOARD_SIZE;
   					setSquareLetter (m_board[row][col], packetReader.getNextInt());
					pos++;
				}
				break;
			case Constants.SCORES:
				while (packetReader.hasNext(3)) {
					buffer = new StringBuffer();
					buffer.append(packetReader.getNext());
					buffer.append(",");
					buffer.append(packetReader.getNext());
					buffer.append(",");
					buffer.append(packetReader.getNext());
   					setMessagesArea (buffer.toString());
				}
				break;
			case Constants.TILES_LEFT:
				int iTiles = packetReader.getNextInt();
				buffer = new StringBuffer();
				buffer.append("Tiles Remaining=");
				buffer.append(iTiles);
				setMessagesArea (buffer.toString());
				break;
			case Constants.RACK:
				col = 0;
				while (packetReader.hasNext())
   					setSquareLetter (m_playerRack[col++], packetReader.getNextInt());
				break;
			case Constants.PLAYER:
				int keyCurrent = packetReader.getNextInt();
				Debug.println("keyid "+keyid+" currentPlayerKeyid "+keyCurrent);
				Debug.println("m_keyid "+m_keyid);

				buffer = new StringBuffer();
				buffer.append("Current Player ID is ");
				buffer.append(keyCurrent);
				buffer.append(". Your Player ID is ");
				buffer.append(keyid);
				setMessagesArea (buffer.toString());
				if (keyCurrent == m_keyid) {
					setMyTurn(true);
					JOptionPane.showMessageDialog(this,"Your Turn","Scrabble",
									JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					setMyTurn(false);
				}
				break;
			case Constants.BADMOVE:
					JOptionPane.showMessageDialog(this,
						packetReader.getNext(),"Scrabble",
						JOptionPane.ERROR_MESSAGE);
				break;
			default:
				break;
		}
		Debug.println("<<< processMessage()");
	}
	public void actionPerformed (ActionEvent e) {
		Debug.println(">>> ScrabbleClient::actionPerformed()");
		JButton jb = (JButton) e.getSource();
		String strBtn = jb.getText();
		Debug.println("action "+jb.getText());
		if (strBtn.equals("Move")) {
			sendMoveMessage();
		}
		else if (strBtn.equals("Pass")) {
			sendPassMessage();
		}
		else if (strBtn.equals("Clear")) {
			sendClearMessage();
		}
		else if (strBtn.equals("Exit")) {
			doStopClient();
		}
		Debug.println("<<< ScrabbleClient::actionPerformed()");
	}
	private void handleMouseMove (int moveType, MouseEvent me) {
		if (! getMyTurn()) {
			JOptionPane.showMessageDialog(this,
				"A little twitchy today, aren't you. Too much caffeine maybe.",
				"It is not your turn!",JOptionPane.ERROR_MESSAGE);
			return;
		}

		Square sq = ((Square) me.getComponent());
		switch (moveType) {
			case 1:			// mouse pressed event
				pressedSquare = null;
				if (sq.isRack() && (! sq.isEmpty())) {
					pressedSquare = sq;	// select this letter from the rack
				}
				break;
			case 2:			// mouse released event
				if ((pressedSquare != null) && (enteredSquare != null) &&
						(! enteredSquare.isRack()) && enteredSquare.isEmpty()) {
					setSquareLetter (enteredSquare,
							pressedSquare.getLetterNumber());	// move letter
					setSquareLetter (pressedSquare, -1);	// not now in rack
				}
				pressedSquare = null;
				break;
			case 3:			// mouse entered event
				enteredSquare = sq;
				break;
			default:			// not interested
				break;
		}
	}
	public void setSize (int width, int height) {
		Debug.println("setSize; w "+width+" h "+height);
		super.setSize (width, height);
		Debug.println("before validate");
		validate();
		Debug.println("before repaint");
		repaint();
	}
}

