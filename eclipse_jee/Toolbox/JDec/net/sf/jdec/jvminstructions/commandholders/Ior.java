package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IORCommand;
import net.sf.jdec.util.ExecutionState;

public class Ior extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IORCommand(ExecutionState.getMethodContext()));
	}
}
