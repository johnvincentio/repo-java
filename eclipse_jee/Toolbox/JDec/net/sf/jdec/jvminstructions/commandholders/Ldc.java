package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LdcCommand;
import net.sf.jdec.util.ExecutionState;

public class Ldc extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LdcCommand(ExecutionState.getMethodContext()));
	}
}

