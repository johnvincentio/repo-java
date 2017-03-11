package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPEQCommand;
import net.sf.jdec.util.ExecutionState;

public  class IFICMPEQ extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPEQCommand(ExecutionState.getMethodContext()));
	}
}

