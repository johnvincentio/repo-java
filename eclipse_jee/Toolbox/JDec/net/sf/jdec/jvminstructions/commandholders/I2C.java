package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2CCommand;
import net.sf.jdec.util.ExecutionState;

public class I2C extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2CCommand(ExecutionState.getMethodContext()));
	}
}
