package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FremCommand;
import net.sf.jdec.util.ExecutionState;

public class Frem extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Frem";
	}

	protected void registerCommand() {
		setCommand(new FremCommand(ExecutionState.getMethodContext()));
	}

}
