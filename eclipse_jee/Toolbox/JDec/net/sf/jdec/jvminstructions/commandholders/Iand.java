package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IandCommand;
import net.sf.jdec.util.ExecutionState;

public class Iand extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IandCommand(ExecutionState.getMethodContext()));
	}
}

