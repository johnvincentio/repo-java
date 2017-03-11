package com.idc.ttt;

import javax.swing.*;
import java.awt.*;

/*
Square.java models a position in the game TicTacToe.
It knows it's position in the grid and knows it's value,
   either empty, an X or an O.
*/
public class Square extends JPanel {
	private static final long serialVersionUID = -8251581994465265194L;
  final static int SQUARE_X_MARK = 1;
  final static int SQUARE_O_MARK = 2;
  final static int SQUARE_EMPTY = 0;

  private int markValue;
  private int xCoord;
  private int yCoord;

  public Square(int i, int j) {xCoord = i; yCoord = j; markValue = SQUARE_EMPTY;}

  public Dimension getPreferredSize() {return new Dimension(30, 30);}
  public Dimension getMinimumSize() {return getPreferredSize();}

  public int getXCoord() {return this.xCoord;}
  public int getYCoord() {return this.yCoord;}
  public boolean isEmpty() {return (markValue == SQUARE_EMPTY? true : false);}

  private void setMarkValue(int i) {markValue = i; repaint();}
  public void setMarkOnSquare(boolean bXType) {
    if (bXType)
      setMarkValue(SQUARE_X_MARK);
    else
      setMarkValue(SQUARE_O_MARK);
  }
  private String MakeStringMark() {
    if (markValue == SQUARE_X_MARK)
      return "X";
    else if (markValue == SQUARE_O_MARK)
      return "O";
    else
      return " ";
  }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawRect(0, 0, 29, 29);
    g.drawString(MakeStringMark(), 11, 20);
  }
}

