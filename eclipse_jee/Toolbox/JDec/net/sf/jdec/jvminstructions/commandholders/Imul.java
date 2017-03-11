package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.ImulCommand;
import net.sf.jdec.util.ExecutionState;

public class Imul extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new ImulCommand(ExecutionState.getMethodContext()));
	}
}

