package com.idc.a2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1;
    
    private static final int CELL_SIZE = 80; // Pixels
    private Font m_biggerFont;
    private SlidePuzzleModel m_puzzleModel;
    
    public GraphicsPanel(SlidePuzzleModel puzzleModel) {
    	m_puzzleModel = puzzleModel;
        m_biggerFont = new Font("SansSerif", Font.BOLD, CELL_SIZE/2);
        this.setPreferredSize(
               new Dimension(CELL_SIZE * m_puzzleModel.getCols(), CELL_SIZE * m_puzzleModel.getRows()));
        this.setBackground(Color.black);
        this.addMouseListener(this);  // Listen own mouse events.
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r=0; r < m_puzzleModel.getRows(); r++) {
            for (int c=0; c < m_puzzleModel.getCols(); c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;
                String text = m_puzzleModel.getFace(r, c);
                if (text != null) {
                    g.setColor(Color.gray);
                    g.fillRect(x+2, y+2, CELL_SIZE-4, CELL_SIZE-4);
                    g.setColor(Color.black);
                    g.setFont(m_biggerFont);
                    g.drawString(text, x+20, y+(3*CELL_SIZE)/4);
                }
            }
        }
    }
    
    public void mousePressed(MouseEvent e) {
        //--- map x,y coordinates into a row and col.
        int col = e.getX()/CELL_SIZE;
        int row = e.getY()/CELL_SIZE;
        
        if (! m_puzzleModel.moveTile(row, col)) {
            // moveTile moves tile if legal, else returns false.
            Toolkit.getDefaultToolkit().beep();
        }
        
        this.repaint();  // Show any updates to model.
    }

    public void mouseClicked (MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited  (MouseEvent e) {}
}
