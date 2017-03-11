package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_0Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_0 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_0Command(ExecutionState.getMethodContext()));
	}
}

