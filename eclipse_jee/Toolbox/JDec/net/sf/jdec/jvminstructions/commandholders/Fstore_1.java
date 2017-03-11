package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fstore_1Command;
import net.sf.jdec.util.ExecutionState;

public class Fstore_1 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fstore_1";
	}

	protected void registerCommand() {
		setCommand(new Fstore_1Command(ExecutionState.getMethodContext()));
	}

}
