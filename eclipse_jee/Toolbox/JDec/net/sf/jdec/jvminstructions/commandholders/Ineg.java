package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InegCommand;
import net.sf.jdec.util.ExecutionState;

public class Ineg extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InegCommand(ExecutionState.getMethodContext()));
	}
}
