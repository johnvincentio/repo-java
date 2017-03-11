package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Istore0Command;
import net.sf.jdec.util.ExecutionState;

public class Istore0 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Istore0Command(ExecutionState.getMethodContext()));
	}
}
