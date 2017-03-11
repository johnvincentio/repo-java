package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Ldc_wCommand;
import net.sf.jdec.util.ExecutionState;

public class Ldc_w extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Ldc_wCommand(ExecutionState.getMethodContext()));
	}
}

