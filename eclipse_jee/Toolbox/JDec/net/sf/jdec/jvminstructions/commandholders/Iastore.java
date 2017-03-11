package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Iastore extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IastoreCommand(ExecutionState.getMethodContext()));
	}
}
