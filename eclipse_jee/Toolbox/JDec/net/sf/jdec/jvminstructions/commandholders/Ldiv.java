package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LdivCommand;
import net.sf.jdec.util.ExecutionState;

public class Ldiv extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LdivCommand(ExecutionState.getMethodContext()));
	}
}

