package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.BipushCommand;
import net.sf.jdec.util.ExecutionState;

public class Bipush extends AbstractInstructionCommandHolder {

	public Bipush() {
	}

	protected String getName() {
		return "bipush";
	}

	protected void registerCommand() {
		BipushCommand command = new BipushCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
