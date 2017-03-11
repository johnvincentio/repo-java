package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.DaddCommand;
import net.sf.jdec.util.ExecutionState;

public class Dadd extends AbstractInstructionCommandHolder {

	protected String getName() {
		return null;
	}

	protected void registerCommand() {
		DaddCommand command = new DaddCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}


}
