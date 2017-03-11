package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LmulCommand;
import net.sf.jdec.util.ExecutionState;

public class Lmul extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LmulCommand(ExecutionState.getMethodContext()));
	}
}

