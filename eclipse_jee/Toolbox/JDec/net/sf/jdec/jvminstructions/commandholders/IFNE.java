package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFNECommand;
import net.sf.jdec.util.ExecutionState;

public class IFNE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFNECommand(ExecutionState.getMethodContext()));
	}
}
