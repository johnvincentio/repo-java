package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.L2fCommand;
import net.sf.jdec.util.ExecutionState;

public class L2f extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new L2fCommand(ExecutionState.getMethodContext()));
	}
}

