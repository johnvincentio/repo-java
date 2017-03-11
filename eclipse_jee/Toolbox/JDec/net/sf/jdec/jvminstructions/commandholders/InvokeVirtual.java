package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InvokeVirtualCommand;
import net.sf.jdec.util.ExecutionState;

public class InvokeVirtual extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InvokeVirtualCommand(ExecutionState.getMethodContext()));
	}
}
