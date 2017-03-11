package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FloadCommand;
import net.sf.jdec.util.ExecutionState;

public class Fload extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fload";
	}

	protected void registerCommand() {
		setCommand(new FloadCommand(ExecutionState.getMethodContext()));
	}

}
