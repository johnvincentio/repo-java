package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fstore_0Command;
import net.sf.jdec.util.ExecutionState;

public class Fstore_0 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fstore_0";
	}

	protected void registerCommand() {
		setCommand(new Fstore_0Command(ExecutionState.getMethodContext()));
	}

}
