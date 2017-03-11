package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2SCommand;
import net.sf.jdec.util.ExecutionState;

public class I2S extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2SCommand(ExecutionState.getMethodContext()));
	}
}
