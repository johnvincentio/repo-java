package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.JsrCommand;
import net.sf.jdec.util.ExecutionState;

public class Jsr extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new JsrCommand(ExecutionState.getMethodContext()));
	}
}

