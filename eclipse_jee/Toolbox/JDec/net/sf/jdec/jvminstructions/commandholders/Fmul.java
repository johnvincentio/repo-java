package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FmulCommand;
import net.sf.jdec.util.ExecutionState;

public class Fmul extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fmul";
	}

	protected void registerCommand() {
		setCommand(new FmulCommand(ExecutionState.getMethodContext()));
	}

}
