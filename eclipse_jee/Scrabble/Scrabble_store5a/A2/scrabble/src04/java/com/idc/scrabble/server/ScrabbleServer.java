package com.idc.scrabble.server;

import com.idc.scrabble.utils.Debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.ServerSocket;
import java.io.IOException;

public class ScrabbleServer extends JFrame {
	private	ScrabbleServer m_app;
	private Game m_game;
	private ServerSocket m_server;
	private JTextArea m_outputArea;
	private static final int MAXPLAYERS=6;		// Scrabble max is 6

	public ScrabbleServer() {
		super( "Scrabble Server" );
		Debug.setFile("CONSOLE",false);
		Debug.println("in ScrabbleServer");
		try {			 // set up ServerSocket
			m_server = new ServerSocket(12345,MAXPLAYERS); }
		catch (IOException ioException) {
			ioException.printStackTrace();
			System.exit(1);
		}
		m_outputArea = new JTextArea();
		getContentPane().add(m_outputArea, BorderLayout.CENTER);
		m_outputArea.setText( "Server awaiting connections\n" );
		setSize(300, 300);
		setVisible(true);
		Debug.println("<<< ScrabbleServer::ScrabbleServer");
	}
	public void startServer(ScrabbleServer app) {
		Debug.println(">>> ScrabbleServer::startServer");
		m_app = app;
		m_game = new Game(m_app, m_server);
		m_game.startServer(MAXPLAYERS);	//start the game
		Debug.println("<<< ScrabbleServer::startServer");
	}
	public Game getGameRef() {return m_game;}

   public void displayMessage( final String messageToDisplay ) {
//      Debug.println(">>> ScrabbleServer::displayMessage");
      SwingUtilities.invokeLater(
         new Runnable() {  // inner class to ensure GUI updates properly
            public void run() {
               m_outputArea.append( messageToDisplay );
               m_outputArea.setCaretPosition( 
                  m_outputArea.getText().length() );
            }
         }
      );
 //     Debug.println("<<< ScrabbleServer::displayMessage");
   }

	public static void main (String args[]) {
		ScrabbleServer app = new ScrabbleServer();
		app.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		app.startServer(app);
	}
}

