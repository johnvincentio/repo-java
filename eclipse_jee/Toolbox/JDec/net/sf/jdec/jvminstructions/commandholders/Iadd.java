package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.IaddCommand;
import net.sf.jdec.util.ExecutionState;

public class Iadd extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new IaddCommand(ExecutionState.getMethodContext()));
	}
}
