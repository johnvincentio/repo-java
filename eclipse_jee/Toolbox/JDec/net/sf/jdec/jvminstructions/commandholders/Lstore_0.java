package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lstore_0Command;
import net.sf.jdec.util.ExecutionState;

public class Lstore_0 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lstore_0Command(ExecutionState.getMethodContext()));
	}
}

