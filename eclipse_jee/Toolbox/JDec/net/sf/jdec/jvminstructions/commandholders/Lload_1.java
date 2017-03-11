package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lload_1Command;
import net.sf.jdec.util.ExecutionState;

public class Lload_1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lload_1Command(ExecutionState.getMethodContext()));
	}
}

