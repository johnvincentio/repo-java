package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lload_3Command;
import net.sf.jdec.util.ExecutionState;

public class Lload_3 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lload_3Command(ExecutionState.getMethodContext()));
	}
}

