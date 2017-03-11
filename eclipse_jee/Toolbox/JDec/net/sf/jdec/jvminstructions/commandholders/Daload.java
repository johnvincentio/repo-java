package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.DaloadCommad;
import net.sf.jdec.util.ExecutionState;

public class Daload extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Daload";
	}

	protected void registerCommand() {
		setCommand(new DaloadCommad(ExecutionState.getMethodContext()));
	}

}
