package com.idc.a2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameAction implements ActionListener {
	private GraphicsPanel m_puzzleGraphics;
	private SlidePuzzleModel m_puzzleModel;
	public NewGameAction (GraphicsPanel puzzleGraphics, SlidePuzzleModel puzzleModel) {
		m_puzzleGraphics = puzzleGraphics;
		m_puzzleModel = puzzleModel;
	}
    public void actionPerformed(ActionEvent e) {
        m_puzzleModel.reset();
        m_puzzleGraphics.repaint();
    }
}



