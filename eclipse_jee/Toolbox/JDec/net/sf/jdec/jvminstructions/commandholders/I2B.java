package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2BCommand;
import net.sf.jdec.util.ExecutionState;

public class I2B extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2BCommand(ExecutionState.getMethodContext()));
	}

}
