package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.PutfieldCommand;
import net.sf.jdec.util.ExecutionState;

public class PutField extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new PutfieldCommand(ExecutionState.getMethodContext()));
	}
}
