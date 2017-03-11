package misc;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Buttons {
	private static ButtonHandler buttonHandler = new Buttons().new ButtonHandler();
	private static JPanel jp;

	public static void main(String[] args) {
		JFrame jf = new JFrame("Sample");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jp = new JPanel(new FlowLayout());
		jp.setFocusable(true);
		jf.add(jp);

		jp.add(createButton("Bye", KeyEvent.VK_B));
		jp.add(createButton("Hello", KeyEvent.VK_H));

		jp.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				System.out.println("Key pressed");
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					doBye();
				} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					doHello();
				}
			}
		});

		jf.pack();
		jf.setVisible(true);
	}

	private static JButton createButton(String name, int mnemonic) {
		JButton b1 = new JButton(name);
		b1.addActionListener(buttonHandler);
		b1.setMnemonic(mnemonic);
		b1.setDisplayedMnemonicIndex(name.indexOf(mnemonic));
		b1.setActionCommand(name);
		return b1;
	}

	private static void doHello() {
		System.out.println("Hello");
		jp.requestFocus();
	}

	private static void doBye() {
		System.out.println("Bye");
		jp.requestFocus();
	}

	private class ButtonHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			System.out.println("Button pressed");
			if ("Hello".equals(e.getActionCommand())) {
				doHello();
			} else if ("Bye".equals(e.getActionCommand())) {
				doBye();
			}
		}
	}
}
