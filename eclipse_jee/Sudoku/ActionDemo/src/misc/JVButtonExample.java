package misc;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class JVButtonExample {
	private JFrame frame;
	private JButton button;

	private void displayGUI() {
		frame = new JFrame("JV Button Mnemonic Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
/*
		Action buttonAction = new ButtonAction("CLICK ME", "This is a Click Me JButton", new Integer(KeyEvent.VK_C));
		button = new JButton(buttonAction);
		button.getInputMap().put(KeyStroke.getKeyStroke('c'), "Click Me Button");
		button.getActionMap().put("Click Me Button", buttonAction);
*/

		JButton btn = createButton("CLICK ME", "This is a Click Me JButton", KeyEvent.VK_C, "Click Me Button", 'c');
		contentPane.add(btn);

		frame.setContentPane(contentPane);
		frame.pack();
		frame.setSize(300,200);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	private JButton createButton (String title, String tooltip, int mnemonic, String mapKey, char keyStroke) {
		InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actionMap = frame.getRootPane().getActionMap();

		Action buttonAction = new ButtonAction (title, tooltip, new Integer(mnemonic));
		JButton button = new JButton(buttonAction);

		inputMap.put(KeyStroke.getKeyStroke(keyStroke), mapKey);
		actionMap.put(mapKey, buttonAction);

		return button;
	}

	class ButtonAction extends AbstractAction {
		public ButtonAction (String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed (ActionEvent ae) {
			JOptionPane.showMessageDialog(frame, "BINGO, you SAW me.");
		}
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JVButtonExample().displayGUI();
			}
		});
	}
}
