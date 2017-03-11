package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Dup2Command;
import net.sf.jdec.util.ExecutionState;

public class Dup2 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Dup2";
	}

	protected void registerCommand() {
		setCommand(new Dup2Command(ExecutionState.getMethodContext()));
	}

}
