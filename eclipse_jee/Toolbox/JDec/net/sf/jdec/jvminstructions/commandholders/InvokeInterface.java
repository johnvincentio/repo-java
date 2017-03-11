package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InvokeInterfaceCommand;
import net.sf.jdec.util.ExecutionState;

public class InvokeInterface extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InvokeInterfaceCommand(ExecutionState.getMethodContext()));
	}
}

