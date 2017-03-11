package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IxorCommand;
import net.sf.jdec.util.ExecutionState;

public class Ixor extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IxorCommand(ExecutionState.getMethodContext()));
	}
}
