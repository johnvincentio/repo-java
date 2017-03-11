package misc;

import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.KeyStroke;

public class Main2 extends JFrame {
	JList list;

	public Main2() {
		getContentPane().setLayout(new FlowLayout());

		final JButton button = new JButton("Click-Me!");

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Button was clicked!");
			}
		};
		button.addActionListener(al);

		// don't use button.setMnemonic('C') if you want only the CTRL mask to
		// work,
		// use button.getModel() instead.
		button.getModel().setMnemonic('C');
		KeyStroke keyStroke = KeyStroke.getKeyStroke('C', Event.CTRL_MASK, false);
		button.registerKeyboardAction(al, keyStroke, JComponent.WHEN_FOCUSED);

		getContentPane().add(button);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		setSize(400, 400);
	}

	public static void main(String[] args) {
		(new Main2()).show();
	}
}