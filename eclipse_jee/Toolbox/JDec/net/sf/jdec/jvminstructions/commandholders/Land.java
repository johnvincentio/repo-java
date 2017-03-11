package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LandCommand;
import net.sf.jdec.util.ExecutionState;

public class Land extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LandCommand(ExecutionState.getMethodContext()));
	}
}

