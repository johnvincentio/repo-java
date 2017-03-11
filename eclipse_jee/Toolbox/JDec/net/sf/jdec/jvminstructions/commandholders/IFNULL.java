package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFNULLCommand;
import net.sf.jdec.util.ExecutionState;

public class IFNULL extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFNULLCommand(ExecutionState.getMethodContext()));
	}
}
