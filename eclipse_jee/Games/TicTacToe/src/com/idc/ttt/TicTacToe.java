package com.idc.ttt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToe extends JFrame {
	private static final long serialVersionUID = -7041186964035527386L;

	private Container contentPane;
  private JTextArea displayArea;  // messages for the user
  private JTextField idField;     // player turn message
  private JPanel boardPanel;    // panel for the grid
  private JPanel panel2;        // panel to stick the grid panel to the content pane
  private Square square[][];    // tictactoe grid.
  private int marks[][];        // keeps a count for each square.
  private int iaTotals[];       // 8 buckets for 8 winning scenarios
  private boolean bXPlayer = true;  // true is X is to play.
  MouseListener lstner;         // look for mouse clicks

  public TicTacToe() {
    setTitle("TicTacToe");
    contentPane = getContentPane();

    displayArea = new JTextArea(4, 30);
    displayArea.setEditable(false);
    contentPane.add(new JScrollPane(displayArea), BorderLayout.SOUTH);

    idField = new JTextField();
    idField.setEditable(false);
    contentPane.add(idField, BorderLayout.NORTH);

    boardPanel = new JPanel();
    boardPanel.setLayout(new GridLayout(3, 3, 0, 0));

    lstner = new MouseListener() {
      public void mouseReleased(MouseEvent me) {makeMove((Square) me.getComponent());}
      public void mouseClicked(MouseEvent me) {/*sayWhat("clicked",me);*/}
      public void mousePressed(MouseEvent me) {/*sayWhat("pressed",me);*/}
      public void mouseEntered(MouseEvent me) {/*sayWhat("entered",me);*/}
      public void mouseExited(MouseEvent me) {/*sayWhat("exited",me);*/}
    };

    square = new Square[3][3];
    marks = new int[3][3];
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        square[row][col] = new Square(row, col);
        square[row][col].addMouseListener(lstner);  // listen for mouse events
        boardPanel.add(square[row][col]);
        marks[row][col] = 0;                        // initialize
      }
    }
    iaTotals = new int[8];
    for (int i=0; i<8; i++) iaTotals[i] = 0;       // initialize
    setToPlayer();

    panel2 = new JPanel();
    panel2.add(boardPanel, BorderLayout.CENTER);
    contentPane.add(panel2, BorderLayout.CENTER);
    pack();
    setVisible(true);
  }

  private void sayWhat(String evDs, MouseEvent e) { // useful trace when I messed up!
    setAreaMessage(evDs + " detected on " + e.getComponent().getClass().getName()+" .\n");
  }

  private void setIDMessage(String msg) {idField.setText(msg);}
  private void setAreaMessage(String msg) {displayArea.setText(msg);}
  private void setToPlayer () {
    if (bXPlayer) {
      setIDMessage("Player X");
      setAreaMessage("Player X - Just point that old mouse and click");
    }
    else {
      setIDMessage("Player O");
      setAreaMessage("Player O - Point and click");
    }
  }
  private void setWonGame () {
    if (bXPlayer)
      setAreaMessage("Player X just won the game!");
    else
      setAreaMessage("Player O always was going to win!");
  }
  private void setDrawnGame () {
    setIDMessage("");
    setAreaMessage("The game is a draw");
  }

  public static void main(String args[]) {
    TicTacToe ticTacToe = new TicTacToe();
    ticTacToe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  private void makeMove(Square sq) {
    if (! sq.isEmpty()) {
      setAreaMessage("That position is occupied!");
      return;
    }
    sq.setMarkOnSquare(bXPlayer);  // mark the grid

//  Recalculate the buckets
//  There are only 8 possible winning scenarios, so assign a bucket for each.
    int x = sq.getXCoord();
    int y = sq.getYCoord();
    int inc = bXPlayer ? 100 : 1000;
    marks[x][y] = inc;     // mark the current grid position
    iaTotals[0] = marks[0][0] + marks[0][1] + marks[0][2];  // (0,0) is the top left
    iaTotals[1] = marks[1][0] + marks[1][1] + marks[1][2];
    iaTotals[2] = marks[2][0] + marks[2][1] + marks[2][2];
    iaTotals[3] = marks[0][0] + marks[1][0] + marks[2][0];
    iaTotals[4] = marks[0][1] + marks[1][1] + marks[2][1];
    iaTotals[5] = marks[0][2] + marks[1][2] + marks[2][2];
    iaTotals[6] = marks[0][0] + marks[1][1] + marks[2][2];
    iaTotals[7] = marks[0][2] + marks[1][1] + marks[2][0];

    int winner = -1;                   // look for a won game
    for (int i=0; i<8; i++) {
//      System.out.println(" (i,total) ("+i+","+iaTotals[i]+")");
      if (iaTotals[i] == inc * 3) { // X win = 300, O win = 3000
        disableMyMouse();
        setWonGame();
        return;
      }
    }

    int nTotal = 0;                  // look for a drawn game
    int nMax = (5 * 100) + (4 * 1000);
    for (int i=0; i<3; i++) {
      for (int j=0; j<3; j++) nTotal += marks[i][j];
    }
//    System.out.println("nMax "+nMax+" nTotal "+nTotal);
    if (nTotal >= nMax) {
      disableMyMouse();
      setDrawnGame();
      return;
    }
     bXPlayer = ! bXPlayer;
     setToPlayer();
  }

  private void disableMyMouse() {
    for (int i=0; i<3; i++) {
      for (int j=0;j<3; j++)
        square[i][j].removeMouseListener(lstner); // disable the mouse listener
    }
  }
}
