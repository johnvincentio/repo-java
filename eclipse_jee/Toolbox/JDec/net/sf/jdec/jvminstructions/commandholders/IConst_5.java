package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_5Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_5 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_5Command(ExecutionState.getMethodContext()));
	}
}

