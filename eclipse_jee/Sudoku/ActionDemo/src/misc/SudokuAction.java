package misc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SudokuAction extends AbstractAction {
	private static final long serialVersionUID = 7987483303188562190L;

	public SudokuAction (String name, String shortDescription, Integer mnemonic) {
		super(name);
		putValue(SHORT_DESCRIPTION, shortDescription);
		putValue(MNEMONIC_KEY, mnemonic);
	}
	public void actionPerformed (ActionEvent e) {
		System.out.println("Action [" + e.getActionCommand() + "] performed!");
	}
}
