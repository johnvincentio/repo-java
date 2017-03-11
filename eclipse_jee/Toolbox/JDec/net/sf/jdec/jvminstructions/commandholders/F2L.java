package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.F2LCommand;
import net.sf.jdec.util.ExecutionState;

public class F2L extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "F2d";
	}

	protected void registerCommand() {
		setCommand(new F2LCommand(ExecutionState.getMethodContext()));
	}

}
