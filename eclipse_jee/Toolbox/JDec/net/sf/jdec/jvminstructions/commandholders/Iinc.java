package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IINCCommand;
import net.sf.jdec.util.ExecutionState;

public class Iinc extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IINCCommand(ExecutionState.getMethodContext()));
	}
}

