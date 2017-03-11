package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InvokeSpecialCommand;
import net.sf.jdec.util.ExecutionState;

public class InvokeSpecial extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InvokeSpecialCommand(ExecutionState.getMethodContext()));
	}
}

