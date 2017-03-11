package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.F2ICommand;
import net.sf.jdec.util.ExecutionState;

public class F2I extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "F2d";
	}

	protected void registerCommand() {
		setCommand(new F2ICommand(ExecutionState.getMethodContext()));
	}

}
