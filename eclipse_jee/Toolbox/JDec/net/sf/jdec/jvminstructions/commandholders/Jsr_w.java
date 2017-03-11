package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Jsr_WCommand;
import net.sf.jdec.util.ExecutionState;

public class Jsr_w extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Jsr_WCommand(ExecutionState.getMethodContext()));
	}
}

