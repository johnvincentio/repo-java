package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.D2LCommand;
import net.sf.jdec.util.ExecutionState;

public class D2L extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "D2L";
	}

	protected void registerCommand() {
		setCommand(new D2LCommand(ExecutionState.getMethodContext()));
	}

}
