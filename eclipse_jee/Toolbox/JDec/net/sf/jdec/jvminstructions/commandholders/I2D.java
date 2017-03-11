package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.I2DCommand;
import net.sf.jdec.util.ExecutionState;

public class I2D extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new I2DCommand(ExecutionState.getMethodContext()));
	}
}
