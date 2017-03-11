package com.idc.test1;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MakeFlag {

	public static void main(String[] args) {
		(new MakeFlag()).doApp();
	}

	public void doApp() {
		JFrame frame = new JFrame("Make a Flag");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		for (int i = 1; i < 15; i++)
			pane.add(new FlagPanel(this, i));

		frame.add(pane);
		frame.setSize(400,300);
		frame.setVisible(true);
	}
}
