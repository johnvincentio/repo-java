package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFNONNULLCommand;
import net.sf.jdec.util.ExecutionState;

public class IFNONNULL extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFNONNULLCommand(ExecutionState.getMethodContext()));
	}
}
