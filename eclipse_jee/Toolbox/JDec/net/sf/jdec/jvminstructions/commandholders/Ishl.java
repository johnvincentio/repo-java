package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IshlCommand;
import net.sf.jdec.util.ExecutionState;

public class Ishl extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IshlCommand(ExecutionState.getMethodContext()));
	}
}
