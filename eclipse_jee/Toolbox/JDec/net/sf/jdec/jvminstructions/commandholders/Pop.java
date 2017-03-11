package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.PopCommand;
import net.sf.jdec.util.ExecutionState;

public class Pop extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new PopCommand(ExecutionState.getMethodContext()));
	}
}
