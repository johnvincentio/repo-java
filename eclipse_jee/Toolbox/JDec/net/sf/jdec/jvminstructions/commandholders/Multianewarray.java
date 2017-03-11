package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.MultiAnewArrayCommand;
import net.sf.jdec.util.ExecutionState;

public class Multianewarray extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new MultiAnewArrayCommand(ExecutionState.getMethodContext()));
	}
}
