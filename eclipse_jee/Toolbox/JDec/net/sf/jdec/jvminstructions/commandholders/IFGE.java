package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFGECommand;
import net.sf.jdec.util.ExecutionState;

public class IFGE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFGECommand(ExecutionState.getMethodContext()));
	}
}
