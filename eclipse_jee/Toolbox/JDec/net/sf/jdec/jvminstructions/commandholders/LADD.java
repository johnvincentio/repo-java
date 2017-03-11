package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LaddCommand;
import net.sf.jdec.util.ExecutionState;

public class LADD extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LaddCommand(ExecutionState.getMethodContext()));
	}
}

