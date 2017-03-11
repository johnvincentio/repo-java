package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lload_0Command;
import net.sf.jdec.util.ExecutionState;

public class Lload_0 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lload_0Command(ExecutionState.getMethodContext()));
	}
}

