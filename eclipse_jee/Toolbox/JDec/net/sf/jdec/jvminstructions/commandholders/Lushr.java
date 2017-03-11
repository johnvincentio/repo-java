package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LushrCommand;
import net.sf.jdec.util.ExecutionState;

public class Lushr extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LushrCommand(ExecutionState.getMethodContext()));
	}
}