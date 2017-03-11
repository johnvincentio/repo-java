package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.SwapCommand;
import net.sf.jdec.util.ExecutionState;

public class Swap extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new SwapCommand(ExecutionState.getMethodContext()));
	}
}

