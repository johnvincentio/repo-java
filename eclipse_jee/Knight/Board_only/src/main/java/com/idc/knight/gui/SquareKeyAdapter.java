package com.idc.knight.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SquareKeyAdapter extends KeyAdapter {
	private BoardGui boardGui;

	public SquareKeyAdapter(BoardGui boardGui) {
//		System.out.println("--- SquareKeyAdapter::constructor");
		this.boardGui = boardGui;
	}
	public void keyReleased (KeyEvent e) {
//		System.out.println(">>> SquareKeyAdapter::keyReleased");
		int keyCode = e.getKeyCode();
//		System.out.println("keyPressed: "+keyCode);
		if (keyCode == 38) boardGui.redraw (true);			// 38 = up arrow
		if (keyCode == 40) boardGui.redraw (false);			// 40 is down arrow
//		System.out.println("<<< SquareKeyAdapter::keyReleased");
	}

	public void keyTyped (KeyEvent e) {
//		System.out.println("--- SquareKeyAdapter::keyTyped");
	}

	public void keyPressed (KeyEvent e) {
//		System.out.println("--- SquareKeyAdapter::keyPressed");
	}
}
