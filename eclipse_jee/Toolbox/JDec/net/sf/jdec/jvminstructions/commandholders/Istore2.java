package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Istore2Command;
import net.sf.jdec.util.ExecutionState;

public class Istore2 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Istore2Command(ExecutionState.getMethodContext()));
	}
}
