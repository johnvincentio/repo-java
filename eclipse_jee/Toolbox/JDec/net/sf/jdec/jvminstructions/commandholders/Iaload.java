package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Iaload extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IaloadCommand(ExecutionState.getMethodContext()));
	}
}

