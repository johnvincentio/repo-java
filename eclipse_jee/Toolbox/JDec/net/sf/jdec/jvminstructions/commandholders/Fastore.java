package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Fastore extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fastore";
	}

	protected void registerCommand() {
		setCommand(new FastoreCommand(ExecutionState.getMethodContext()));
	}

}
