package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IFGTCommand;
import net.sf.jdec.util.ExecutionState;

public class IFGT extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IFGTCommand(ExecutionState.getMethodContext()));
	}
}

