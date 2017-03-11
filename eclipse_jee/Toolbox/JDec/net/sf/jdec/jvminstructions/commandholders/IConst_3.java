package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_3Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_3 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_3Command(ExecutionState.getMethodContext()));
	}
}

