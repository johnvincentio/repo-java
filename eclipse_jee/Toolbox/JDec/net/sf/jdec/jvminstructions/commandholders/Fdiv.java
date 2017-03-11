package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FdivCommand;
import net.sf.jdec.util.ExecutionState;

public class Fdiv extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fdiv";
	}

	protected void registerCommand() {
		setCommand(new FdivCommand(ExecutionState.getMethodContext()));
	}

}
