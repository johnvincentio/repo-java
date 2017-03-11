package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IReturnCommand;
import net.sf.jdec.util.ExecutionState;

public class Ireturn extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IReturnCommand(ExecutionState.getMethodContext()));
	}
}

