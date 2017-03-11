package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IConst_2Command;
import net.sf.jdec.util.ExecutionState;

public class IConst_2 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IConst_2Command(ExecutionState.getMethodContext()));
	}
}
