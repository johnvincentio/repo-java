package com.idc.a3;

import javax.swing.JFrame;

public class RollDice extends JFrame {
	private static final long serialVersionUID = 1;
    
    public RollDice() {
        this.setContentPane(new RollDicePanel());
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Dice Demo");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new RollDicePanel());
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
