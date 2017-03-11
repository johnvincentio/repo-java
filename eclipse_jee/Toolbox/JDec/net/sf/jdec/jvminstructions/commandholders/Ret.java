package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.RetCommand;
import net.sf.jdec.util.ExecutionState;

public class Ret extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new RetCommand(ExecutionState.getMethodContext()));
	}
}
