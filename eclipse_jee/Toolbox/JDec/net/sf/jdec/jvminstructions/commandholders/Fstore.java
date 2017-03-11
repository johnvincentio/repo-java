package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FstoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Fstore extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fstore";
	}

	protected void registerCommand() {
		setCommand(new FstoreCommand(ExecutionState.getMethodContext()));
	}

}
