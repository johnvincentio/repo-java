package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.GotoCommand;
import net.sf.jdec.util.ExecutionState;

public class Goto extends AbstractInstructionCommandHolder {

	public Goto() {
	}

	protected void registerCommand() {
		setCommand(new GotoCommand(ExecutionState.getMethodContext()));
	}

}
