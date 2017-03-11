package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LreturnCommand;
import net.sf.jdec.util.ExecutionState;

public class Lreturn extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LreturnCommand(ExecutionState.getMethodContext()));
	}
}

