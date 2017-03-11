package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.BaloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Baload extends AbstractInstructionCommandHolder {

	public Baload() {
		
	}

	protected String getName() {
		
		return "Baload";
	}

	protected void registerCommand() {
		BaloadCommand command = new BaloadCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
