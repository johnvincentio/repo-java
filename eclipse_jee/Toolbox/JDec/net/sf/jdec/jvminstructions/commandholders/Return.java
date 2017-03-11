package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.ReturnCommand;
import net.sf.jdec.util.ExecutionState;

public class Return extends AbstractInstructionCommandHolder {

	protected void registerCommand() {
		setCommand(new ReturnCommand(ExecutionState.getMethodContext()));
	}
}
