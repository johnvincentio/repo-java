package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IstoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Istore extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IstoreCommand(ExecutionState.getMethodContext()));
	}
}
