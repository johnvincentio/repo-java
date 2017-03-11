package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.NewArrayCommand;
import net.sf.jdec.util.ExecutionState;

public class NewArray extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new NewArrayCommand(ExecutionState.getMethodContext()));
	}
}

