package misc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class Main {
	public static void main(String[] argv) throws Exception {
		JButton component = new JButton("Button");
		component.getInputMap().put(KeyStroke.getKeyStroke("F2"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("control A"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("shift F2"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke('('), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("button3 F"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("typed x"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("released DELETE"), "actionName");
		component.getInputMap().put(KeyStroke.getKeyStroke("shift UP"), "actionName");

		component.getActionMap().put("actionName",
			new AbstractAction("actionName") {
				public void actionPerformed(ActionEvent evt) {
					System.out.println(evt);
				}
			});
	}
}
