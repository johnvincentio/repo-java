package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFACMPNECommand;
import net.sf.jdec.util.ExecutionState;

public class IFACMPNE extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFACMPNECommand(ExecutionState.getMethodContext()));
	}
}
