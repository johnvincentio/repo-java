package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.InvokeStaticCommand;
import net.sf.jdec.util.ExecutionState;

public class InvokeStatic extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new InvokeStaticCommand(ExecutionState.getMethodContext()));
	}
}
