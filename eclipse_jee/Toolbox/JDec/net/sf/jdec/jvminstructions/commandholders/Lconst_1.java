package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lconst_1Command;
import net.sf.jdec.util.ExecutionState;

public class Lconst_1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lconst_1Command(ExecutionState.getMethodContext()));
	}
}

