package com.idc.knight.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class MyKeyAdapter extends KeyAdapter {

	private KnightGui m_knightGui;
	public MyKeyAdapter (KnightGui knightGui) {
		m_knightGui = knightGui;
	}
	public void keyReleased (KeyEvent e) {
//		System.out.println(">>> MyKeyAdapter::keyReleased");
		long total = m_knightGui.getTotalSolutions();
		long current = m_knightGui.getSolutionNumber();
//		System.out.println("total "+total+" current "+current);

		int keyCode = e.getKeyCode();
//		System.out.println("keyPressed: "+keyCode);
		if (keyCode == 38) current++;			// 38 = up arrow
		if (keyCode == 40) current--;			// 40 is down arrow
		if (current < 1) current = total;
		if (current > total) current = 1;
//		System.out.println("current "+current);
		
		JTextField textField = (JTextField) e.getSource();
		textField.setText (Long.toString(current));
//		System.out.println("<<< MyKeyAdapter::keyReleased");
	}

	public void keyTyped (KeyEvent e) {
//		System.out.println("--- MyKeyAdapter::keyTyped");
	}

	public void keyPressed (KeyEvent e) {
//		System.out.println("--- MyKeyAdapter::keyPressed");
	}
}
