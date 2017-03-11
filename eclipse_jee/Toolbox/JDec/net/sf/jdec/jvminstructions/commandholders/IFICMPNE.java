package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFICMPNECommand;
import net.sf.jdec.util.ExecutionState;

public class IFICMPNE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFICMPNECommand(ExecutionState.getMethodContext()));
	}
}

