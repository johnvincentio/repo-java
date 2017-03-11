package com.idc.nothing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Main extends JFrame {
	private static final long serialVersionUID = -3251809862078059210L;

	public Main() throws HeadlessException {
		setSize(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel usernameLabel = new JLabel("Username: ");
		JTextField usernameTextField = new JTextField();
		usernameTextField.setPreferredSize(new Dimension(100, 20));
		add (usernameLabel);
		add (usernameTextField);

		usernameTextField.addKeyListener (new KeyAdapter() {
			public void keyReleased (KeyEvent e) {
				JTextField textField = (JTextField) e.getSource();
				String text = textField.getText();
				textField.setText(text.toUpperCase());
			}

			public void keyTyped (KeyEvent e) {
				int ab = e.getKeyCode();
				char cd = e.getKeyChar();
				System.out.println("keyTyped: "+ab+" char :"+cd+":");
			}

			public void keyPressed (KeyEvent e) {
				int ab = e.getKeyCode();
				System.out.println("keyPressed: "+ab);
			}
		});
	}
/*
up arrow = 38
down arrow = 40
page up = 33
page down = 34
del = 127
home = 36
end = 35
enter = 10
*/
	public static void main(String[] args) {
		new Main().setVisible(true);
	}
}