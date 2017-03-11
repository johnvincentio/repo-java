package misc;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

public class MnemonicButton {
	public static void main(String args[]) {
		JFrame frame = new JFrame("DefaultButton");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton button = new JButton("Click Text Button");

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Button was clicked!");
			}
		};
		button.addActionListener(al);

//		button.getModel().setMnemonic('U');
//		KeyStroke keyStroke = KeyStroke.getKeyStroke('U', 0, false);
//		KeyStroke keyStroke = KeyStroke.getKeyStroke('U', Event.CTRL_MASK, false);
//		button.registerKeyboardAction(al, keyStroke, JComponent.WHEN_FOCUSED);

		button.getModel().setMnemonic('C');
		KeyStroke keyStroke = KeyStroke.getKeyStroke('C', Event.CTRL_MASK, false);
		button.registerKeyboardAction(al, keyStroke, JComponent.WHEN_FOCUSED);

		frame.add(button);
		frame.setSize(300, 200);
		frame.setVisible(true);
	}
}
