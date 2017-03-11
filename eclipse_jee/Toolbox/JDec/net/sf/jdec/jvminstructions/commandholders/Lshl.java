package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LshlCommand;
import net.sf.jdec.util.ExecutionState;

public class Lshl extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LshlCommand(ExecutionState.getMethodContext()));
	}
}

