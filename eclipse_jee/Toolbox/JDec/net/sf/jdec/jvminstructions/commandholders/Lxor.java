package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LxorCommand;
import net.sf.jdec.util.ExecutionState;

public class Lxor extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LxorCommand(ExecutionState.getMethodContext()));
	}
}

