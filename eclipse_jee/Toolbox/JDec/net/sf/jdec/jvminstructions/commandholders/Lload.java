package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Lload extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LloadCommand(ExecutionState.getMethodContext()));
	}
}

