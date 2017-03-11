package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FsubCommand;
import net.sf.jdec.util.ExecutionState;

public class Fsub extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fsub";
	}

	protected void registerCommand() {
		setCommand(new FsubCommand(ExecutionState.getMethodContext()));
	}

}
