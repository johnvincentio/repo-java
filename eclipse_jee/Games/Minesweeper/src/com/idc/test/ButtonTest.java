package com.idc.test;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ButtonTest {
	public static void main(String[] args) {
		new ButtonTest().test();
	}

	public void test() {
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(new File("gifs/flag.gif").toURI().toURL());
		}
		catch (MalformedURLException me) {
			System.out.println("Exception; "+me.getMessage());
		}
		JFrame frame = new JFrame("ButtonTest");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();

		JButton btn1 = new JButton("One");
		pane.add(btn1);

		JButton btn2 = new JButton("Two");
		btn2.setSelected(true);
		btn2.setEnabled(false);
		pane.add(btn2);	

		JButton btn3 = new MyButton("Three");
		pane.add(btn3);

		JButton btn4 = new JButton();
		btn4.setText("\u2600");
		btn4.setSelected(true);
		btn4.setEnabled(false);
		pane.add(btn4);	


		frame.add(pane);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	class MyButton extends JButton {
		private static final long serialVersionUID = 409141704059175687L;

		final static int SQUARE_SIZE = 40;

		public MyButton(String title) {
			super(title);
		}
		public Dimension getPreferredSize() {return new Dimension(SQUARE_SIZE, SQUARE_SIZE);}
		public Dimension getMinimumSize() {return getPreferredSize();}

		public void paint(Graphics g) {
			super.paint(g);
			System.out.println(">>> MyButton::paint");
			System.out.println("<<< MyButton::paint");
		}
	}
}

/*
//		JButton button = new JButton("Press", icon);
		pane.add(new JButton("zero"));
		pane.add(new MyButton("Zero"));
		pane.add(new MyButton("one"));
//		pane.add(button);
		pane.add(new JButton("two"));
		pane.add(new JButton(icon));
		pane.add(new JButton("three"));
*/
