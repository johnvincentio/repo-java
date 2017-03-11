package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lstore_1Command;
import net.sf.jdec.util.ExecutionState;

public class Lstore_1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lstore_1Command(ExecutionState.getMethodContext()));
	}
}

