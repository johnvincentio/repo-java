package com.idc.a2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SlidePuzzleGUI extends JPanel {
	private static final long serialVersionUID = 1;
	private static final int ROWS = 3;
	private static final int COLS = 3;
    private GraphicsPanel m_puzzleGraphics;
    private SlidePuzzleModel m_puzzleModel;

    public SlidePuzzleGUI() {
        m_puzzleModel = new SlidePuzzleModel(ROWS, COLS);
    	m_puzzleGraphics = new GraphicsPanel(m_puzzleModel);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new NewGameAction(m_puzzleGraphics, m_puzzleModel));

        //--- Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(newGameButton);
        
        //--- Set the layout and add the components
        this.setLayout(new BorderLayout());
        this.add(controlPanel, BorderLayout.NORTH);
        this.add(m_puzzleGraphics, BorderLayout.CENTER);
    }
}
