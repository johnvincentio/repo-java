package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LremCommand;
import net.sf.jdec.util.ExecutionState;

public class Lrem extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LremCommand(ExecutionState.getMethodContext()));
	}
}

