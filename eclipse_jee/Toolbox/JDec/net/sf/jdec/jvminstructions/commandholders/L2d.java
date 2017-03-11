package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.L2dCommand;
import net.sf.jdec.util.ExecutionState;

public class L2d extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new L2dCommand(ExecutionState.getMethodContext()));
	}
}

