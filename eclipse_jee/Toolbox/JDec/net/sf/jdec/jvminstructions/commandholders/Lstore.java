package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.LstoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Lstore extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new LstoreCommand(ExecutionState.getMethodContext()));
	}
}

