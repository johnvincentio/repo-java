package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FaddCommand;
import net.sf.jdec.util.ExecutionState;

public class Fadd extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fadd";
	}

	protected void registerCommand() {
		setCommand(new FaddCommand(ExecutionState.getMethodContext()));
	}

}
