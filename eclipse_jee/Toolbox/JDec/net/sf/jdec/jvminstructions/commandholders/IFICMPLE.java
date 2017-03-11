package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPLECommand;
import net.sf.jdec.util.ExecutionState;

public class IFICMPLE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPLECommand(ExecutionState.getMethodContext()));
	}
}
