package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Ldc2_wCommand;
import net.sf.jdec.util.ExecutionState;

public class Ldc2_w extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Ldc2_wCommand(ExecutionState.getMethodContext()));
	}
}

