package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dup2x1Command;
import net.sf.jdec.util.ExecutionState;

public class Dup2x1 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Dup2x1";
	}

	protected void registerCommand() {
		setCommand(new Dup2x1Command(ExecutionState.getMethodContext()));
	}

}
