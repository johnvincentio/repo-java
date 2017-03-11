package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.PutstaticCommand;
import net.sf.jdec.util.ExecutionState;

public class Putstatic extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new PutstaticCommand(ExecutionState.getMethodContext()));
	}
}

