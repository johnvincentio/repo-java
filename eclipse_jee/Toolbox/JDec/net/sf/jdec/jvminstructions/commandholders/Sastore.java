package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.SastoreCommand;
import net.sf.jdec.util.ExecutionState;

public class Sastore extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new SastoreCommand(ExecutionState.getMethodContext()));
	}
}
