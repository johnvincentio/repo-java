package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFEQCommand;
import net.sf.jdec.util.ExecutionState;

public class IFEQ extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFEQCommand(ExecutionState.getMethodContext()));
	}
}
