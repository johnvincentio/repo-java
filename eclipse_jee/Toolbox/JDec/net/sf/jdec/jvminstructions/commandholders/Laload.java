package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Laload extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LaloadCommand(ExecutionState.getMethodContext()));
	}
}

