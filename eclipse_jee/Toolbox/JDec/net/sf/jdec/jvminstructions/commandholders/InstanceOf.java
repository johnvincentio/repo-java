package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InstanceOfCommand;
import net.sf.jdec.util.ExecutionState;

public class InstanceOf extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InstanceOfCommand(ExecutionState.getMethodContext()));
	}
}

