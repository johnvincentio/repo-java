package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IshrCommand;
import net.sf.jdec.util.ExecutionState;

public class Ishr extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IshrCommand(ExecutionState.getMethodContext()));
	}
}
