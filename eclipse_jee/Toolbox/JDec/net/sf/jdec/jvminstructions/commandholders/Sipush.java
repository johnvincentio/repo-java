package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.SipushCommand;
import net.sf.jdec.util.ExecutionState;

public class Sipush extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new SipushCommand(ExecutionState.getMethodContext()));
	}
}

