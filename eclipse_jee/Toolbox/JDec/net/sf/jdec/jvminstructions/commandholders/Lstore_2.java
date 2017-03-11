package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lstore_2Command;
import net.sf.jdec.util.ExecutionState;

public class Lstore_2 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lstore_2Command(ExecutionState.getMethodContext()));
	}
}

