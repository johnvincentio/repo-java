package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dup2x2Command;
import net.sf.jdec.util.ExecutionState;

public class Dup2x2 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Dup2x2";
	}

	protected void registerCommand() {
		setCommand(new Dup2x2Command(ExecutionState.getMethodContext()));
	}

}
