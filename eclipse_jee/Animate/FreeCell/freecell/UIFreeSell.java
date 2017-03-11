// File  : freesell/UIFreeSell.java
// Description: Freecell solitaire program.
//         Main program / JFrame.  Adds a few components and the 
//         main graphics area, UICardPanel, that handles the mouse and painting.
// Author: Fred Swartz - Feb 20 2007 - Placed in public domain.

package freecell;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.event.*;

/////////////////////////////////////////////////////////////// class UIFreeSell
public class UIFreeSell extends JFrame {
	private static final long serialVersionUID = 1;
    //=================================================================== fields
    private GameModel _model;
    
    private UICardPanel _boardDisplay;
    
    private JCheckBox _autoCompleteCB = new JCheckBox("Auto Complete");
    
    //===================================================================== main
    public static void main(String[] args) {
        //... Do all GUI initialization on EDT thread.  This is the
        //    correct way, but is often omitted because the other
        //    almost always(!) works.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UIFreeSell();
            }
        });
    }
    
    //============================================================== constructor
    public UIFreeSell() {
    	_model = new GameModel();
        _boardDisplay = new UICardPanel(_model);
        
        //... Create button and check box.
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(new ActionNewGame());
  
        JButton restartGameBtn = new JButton("Restart Game");
        restartGameBtn.addActionListener(new ActionRestartGame());
       
        _autoCompleteCB.setSelected(true);
        _autoCompleteCB.addActionListener(new ActionAutoComplete());
        _boardDisplay.setAutoCompletion(_autoCompleteCB.isSelected());
        
        //... Do layout
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(newGameBtn);
        controlPanel.add(restartGameBtn);
        controlPanel.add(_autoCompleteCB);
        
        //... Create content pane with graphics area in center (so it expands)
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(controlPanel, BorderLayout.NORTH);
        content.add(_boardDisplay, BorderLayout.CENTER);
        
        //... Set this window's characteristics.
        setContentPane(content);
        setTitle("Free Cell");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    ////////////////////////////////////////////////////////////// ActionNewGame
    class ActionNewGame implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
        	_model.reset();
        }
    }
    class ActionRestartGame implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            _model.restart();
        }
    }   
    ///////////////////////////////////////////////////////// ActionAutoComplete
    class ActionAutoComplete implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            _boardDisplay.setAutoCompletion(_autoCompleteCB.isSelected());
        }
    }
    
}
