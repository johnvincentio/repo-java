package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IushrCommand;
import net.sf.jdec.util.ExecutionState;

public class Iushr extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IushrCommand(ExecutionState.getMethodContext()));
	}
}
