package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPGECommand;
import net.sf.jdec.util.ExecutionState;

public class IFICMPGE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPGECommand(ExecutionState.getMethodContext()));
	}
}

