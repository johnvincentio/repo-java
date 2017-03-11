package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Faload extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Faload";
	}

	protected void registerCommand() {
		setCommand(new FaloadCommand(ExecutionState.getMethodContext()));
	}

}
