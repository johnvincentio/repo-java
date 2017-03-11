package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LorCommand;
import net.sf.jdec.util.ExecutionState;

public class Lor extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LorCommand(ExecutionState.getMethodContext()));
	}
}

