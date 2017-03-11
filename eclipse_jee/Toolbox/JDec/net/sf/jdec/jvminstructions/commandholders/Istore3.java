package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Istore3Command;
import net.sf.jdec.util.ExecutionState;

public class Istore3 extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new Istore3Command(ExecutionState.getMethodContext()));
	}
}
