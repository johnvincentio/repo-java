package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LshrCommand;
import net.sf.jdec.util.ExecutionState;

public class Lshr extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LshrCommand(ExecutionState.getMethodContext()));
	}
}

