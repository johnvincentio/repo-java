package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LcmpCommand;
import net.sf.jdec.util.ExecutionState;

public class Lcmp extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LcmpCommand(ExecutionState.getMethodContext()));
	}
}

