package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lstore_3Command;
import net.sf.jdec.util.ExecutionState;

public class Lstore_3 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lstore_3Command(ExecutionState.getMethodContext()));
	}
}

