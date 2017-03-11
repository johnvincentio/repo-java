package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LsubCommand;
import net.sf.jdec.util.ExecutionState;

public class Lsub extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LsubCommand(ExecutionState.getMethodContext()));
	}
}

