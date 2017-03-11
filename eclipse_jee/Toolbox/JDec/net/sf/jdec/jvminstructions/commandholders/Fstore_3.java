package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.Fstore_3Command;
import net.sf.jdec.util.ExecutionState;

public class Fstore_3 extends AbstractInstructionCommandHolder {

	protected String getName() {
		return "Fstore_3";
	}

	protected void registerCommand() {
		setCommand(new Fstore_3Command(ExecutionState.getMethodContext()));
	}

}
