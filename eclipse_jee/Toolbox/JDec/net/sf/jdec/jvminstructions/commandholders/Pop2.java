package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Pop2Command;
import net.sf.jdec.util.ExecutionState;

public class Pop2 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Pop2Command(ExecutionState.getMethodContext()));
	}
}
