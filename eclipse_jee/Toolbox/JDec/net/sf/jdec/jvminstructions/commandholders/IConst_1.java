package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_1Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_1Command(ExecutionState.getMethodContext()));
	}
}

