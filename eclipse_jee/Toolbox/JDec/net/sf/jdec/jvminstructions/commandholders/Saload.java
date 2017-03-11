package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.SaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Saload extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new SaloadCommand(ExecutionState.getMethodContext()));
	}
}

