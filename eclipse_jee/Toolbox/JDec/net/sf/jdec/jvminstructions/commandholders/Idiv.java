package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IDivCommand;
import net.sf.jdec.util.ExecutionState;

public class Idiv extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IDivCommand(ExecutionState.getMethodContext()));
	}
}
