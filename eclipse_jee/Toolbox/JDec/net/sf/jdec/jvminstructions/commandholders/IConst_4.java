package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_4Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_4 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_4Command(ExecutionState.getMethodContext()));
	}
}

