package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.L2iCommand;
import net.sf.jdec.util.ExecutionState;

public class L2I extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new L2iCommand(ExecutionState.getMethodContext()));
	}
}

