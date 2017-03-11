package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LnegCommand;
import net.sf.jdec.util.ExecutionState;

public class Lneg extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LnegCommand(ExecutionState.getMethodContext()));
	}
}
