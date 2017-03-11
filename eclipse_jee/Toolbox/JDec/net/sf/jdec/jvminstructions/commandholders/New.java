package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.NewCommand;
import net.sf.jdec.util.ExecutionState;

public class New extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new NewCommand(ExecutionState.getMethodContext()));
	}
}

