package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.FreturnCommand;
import net.sf.jdec.util.ExecutionState;

public class Freturn extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Freturn";
	}

	protected void registerCommand() {
		setCommand(new FreturnCommand(ExecutionState.getMethodContext()));
	}

}
