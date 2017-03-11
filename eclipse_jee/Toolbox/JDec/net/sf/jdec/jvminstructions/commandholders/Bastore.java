package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.BastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Bastore extends AbstractInstructionCommandHolder {

	public Bastore() {
	}

	protected String getName() {
		return "Bastore";
	}

	protected void registerCommand() {
		BastoreCommand command = new BastoreCommand(ExecutionState.getMethodContext());
		setCommand(command);
	}

}
