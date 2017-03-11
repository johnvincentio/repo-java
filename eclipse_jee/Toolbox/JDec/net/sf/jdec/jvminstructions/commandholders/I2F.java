package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2FCommand;
import net.sf.jdec.util.ExecutionState;

public class I2F extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2FCommand(ExecutionState.getMethodContext()));
	}
}
