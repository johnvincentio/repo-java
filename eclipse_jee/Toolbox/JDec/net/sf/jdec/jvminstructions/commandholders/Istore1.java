package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Istore1Command;
import net.sf.jdec.util.ExecutionState;

public class Istore1 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Istore1Command(ExecutionState.getMethodContext()));
	}
}
