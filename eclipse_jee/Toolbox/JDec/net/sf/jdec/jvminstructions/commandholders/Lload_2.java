package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Lload_2Command;
import net.sf.jdec.util.ExecutionState;

public class Lload_2 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Lload_2Command(ExecutionState.getMethodContext()));
	}
}

