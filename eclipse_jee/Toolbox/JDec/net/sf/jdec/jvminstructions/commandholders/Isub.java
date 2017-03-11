package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.ISubCommand;
import net.sf.jdec.util.ExecutionState;

public class Isub extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new ISubCommand(ExecutionState.getMethodContext()));
	}
}
