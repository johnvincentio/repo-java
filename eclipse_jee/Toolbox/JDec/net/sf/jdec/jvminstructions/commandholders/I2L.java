package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2LCommand;
import net.sf.jdec.util.ExecutionState;

public class I2L extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2LCommand(ExecutionState.getMethodContext()));
	}
}