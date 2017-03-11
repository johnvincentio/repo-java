package net.sf.jdec.jvminstructions.commands;

import net.sf.jdec.reflection.Behaviour;

public class JsrCommand extends AbstractInstructionCommand {

	public JsrCommand(Behaviour context) {
		super(context);

	}

	public int getSkipBytes() {
		return 2;
	}

	public void execute() {
		/**
		 *  Leave empty
		 *  There is no need to handle this methods
		 *  as it is handled already by the
		 *  exception handler codes. 
		 *  
		 *  NOTE: Handled by try/finally handling code.
		 */
	}

}
