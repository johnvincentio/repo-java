package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IremCommand;
import net.sf.jdec.util.ExecutionState;

public class Irem extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IremCommand(ExecutionState.getMethodContext()));
	}
}
