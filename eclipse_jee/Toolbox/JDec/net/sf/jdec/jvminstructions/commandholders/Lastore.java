package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Lastore extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LastoreCommand(ExecutionState.getMethodContext()));
	}
}

