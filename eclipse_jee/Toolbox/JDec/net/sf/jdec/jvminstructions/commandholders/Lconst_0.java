package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lconst_0Command;
import net.sf.jdec.util.ExecutionState;

public class Lconst_0 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lconst_0Command(ExecutionState.getMethodContext()));
	}
}

