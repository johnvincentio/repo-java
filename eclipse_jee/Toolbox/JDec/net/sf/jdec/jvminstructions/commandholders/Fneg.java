package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FnegCommand;
import net.sf.jdec.util.ExecutionState;

public class Fneg extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fneg";
	}

	protected void registerCommand() {
		setCommand(new FnegCommand(ExecutionState.getMethodContext()));
	}

}
